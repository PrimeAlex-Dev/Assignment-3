package com.fpt.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingCreateRequest {

    @NotEmpty(message = "Danh sách phòng đặt không được để trống")
    @Valid
    private List<BookingItemRequest> items;
}
