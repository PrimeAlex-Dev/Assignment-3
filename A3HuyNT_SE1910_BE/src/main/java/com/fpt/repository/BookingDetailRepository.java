package com.fpt.repository;

import com.fpt.entity.BookingDetail;
import com.fpt.entity.BookingDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingDetailRepository extends JpaRepository<BookingDetail, BookingDetailId> {

    // Kiểm tra xem phòng có tồn tại trong bất kỳ giao dịch đặt phòng nào chưa (Dùng cho logic xóa phòng)
    boolean existsByRoomID(Integer roomID);

    // Lấy chi tiết đặt phòng theo mã đơn
    List<BookingDetail> findByBookingReservationID(Integer bookingReservationID);

    // Kiểm tra xem phòng có bị trùng lịch với đơn đặt phòng nào đang hoạt động không (BookingStatus != 3: Đã hủy)
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(b) > 0 FROM BookingDetail b " +
            "WHERE b.roomID = :roomID " +
            "AND b.bookingReservation.bookingStatus != 3 " +
            "AND b.startDate < :endDate AND b.endDate > :startDate")
    boolean isRoomBooked(
            @org.springframework.data.repository.query.Param("roomID") Integer roomID,
            @org.springframework.data.repository.query.Param("startDate") java.time.LocalDate startDate,
            @org.springframework.data.repository.query.Param("endDate") java.time.LocalDate endDate
    );
}
