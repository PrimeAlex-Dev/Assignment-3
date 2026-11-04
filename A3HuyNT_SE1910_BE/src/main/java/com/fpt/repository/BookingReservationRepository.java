package com.fpt.repository;

import com.fpt.entity.BookingReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingReservationRepository extends JpaRepository<BookingReservation, Integer> {

    // Lấy lịch sử đặt phòng của một khách hàng
    List<BookingReservation> findByCustomer_CustomerIDOrderByBookingDateDesc(Integer customerID);

    // Kiểm tra xem khách hàng đã từng có đơn đặt phòng nào chưa
    boolean existsByCustomer_CustomerID(Integer customerID);
}
