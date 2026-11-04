package com.fpt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "BookingReservation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BookingReservationID")
    private Integer bookingReservationID;

    @Column(name = "BookingDate", nullable = false)
    private LocalDate bookingDate;

    @Column(name = "TotalPrice", precision = 18, scale = 2)
    private BigDecimal totalPrice;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CustomerID", nullable = false)
    private Customer customer;

    @Column(name = "BookingStatus")
    private Integer bookingStatus; // 1: Pending, 2: Confirmed, 3: Cancelled

    @OneToMany(mappedBy = "bookingReservation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<BookingDetail> bookingDetails = new ArrayList<>();
}
