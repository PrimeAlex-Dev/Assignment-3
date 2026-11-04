package com.fpt.service;

import com.fpt.dto.request.CustomerAdminRequest;
import com.fpt.dto.request.CustomerProfileUpdateRequest;
import com.fpt.dto.response.CustomerResponse;
import com.fpt.entity.Customer;
import com.fpt.repository.BookingReservationRepository;
import com.fpt.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final BookingReservationRepository bookingReservationRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${staff.email}")
    private String staffEmail;

    // =========================================================================
    // DÀNH CHO CUSTOMER (Quản lý Profile cá nhân)
    // =========================================================================

    // 1. Xem thông tin cá nhân của Customer đang đăng nhập
    public CustomerResponse getMyProfile(String email) {
        Customer customer = customerRepository.findByEmailAddress(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin khách hàng với email: " + email));
        return mapToResponse(customer);
    }

    // 2. Cập nhật thông tin cá nhân của Customer đang đăng nhập
    @Transactional
    public CustomerResponse updateMyProfile(String email, CustomerProfileUpdateRequest request) {
        Customer customer = customerRepository.findByEmailAddress(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin khách hàng với email: " + email));

        customer.setCustomerFullName(request.getCustomerFullName().trim());
        customer.setTelephone(request.getTelephone() != null ? request.getTelephone().trim() : null);
        customer.setCustomerBirthday(request.getCustomerBirthday());

        // Nếu người dùng nhập mật khẩu mới thì cập nhật
        if (StringUtils.hasText(request.getNewPassword())) {
            customer.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        return mapToResponse(customerRepository.save(customer));
    }

    // =========================================================================
    // DÀNH CHO STAFF (Quản lý toàn bộ danh sách Customer)
    // =========================================================================

    // 3. Lấy danh sách khách hàng (hỗ trợ tìm kiếm theo tên, email, sđt và lọc trạng thái)
    public List<CustomerResponse> getAllCustomers(String keyword, Integer status) {
        String searchKey = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        List<Customer> customers = customerRepository.searchCustomers(searchKey, status);

        return customers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 4. Lấy chi tiết thông tin một khách hàng theo ID
    public CustomerResponse getCustomerById(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + id));
        return mapToResponse(customer);
    }

    // 5. Staff thêm mới một khách hàng
    @Transactional
    public CustomerResponse createCustomer(CustomerAdminRequest request) {
        String inputEmail = request.getEmailAddress().trim();

        if (staffEmail.equalsIgnoreCase(inputEmail)) {
            throw new RuntimeException("Email này đã được sử dụng cho hệ thống quản trị Staff");
        }

        if (customerRepository.existsByEmailAddress(inputEmail)) {
            throw new RuntimeException("Email " + inputEmail + " đã được đăng ký trong hệ thống");
        }

        if (!StringUtils.hasText(request.getPassword()) || request.getPassword().length() < 6) {
            throw new RuntimeException("Mật khẩu phải từ 6 ký tự trở lên");
        }

        Customer customer = Customer.builder()
                .customerFullName(request.getCustomerFullName().trim())
                .telephone(request.getTelephone() != null ? request.getTelephone().trim() : null)
                .emailAddress(inputEmail)
                .customerBirthday(request.getCustomerBirthday())
                .customerStatus(request.getCustomerStatus() != null ? request.getCustomerStatus() : 1) // Mặc định 1: Active
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        return mapToResponse(customerRepository.save(customer));
    }

    // 6. Staff cập nhật thông tin khách hàng
    @Transactional
    public CustomerResponse updateCustomer(Integer id, CustomerAdminRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + id));

        String newEmail = request.getEmailAddress().trim();
        if (!customer.getEmailAddress().equalsIgnoreCase(newEmail)) {
            if (staffEmail.equalsIgnoreCase(newEmail)) {
                throw new RuntimeException("Email này đã được sử dụng cho hệ thống quản trị Staff");
            }
            if (customerRepository.existsByEmailAddress(newEmail)) {
                throw new RuntimeException("Email " + newEmail + " đã tồn tại trong hệ thống");
            }
            customer.setEmailAddress(newEmail);
        }

        customer.setCustomerFullName(request.getCustomerFullName().trim());
        customer.setTelephone(request.getTelephone() != null ? request.getTelephone().trim() : null);
        customer.setCustomerBirthday(request.getCustomerBirthday());
        if (request.getCustomerStatus() != null) {
            customer.setCustomerStatus(request.getCustomerStatus());
        }

        // Cập nhật mật khẩu nếu có gửi lên
        if (StringUtils.hasText(request.getPassword())) {
            if (request.getPassword().length() < 6) {
                throw new RuntimeException("Mật khẩu mới phải từ 6 ký tự trở lên");
            }
            customer.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return mapToResponse(customerRepository.save(customer));
    }

    // 7. Staff xóa khách hàng (Kiểm tra lịch sử đơn đặt phòng)
    @Transactional
    public String deleteCustomer(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng với ID: " + id));

        // Kiểm tra xem khách hàng này đã từng phát sinh đơn đặt phòng chưa
        boolean hasBookingHistory = bookingReservationRepository.existsByCustomer_CustomerID(id);

        if (hasBookingHistory) {
            // Đã có đơn đặt phòng -> Khóa/Chuyển trạng thái sang 0 (Soft Delete)
            customer.setCustomerStatus(0); // 0: Inactive/Locked
            customerRepository.save(customer);
            return "Khách hàng " + customer.getCustomerFullName() + " đã có lịch sử đặt phòng nên được chuyển sang trạng thái ngưng hoạt động (Soft Delete).";
        } else {
            // Chưa từng đặt phòng -> Xóa vật lý khỏi Database (Hard Delete)
            customerRepository.delete(customer);
            return "Xóa vĩnh viễn khách hàng " + customer.getCustomerFullName() + " thành công (Hard Delete).";
        }
    }

    // Chuyển Entity sang DTO ẩn mật khẩu
    private CustomerResponse mapToResponse(Customer customer) {
        return CustomerResponse.builder()
                .customerID(customer.getCustomerID())
                .customerFullName(customer.getCustomerFullName())
                .telephone(customer.getTelephone())
                .emailAddress(customer.getEmailAddress())
                .customerBirthday(customer.getCustomerBirthday())
                .customerStatus(customer.getCustomerStatus())
                .build();
    }
}
