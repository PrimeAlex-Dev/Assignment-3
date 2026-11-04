package com.fpt.service;

import com.fpt.dto.request.LoginRequest;
import com.fpt.dto.request.RegisterRequest;
import com.fpt.dto.response.AuthResponse;
import com.fpt.entity.Customer;
import com.fpt.repository.CustomerRepository;
import com.fpt.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    @Value("${staff.email}")
    private String staffEmail;

    @Value("${staff.password}")
    private String staffPassword;

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    // Xử lý đăng nhập cho cả Staff (application.properties) và Customer (Database)
    public AuthResponse login(LoginRequest request) {
        String inputEmail = request.getEmail().trim();

        // 1. Kiểm tra tài khoản Staff từ application.properties
        if (staffEmail.equalsIgnoreCase(inputEmail)) {
            if (!staffPassword.equals(request.getPassword())) {
                throw new RuntimeException("Sai thông tin mật khẩu Staff");
            }

            // Tạo Token với ROLE_STAFF
            String token = jwtUtils.generateToken(staffEmail, "ROLE_STAFF");
            return AuthResponse.builder()
                    .token(token)
                    .type("Bearer")
                    .email(staffEmail)
                    .role("ROLE_STAFF")
                    .customerId(null)
                    .fullName("Hotel Staff")
                    .build();
        }

        // 2. Kiểm tra tài khoản Customer trong Database
        Customer customer = customerRepository.findByEmailAddress(inputEmail)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại trong hệ thống"));

        // Kiểm tra mật khẩu mã hóa BCrypt
        if (!passwordEncoder.matches(request.getPassword(), customer.getPassword())) {
            throw new RuntimeException("Mật khẩu không chính xác");
        }

        // Kiểm tra trạng thái tài khoản
        if (customer.getCustomerStatus() != null && customer.getCustomerStatus() == 0) {
            throw new RuntimeException("Tài khoản của bạn đã bị khóa hoặc ngừng hoạt động");
        }

        // Tạo Token với ROLE_CUSTOMER
        String token = jwtUtils.generateToken(customer.getEmailAddress(), "ROLE_CUSTOMER");

        return AuthResponse.builder()
                .token(token)
                .type("Bearer")
                .email(customer.getEmailAddress())
                .role("ROLE_CUSTOMER")
                .customerId(customer.getCustomerID())
                .fullName(customer.getCustomerFullName())
                .build();
    }

    // Đăng ký tài khoản Customer mới
    public Customer register(RegisterRequest request) {
        String inputEmail = request.getEmailAddress().trim();

        // Không cho phép trùng email với tài khoản Staff
        if (staffEmail.equalsIgnoreCase(inputEmail)) {
            throw new RuntimeException("Email này đã được sử dụng bởi hệ thống quản trị");
        }

        // Kiểm tra trùng email trong Database
        if (customerRepository.existsByEmailAddress(inputEmail)) {
            throw new RuntimeException("Email này đã được đăng ký tài khoản");
        }

        // Tạo Customer mới
        Customer customer = Customer.builder()
                .customerFullName(request.getCustomerFullName().trim())
                .telephone(request.getTelephone() != null ? request.getTelephone().trim() : null)
                .emailAddress(inputEmail)
                .customerBirthday(request.getCustomerBirthday())
                .customerStatus(1) // 1: Active
                .password(passwordEncoder.encode(request.getPassword())) // BCrypt hash
                .build();

        return customerRepository.save(customer);
    }
}
