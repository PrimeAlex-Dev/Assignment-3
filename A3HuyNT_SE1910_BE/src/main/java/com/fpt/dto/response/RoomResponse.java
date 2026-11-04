package com.fpt.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomResponse {

    private Integer roomID;
    private String roomNumber;
    private String roomDetailDescription;
    private Integer roomMaxCapacity;
    private Integer roomTypeID;
    private String roomTypeName;
    private Integer roomStatus; // 1: Hoạt động, 2: Ngưng hoạt động
    private BigDecimal roomPricePerDay;
}
