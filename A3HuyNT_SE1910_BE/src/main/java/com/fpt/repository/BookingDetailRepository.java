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
}
