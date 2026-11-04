package com.fpt.controller;

import com.fpt.dto.request.RoomRequest;
import com.fpt.dto.response.ApiResponse;
import com.fpt.dto.response.RoomResponse;
import com.fpt.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    // 1. Xem danh sách phòng (Public - Không cần đăng nhập theo yêu cầu đề bài)
    @GetMapping
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAllRooms(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer roomTypeId,
            @RequestParam(required = false) Integer status
    ) {
        List<RoomResponse> rooms = roomService.getAllRooms(keyword, roomTypeId, status);
        return ResponseEntity.ok(ApiResponse.success(rooms));
    }

    // 2. Xem chi tiết thông tin một phòng (Public)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomById(@PathVariable Integer id) {
        RoomResponse room = roomService.getRoomById(id);
        return ResponseEntity.ok(ApiResponse.success(room));
    }

    // 3. Thêm mới thông tin phòng (Chỉ Staff)
    @PostMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(@Valid @RequestBody RoomRequest request) {
        RoomResponse response = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Thêm mới phòng thành công", response));
    }

    // 4. Cập nhật thông tin phòng (Chỉ Staff)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(
            @PathVariable Integer id,
            @Valid @RequestBody RoomRequest request
    ) {
        RoomResponse response = roomService.updateRoom(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật phòng thành công", response));
    }

    // 5. Xóa thông tin phòng (Chỉ Staff)
    // Tự động kiểm tra: Chưa có giao dịch -> Hard Delete, Đã có giao dịch -> Soft Delete (đổi status)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<ApiResponse<String>> deleteRoom(@PathVariable Integer id) {
        String resultMessage = roomService.deleteRoom(id);
        return ResponseEntity.ok(ApiResponse.success(resultMessage, resultMessage));
    }
}
