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

    // Tìm kiếm nâng cao theo từ khóa, loại phòng và trạng thái
    @org.springframework.data.jpa.repository.Query("SELECT r FROM RoomInformation r WHERE " +
            "(:keyword IS NULL OR LOWER(r.roomNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(r.roomDetailDescription) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:roomTypeId IS NULL OR r.roomType.roomTypeID = :roomTypeId) AND " +
            "(:status IS NULL OR r.roomStatus = :status)")
    List<RoomInformation> searchRooms(
            @org.springframework.data.repository.query.Param("keyword") String keyword,
            @org.springframework.data.repository.query.Param("roomTypeId") Integer roomTypeId,
            @org.springframework.data.repository.query.Param("status") Integer status
    );
}
