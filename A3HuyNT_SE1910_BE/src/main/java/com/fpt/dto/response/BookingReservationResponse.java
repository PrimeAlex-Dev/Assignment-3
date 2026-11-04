package com.fpt.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingReservationResponse {

    private Integer bookingReservationID;
    private LocalDate bookingDate;
    private BigDecimal totalPrice;
    private Integer bookingStatus;
    private String bookingStatusText; // 1: Đã xác nhận / Chờ nhận phòng, 2: Hoàn thành, 3: Đã hủy

    // Thông tin khách hàng đặt phòng
    private Integer customerID;
    private String customerFullName;
    private String customerEmail;
    private String customerPhone;

    // Danh sách các phòng trong đơn đặt phòng
    private List<BookingDetailResponse> bookingDetails;
}
