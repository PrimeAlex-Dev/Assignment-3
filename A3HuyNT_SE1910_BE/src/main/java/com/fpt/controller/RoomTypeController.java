package com.fpt.controller;

import com.fpt.dto.request.RoomTypeRequest;
import com.fpt.dto.response.ApiResponse;
import com.fpt.dto.response.RoomTypeResponse;
import com.fpt.service.RoomTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room-types")
@RequiredArgsConstructor
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    // 1. Lấy danh sách loại phòng (Public - Không cần đăng nhập)
    @GetMapping
    public ResponseEntity<ApiResponse<List<RoomTypeResponse>>> getAllRoomTypes() {
        List<RoomTypeResponse> list = roomTypeService.getAllRoomTypes();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    // 2. Lấy chi tiết một loại phòng (Public)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomTypeResponse>> getRoomTypeById(@PathVariable Integer id) {
        RoomTypeResponse response = roomTypeService.getRoomTypeById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 3. Thêm mới loại phòng (Chỉ Staff)
    @PostMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<ApiResponse<RoomTypeResponse>> createRoomType(@Valid @RequestBody RoomTypeRequest request) {
        RoomTypeResponse response = roomTypeService.createRoomType(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Thêm loại phòng thành công", response));
    }

    // 4. Cập nhật loại phòng (Chỉ Staff)
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<ApiResponse<RoomTypeResponse>> updateRoomType(
            @PathVariable Integer id,
            @Valid @RequestBody RoomTypeRequest request
    ) {
        RoomTypeResponse response = roomTypeService.updateRoomType(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật loại phòng thành công", response));
    }
}
