package com.fpt.controller;

import com.fpt.dto.request.LoginRequest;
import com.fpt.dto.request.RegisterRequest;
import com.fpt.dto.response.ApiResponse;
import com.fpt.dto.response.AuthResponse;
import com.fpt.entity.Customer;
import com.fpt.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // API Đăng nhập cho cả Staff và Customer
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", response));
    }

    // API Đăng ký tài khoản dành cho Customer
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Customer>> register(@Valid @RequestBody RegisterRequest request) {
        Customer customer = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đăng ký tài khoản thành công", customer));
    }
}
