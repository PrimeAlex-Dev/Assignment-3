package com.fpt.service;

import com.fpt.dto.request.BookingCreateRequest;
import com.fpt.dto.request.BookingItemRequest;
import com.fpt.dto.response.BookingDetailResponse;
import com.fpt.dto.response.BookingReservationResponse;
import com.fpt.entity.BookingDetail;
import com.fpt.entity.BookingReservation;
import com.fpt.entity.Customer;
import com.fpt.entity.RoomInformation;
import com.fpt.repository.BookingDetailRepository;
import com.fpt.repository.BookingReservationRepository;
import com.fpt.repository.CustomerRepository;
import com.fpt.repository.RoomInformationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingReservationRepository bookingReservationRepository;
    private final BookingDetailRepository bookingDetailRepository;
    private final RoomInformationRepository roomInformationRepository;
    private final CustomerRepository customerRepository;

    // =========================================================================
    // DÀNH CHO CUSTOMER
    // =========================================================================

    /**
     * Khách hàng tạo đơn đặt phòng trực tuyến (Đặt 1 hoặc nhiều phòng)
     */
    @Transactional
    public BookingReservationResponse createBooking(String customerEmail, BookingCreateRequest request) {
        // 1. Kiểm tra khách hàng
        Customer customer = customerRepository.findByEmailAddress(customerEmail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với email: " + customerEmail));

        if (customer.getCustomerStatus() != null && customer.getCustomerStatus() == 0) {
            throw new RuntimeException("Tài khoản khách hàng của bạn đã bị vô hiệu hóa");
        }

        List<BookingItemRequest> items = request.getItems();
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("Đơn đặt phòng phải có ít nhất 1 phòng");
        }

        LocalDate today = LocalDate.now();
        BigDecimal grandTotal = BigDecimal.ZERO;

        // Danh sách chuẩn bị để lưu sau khi đã validate toàn bộ
        List<RoomInformation> validatedRooms = new ArrayList<>();
        List<BigDecimal> itemPrices = new ArrayList<>();
        List<Long> itemDaysList = new ArrayList<>();

        // 2. Validate từng phòng được chọn
        for (int i = 0; i < items.size(); i++) {
            BookingItemRequest item = items.get(i);

            // Kiểm tra ngày hợp lệ
            if (item.getStartDate() == null || item.getEndDate() == null) {
                throw new RuntimeException("Ngày nhận phòng và ngày trả phòng không được để trống");
            }
            if (!item.getStartDate().isBefore(item.getEndDate())) {
                throw new RuntimeException("Ngày trả phòng phải sau ngày nhận phòng");
            }
            if (item.getStartDate().isBefore(today)) {
                throw new RuntimeException("Ngày nhận phòng (" + item.getStartDate() + ") không được ở trong quá khứ");
            }

            // Kiểm tra trùng phòng trong chính request gửi lên
            for (int j = i + 1; j < items.size(); j++) {
                BookingItemRequest other = items.get(j);
                if (item.getRoomID().equals(other.getRoomID())) {
                    // Nếu cùng 1 phòng, kiểm tra có bị trùng ngày không
                    if (item.getStartDate().isBefore(other.getEndDate()) && item.getEndDate().isAfter(other.getStartDate())) {
                        throw new RuntimeException("Phòng ID " + item.getRoomID() + " bị trùng khoảng thời gian trong chính đơn đặt phòng này");
                    }
                }
            }

            // Kiểm tra phòng có tồn tại và đang hoạt động không
            RoomInformation room = roomInformationRepository.findById(item.getRoomID())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với ID: " + item.getRoomID()));

            if (room.getRoomStatus() != null && room.getRoomStatus() != 1) {
                throw new RuntimeException("Phòng số " + room.getRoomNumber() + " hiện không sẵn sàng để đặt (đang bảo trì hoặc ngừng kinh doanh)");
            }

            // Kiểm tra xem phòng có bị trùng lịch với đơn đặt phòng khác đang hoạt động không
            boolean alreadyBooked = bookingDetailRepository.isRoomBooked(room.getRoomID(), item.getStartDate(), item.getEndDate());
            if (alreadyBooked) {
                throw new RuntimeException("Phòng số " + room.getRoomNumber() + " đã có người đặt trong khoảng thời gian từ "
                        + item.getStartDate() + " đến " + item.getEndDate());
            }

            // Tính số ngày và giá phòng thực tế
            long days = ChronoUnit.DAYS.between(item.getStartDate(), item.getEndDate());
            if (days <= 0) days = 1;

            BigDecimal roomTotal = room.getRoomPricePerDay().multiply(BigDecimal.valueOf(days));
            grandTotal = grandTotal.add(roomTotal);

            validatedRooms.add(room);
            itemPrices.add(roomTotal);
            itemDaysList.add(days);
        }

        // 3. Tạo bản ghi BookingReservation
        BookingReservation reservation = BookingReservation.builder()
                .bookingDate(today)
                .totalPrice(grandTotal)
                .customer(customer)
                .bookingStatus(1) // 1: Đã xác nhận / Hoạt động
                .build();

        BookingReservation savedReservation = bookingReservationRepository.save(reservation);

        // 4. Tạo các bản ghi BookingDetail
        List<BookingDetail> savedDetails = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            BookingItemRequest item = items.get(i);
            RoomInformation room = validatedRooms.get(i);
            BigDecimal actualPrice = itemPrices.get(i);

            BookingDetail detail = BookingDetail.builder()
                    .bookingReservationID(savedReservation.getBookingReservationID())
                    .roomID(room.getRoomID())
                    .startDate(item.getStartDate())
                    .endDate(item.getEndDate())
                    .actualPrice(actualPrice)
                    .roomInformation(room)
                    .bookingReservation(savedReservation)
                    .build();

            savedDetails.add(bookingDetailRepository.save(detail));
        }

        savedReservation.setBookingDetails(savedDetails);
        return mapToResponse(savedReservation);
    }

    /**
     * Khách hàng xem lịch sử đơn đặt phòng của mình
     */
    public List<BookingReservationResponse> getMyBookingHistory(String customerEmail) {
        Customer customer = customerRepository.findByEmailAddress(customerEmail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với email: " + customerEmail));

        List<BookingReservation> list = bookingReservationRepository
                .findByCustomer_CustomerIDOrderByBookingDateDesc(customer.getCustomerID());

        return list.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // =========================================================================
    // DÀNH CHO CẢ STAFF VÀ CUSTOMER
    // =========================================================================

    /**
     * Xem chi tiết 1 đơn đặt phòng (Customer chỉ xem đơn của mình, Staff xem bất kỳ)
     */
    public BookingReservationResponse getBookingById(Integer id, String currentUserEmail, boolean isStaff) {
        BookingReservation reservation = bookingReservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt phòng với ID: " + id));

        // Nếu là Customer, kiểm tra xem đơn này có đúng là của họ không
        if (!isStaff) {
            if (!reservation.getCustomer().getEmailAddress().equalsIgnoreCase(currentUserEmail)) {
                throw new RuntimeException("Bạn không có quyền truy cập vào đơn đặt phòng của người khác");
            }
        }

        return mapToResponse(reservation);
    }

    // =========================================================================
    // DÀNH CHO STAFF
    // =========================================================================

    /**
     * Staff xem toàn bộ danh sách đơn đặt phòng kèm bộ lọc trạng thái và khoảng ngày
     */
    public List<BookingReservationResponse> getAllBookings(Integer status, LocalDate startDate, LocalDate endDate) {
        List<BookingReservation> list = bookingReservationRepository.searchBookings(status, startDate, endDate);
        return list.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Staff cập nhật trạng thái đơn đặt phòng (1: Xác nhận, 2: Hoàn thành, 3: Đã hủy)
     */
    @Transactional
    public BookingReservationResponse updateBookingStatus(Integer id, Integer newStatus) {
        BookingReservation reservation = bookingReservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt phòng với ID: " + id));

        if (newStatus < 1 || newStatus > 3) {
            throw new RuntimeException("Trạng thái đặt phòng không hợp lệ (1: Xác nhận, 2: Hoàn thành, 3: Đã hủy)");
        }

        reservation.setBookingStatus(newStatus);
        return mapToResponse(bookingReservationRepository.save(reservation));
    }

    // =========================================================================
    // HÀM CHUYỂN ĐỔI ENTITY SANG DTO
    // =========================================================================
    private BookingReservationResponse mapToResponse(BookingReservation reservation) {
        List<BookingDetail> details = reservation.getBookingDetails();
        if (details == null || details.isEmpty()) {
            details = bookingDetailRepository.findByBookingReservationID(reservation.getBookingReservationID());
        }

        List<BookingDetailResponse> detailResponses = details.stream().map(detail -> {
            RoomInformation room = detail.getRoomInformation();
            if (room == null && detail.getRoomID() != null) {
                room = roomInformationRepository.findById(detail.getRoomID()).orElse(null);
            }

            long days = ChronoUnit.DAYS.between(detail.getStartDate(), detail.getEndDate());
            if (days <= 0) days = 1;

            return BookingDetailResponse.builder()
                    .roomID(detail.getRoomID())
                    .roomNumber(room != null ? room.getRoomNumber() : "N/A")
                    .roomTypeName(room != null && room.getRoomType() != null ? room.getRoomType().getRoomTypeName() : "N/A")
                    .roomMaxCapacity(room != null ? room.getRoomMaxCapacity() : null)
                    .startDate(detail.getStartDate())
                    .endDate(detail.getEndDate())
                    .numberOfDays(days)
                    .pricePerDay(room != null ? room.getRoomPricePerDay() : null)
                    .actualPrice(detail.getActualPrice())
                    .build();
        }).collect(Collectors.toList());

        String statusText;
        switch (reservation.getBookingStatus() != null ? reservation.getBookingStatus() : 1) {
            case 2 -> statusText = "Đã hoàn thành";
            case 3 -> statusText = "Đã hủy";
            default -> statusText = "Đã xác nhận";
        }

        Customer customer = reservation.getCustomer();

        return BookingReservationResponse.builder()
                .bookingReservationID(reservation.getBookingReservationID())
                .bookingDate(reservation.getBookingDate())
                .totalPrice(reservation.getTotalPrice())
                .bookingStatus(reservation.getBookingStatus())
                .bookingStatusText(statusText)
                .customerID(customer != null ? customer.getCustomerID() : null)
                .customerFullName(customer != null ? customer.getCustomerFullName() : null)
                .customerEmail(customer != null ? customer.getEmailAddress() : null)
                .customerPhone(customer != null ? customer.getTelephone() : null)
                .bookingDetails(detailResponses)
                .build();
    }
}
