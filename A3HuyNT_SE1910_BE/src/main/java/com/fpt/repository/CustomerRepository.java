package com.fpt.repository;

import com.fpt.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    // Tìm kiếm khách hàng theo email
    Optional<Customer> findByEmailAddress(String emailAddress);

    // Kiểm tra trùng email
    boolean existsByEmailAddress(String emailAddress);

    // Tìm kiếm khách hàng theo họ tên (chứa ký tự không phân biệt hoa thường)
    List<Customer> findByCustomerFullNameContainingIgnoreCase(String name);
}
