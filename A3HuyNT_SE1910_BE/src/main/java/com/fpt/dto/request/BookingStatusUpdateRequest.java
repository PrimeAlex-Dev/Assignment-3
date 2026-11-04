package com.fpt.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingStatusUpdateRequest {

    @NotNull(message = "Trạng thái đặt phòng không được để trống")
    private Integer bookingStatus; // 1: Chờ duyệt/Xác nhận, 2: Hoàn thành, 3: Đã hủy
}
