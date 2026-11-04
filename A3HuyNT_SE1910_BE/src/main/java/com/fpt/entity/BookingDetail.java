package com.fpt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "BookingDetail")
@IdClass(BookingDetailId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDetail {

    @Id
    @Column(name = "BookingReservationID")
    private Integer bookingReservationID;

    @Id
    @Column(name = "RoomID")
    private Integer roomID;

    @Column(name = "StartDate", nullable = false)
    private LocalDate startDate;

    @Column(name = "EndDate", nullable = false)
    private LocalDate endDate;

    @Column(name = "ActualPrice", precision = 18, scale = 2)
    private BigDecimal actualPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BookingReservationID", insertable = false, updatable = false)
    @JsonIgnore
    private BookingReservation bookingReservation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "RoomID", insertable = false, updatable = false)
    private RoomInformation roomInformation;
}
