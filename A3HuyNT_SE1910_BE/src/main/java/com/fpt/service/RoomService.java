package com.fpt.service;

import com.fpt.dto.request.RoomRequest;
import com.fpt.dto.response.RoomResponse;
import com.fpt.entity.RoomInformation;
import com.fpt.entity.RoomType;
import com.fpt.repository.BookingDetailRepository;
import com.fpt.repository.RoomInformationRepository;
import com.fpt.repository.RoomTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomInformationRepository roomInformationRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final BookingDetailRepository bookingDetailRepository;

    // Lấy danh sách phòng, có hỗ trợ tìm kiếm theo từ khóa, loại phòng, trạng thái
    public List<RoomResponse> getAllRooms(String keyword, Integer roomTypeId, Integer status) {
        String searchKey = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        List<RoomInformation> rooms = roomInformationRepository.searchRooms(searchKey, roomTypeId, status);

        return rooms.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Lấy thông tin chi tiết một phòng theo ID
    public RoomResponse getRoomById(Integer id) {
        RoomInformation room = roomInformationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với ID: " + id));
        return mapToResponse(room);
    }

    // Thêm mới phòng (Chỉ Staff)
    @Transactional
    public RoomResponse createRoom(RoomRequest request) {
        String roomNumber = request.getRoomNumber().trim();

        // Kiểm tra trùng số phòng
        if (roomInformationRepository.existsByRoomNumber(roomNumber)) {
            throw new RuntimeException("Số phòng " + roomNumber + " đã tồn tại trong hệ thống");
        }

        // Tìm loại phòng
        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeID())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại phòng với ID: " + request.getRoomTypeID()));

        RoomInformation room = RoomInformation.builder()
                .roomNumber(roomNumber)
                .roomDetailDescription(request.getRoomDetailDescription())
                .roomMaxCapacity(request.getRoomMaxCapacity())
                .roomType(roomType)
                .roomStatus(request.getRoomStatus() != null ? request.getRoomStatus() : 1) // 1: Active
                .roomPricePerDay(request.getRoomPricePerDay())
                .build();

        return mapToResponse(roomInformationRepository.save(room));
    }

    // Cập nhật thông tin phòng (Chỉ Staff)
    @Transactional
    public RoomResponse updateRoom(Integer id, RoomRequest request) {
        RoomInformation room = roomInformationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với ID: " + id));

        String roomNumber = request.getRoomNumber().trim();

        // Nếu thay đổi số phòng, kiểm tra xem số phòng mới đã có ai dùng chưa
        if (!room.getRoomNumber().equalsIgnoreCase(roomNumber) && roomInformationRepository.existsByRoomNumber(roomNumber)) {
            throw new RuntimeException("Số phòng " + roomNumber + " đã tồn tại trong hệ thống");
        }

        // Tìm loại phòng mới nếu có cập nhật
        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeID())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại phòng với ID: " + request.getRoomTypeID()));

        room.setRoomNumber(roomNumber);
        room.setRoomDetailDescription(request.getRoomDetailDescription());
        room.setRoomMaxCapacity(request.getRoomMaxCapacity());
        room.setRoomType(roomType);
        if (request.getRoomStatus() != null) {
            room.setRoomStatus(request.getRoomStatus());
        }
        room.setRoomPricePerDay(request.getRoomPricePerDay());

        return mapToResponse(roomInformationRepository.save(room));
    }

    /**
     * Nghiệp vụ đặc thù xóa phòng theo yêu cầu đề bài:
     * - Nếu phòng CHƯA TỪNG phát sinh trong giao dịch đặt phòng (BookingDetail): XÓA VẬT LÝ (Hard delete).
     * - Nếu phòng ĐÃ TỒN TẠI trong BookingDetail: CHUYỂN TRẠNG THÁI sang ngưng hoạt động (Soft delete).
     */
    @Transactional
    public String deleteRoom(Integer id) {
        RoomInformation room = roomInformationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với ID: " + id));

        // Kiểm tra lịch sử giao dịch trong bảng BookingDetail
        boolean hasBookingHistory = bookingDetailRepository.existsByRoomID(id);

        if (hasBookingHistory) {
            // Phòng đã có lịch sử thuê -> Chỉ cập nhật trạng thái RoomStatus = 2 (Soft Delete)
            room.setRoomStatus(2); // 2: Inactive/Deleted
            roomInformationRepository.save(room);
            return "Phòng số " + room.getRoomNumber() + " đã từng có giao dịch đặt phòng nên được chuyển sang trạng thái ngưng hoạt động (Soft Delete).";
        } else {
            // Phòng chưa có lịch sử thuê -> Xóa vật lý khỏi Database (Hard Delete)
            roomInformationRepository.delete(room);
            return "Xóa vĩnh viễn phòng số " + room.getRoomNumber() + " thành công (Hard Delete).";
        }
    }

    // Chuyển đổi Entity sang Response DTO
    private RoomResponse mapToResponse(RoomInformation room) {
        return RoomResponse.builder()
                .roomID(room.getRoomID())
                .roomNumber(room.getRoomNumber())
                .roomDetailDescription(room.getRoomDetailDescription())
                .roomMaxCapacity(room.getRoomMaxCapacity())
                .roomTypeID(room.getRoomType() != null ? room.getRoomType().getRoomTypeID() : null)
                .roomTypeName(room.getRoomType() != null ? room.getRoomType().getRoomTypeName() : null)
                .roomStatus(room.getRoomStatus())
                .roomPricePerDay(room.getRoomPricePerDay())
                .build();
    }
}
