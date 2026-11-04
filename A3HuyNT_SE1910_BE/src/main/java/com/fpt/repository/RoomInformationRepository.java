package com.fpt.repository;

import com.fpt.entity.RoomInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomInformationRepository extends JpaRepository<RoomInformation, Integer> {

    // Tìm kiếm theo số phòng
    Optional<RoomInformation> findByRoomNumber(String roomNumber);

    // Kiểm tra số phòng đã tồn tại chưa
    boolean existsByRoomNumber(String roomNumber);

    // Lấy danh sách các phòng đang hoạt động (RoomStatus = 1)
    List<RoomInformation> findByRoomStatus(Integer roomStatus);

    // Tìm kiếm theo loại phòng
    List<RoomInformation> findByRoomType_RoomTypeID(Integer roomTypeID);
}
