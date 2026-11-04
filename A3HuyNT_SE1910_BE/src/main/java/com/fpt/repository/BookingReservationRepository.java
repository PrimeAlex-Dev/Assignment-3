package com.fpt.repository;

import com.fpt.entity.BookingReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingReservationRepository extends JpaRepository<BookingReservation, Integer> {

    // Lấy lịch sử đặt phòng của một khách hàng
    List<BookingReservation> findByCustomer_CustomerIDOrderByBookingDateDesc(Integer customerID);
}
