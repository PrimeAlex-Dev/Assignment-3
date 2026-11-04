package com.fpt.controller;

import com.fpt.dto.request.CustomerAdminRequest;
import com.fpt.dto.request.CustomerProfileUpdateRequest;
import com.fpt.dto.response.ApiResponse;
import com.fpt.dto.response.CustomerResponse;
import com.fpt.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    // =========================================================================
    // DÀNH CHO KHÁCH HÀNG (CUSTOMER)
    // =========================================================================

    // 1. Khách hàng xem thông tin Profile cá nhân của mình
    @GetMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CustomerResponse>> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        CustomerResponse response = customerService.getMyProfile(email);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 2. Khách hàng cập nhật Profile cá nhân của mình
    @PutMapping("/profile")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody CustomerProfileUpdateRequest request
    ) {
        String email = authentication.getName();
        CustomerResponse response = customerService.updateMyProfile(email, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin cá nhân thành công", response));
    }

    // =========================================================================
    // DÀNH CHO NHÂN VIÊN (STAFF)
    // =========================================================================

    // 3. Staff xem toàn bộ danh sách khách hàng (có tìm kiếm & lọc trạng thái)
    @GetMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAllCustomers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status
    ) {
        List<CustomerResponse> list = customerService.getAllCustomers(keyword, status);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    // 4. Staff xem thông tin chi tiết một khách hàng theo ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(@PathVariable Integer id) {
        CustomerResponse response = customerService.getCustomerById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 5. Staff tạo mới một tài khoản khách hàng thủ công
    @PostMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(@Valid @RequestBody CustomerAdminRequest request) {
        CustomerResponse response = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Thêm mới khách hàng thành công", response));
    }

    // 6. Staff cập nhật thông tin khách hàng
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(
            @PathVariable Integer id,
            @Valid @RequestBody CustomerAdminRequest request
    ) {
        CustomerResponse response = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật khách hàng thành công", response));
    }

    // 7. Staff xóa khách hàng (Tự động kiểm tra: Chưa có booking -> Hard Delete; Đã có booking -> Soft Delete)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<ApiResponse<String>> deleteCustomer(@PathVariable Integer id) {
        String resultMessage = customerService.deleteCustomer(id);
        return ResponseEntity.ok(ApiResponse.success(resultMessage, resultMessage));
    }
}
