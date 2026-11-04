package com.fpt.service;

import com.fpt.dto.request.RoomTypeRequest;
import com.fpt.dto.response.RoomTypeResponse;
import com.fpt.entity.RoomType;
import com.fpt.repository.RoomTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;

    // Lấy toàn bộ danh sách loại phòng
    public List<RoomTypeResponse> getAllRoomTypes() {
        return roomTypeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Lấy thông tin chi tiết loại phòng theo ID
    public RoomTypeResponse getRoomTypeById(Integer id) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại phòng với ID: " + id));
        return mapToResponse(roomType);
    }

    // Thêm mới loại phòng
    public RoomTypeResponse createRoomType(RoomTypeRequest request) {
        RoomType roomType = RoomType.builder()
                .roomTypeName(request.getRoomTypeName().trim())
                .typeDescription(request.getTypeDescription())
                .typeNote(request.getTypeNote())
                .build();

        return mapToResponse(roomTypeRepository.save(roomType));
    }

    // Cập nhật loại phòng
    public RoomTypeResponse updateRoomType(Integer id, RoomTypeRequest request) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy loại phòng với ID: " + id));

        roomType.setRoomTypeName(request.getRoomTypeName().trim());
        roomType.setTypeDescription(request.getTypeDescription());
        roomType.setTypeNote(request.getTypeNote());

        return mapToResponse(roomTypeRepository.save(roomType));
    }

    // Chuyển đổi Entity sang Response DTO
    private RoomTypeResponse mapToResponse(RoomType roomType) {
        return RoomTypeResponse.builder()
                .roomTypeID(roomType.getRoomTypeID())
                .roomTypeName(roomType.getRoomTypeName())
                .typeDescription(roomType.getTypeDescription())
                .typeNote(roomType.getTypeNote())
                .build();
    }
}
