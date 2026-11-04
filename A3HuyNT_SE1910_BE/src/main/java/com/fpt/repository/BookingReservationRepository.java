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

    // Tìm kiếm và lọc danh sách đặt phòng dành cho Staff
    @org.springframework.data.jpa.repository.Query("SELECT b FROM BookingReservation b WHERE " +
            "(:status IS NULL OR b.bookingStatus = :status) AND " +
            "(:startDate IS NULL OR b.bookingDate >= :startDate) AND " +
            "(:endDate IS NULL OR b.bookingDate <= :endDate) " +
            "ORDER BY b.bookingDate DESC")
    List<BookingReservation> searchBookings(
            @org.springframework.data.repository.query.Param("status") Integer status,
            @org.springframework.data.repository.query.Param("startDate") java.time.LocalDate startDate,
            @org.springframework.data.repository.query.Param("endDate") java.time.LocalDate endDate
    );
}
