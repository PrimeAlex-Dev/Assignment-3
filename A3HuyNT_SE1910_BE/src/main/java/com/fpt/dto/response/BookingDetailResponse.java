package com.fpt.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDetailResponse {

    private Integer roomID;
    private String roomNumber;
    private String roomTypeName;
    private Integer roomMaxCapacity;
    private LocalDate startDate;
    private LocalDate endDate;
    private long numberOfDays;
    private BigDecimal pricePerDay;
    private BigDecimal actualPrice;
}
