package com.fpt.controller;

import com.fpt.dto.request.BookingCreateRequest;
import com.fpt.dto.request.BookingStatusUpdateRequest;
import com.fpt.dto.response.ApiResponse;
import com.fpt.dto.response.BookingReservationResponse;
import com.fpt.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // =========================================================================
    // DÀNH CHO KHÁCH HÀNG (CUSTOMER)
    // =========================================================================

    // 1. Khách hàng tạo đơn đặt phòng trực tuyến (Đặt 1 hoặc nhiều phòng)
    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<BookingReservationResponse>> createBooking(
            Authentication authentication,
            @Valid @RequestBody BookingCreateRequest request
    ) {
        String customerEmail = authentication.getName();
        BookingReservationResponse response = bookingService.createBooking(customerEmail, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đặt phòng thành công", response));
    }

    // 2. Khách hàng xem lịch sử đơn đặt phòng của chính mình
    @GetMapping("/my-history")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<List<BookingReservationResponse>>> getMyBookingHistory(Authentication authentication) {
        String customerEmail = authentication.getName();
        List<BookingReservationResponse> history = bookingService.getMyBookingHistory(customerEmail);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    // =========================================================================
    // DÀNH CHO CẢ NHÂN VIÊN VÀ KHÁCH HÀNG
    // =========================================================================

    // 3. Xem chi tiết 1 đơn đặt phòng (Customer chỉ xem đơn của mình, Staff xem bất kỳ)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'CUSTOMER')")
    public ResponseEntity<ApiResponse<BookingReservationResponse>> getBookingById(
            @PathVariable Integer id,
            Authentication authentication
    ) {
        boolean isStaff = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"));

        BookingReservationResponse response = bookingService.getBookingById(id, authentication.getName(), isStaff);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // =========================================================================
    // DÀNH CHO NHÂN VIÊN (STAFF)
    // =========================================================================

    // 4. Staff xem toàn bộ danh sách đơn đặt phòng trong hệ thống
    @GetMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<ApiResponse<List<BookingReservationResponse>>> getAllBookings(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<BookingReservationResponse> list = bookingService.getAllBookings(status, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    // 5. Staff cập nhật trạng thái đơn đặt phòng (1: Xác nhận, 2: Hoàn thành, 3: Đã hủy)
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<ApiResponse<BookingReservationResponse>> updateBookingStatus(
            @PathVariable Integer id,
            @Valid @RequestBody BookingStatusUpdateRequest request
    ) {
        BookingReservationResponse response = bookingService.updateBookingStatus(id, request.getBookingStatus());
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái đơn đặt phòng thành công", response));
    }
}
