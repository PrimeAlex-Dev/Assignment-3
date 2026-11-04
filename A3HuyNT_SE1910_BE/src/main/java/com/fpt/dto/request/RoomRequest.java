package com.fpt.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomRequest {

    @NotBlank(message = "Số phòng không được để trống")
    private String roomNumber;

    private String roomDetailDescription;

    @NotNull(message = "Sức chứa tối đa không được để trống")
    @Min(value = 1, message = "Sức chứa tối đa ít nhất là 1 người")
    private Integer roomMaxCapacity;

    @NotNull(message = "Loại phòng không được để trống")
    private Integer roomTypeID;

    private Integer roomStatus; // 1: Hoạt động, 2: Ngưng hoạt động/Đã xóa

    @NotNull(message = "Giá phòng theo ngày không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phòng phải lớn hơn 0")
    private BigDecimal roomPricePerDay;
}
