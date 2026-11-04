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

    // Tìm kiếm linh hoạt theo từ khóa (tên, email, số điện thoại) và trạng thái
    @org.springframework.data.jpa.repository.Query("SELECT c FROM Customer c WHERE " +
            "(:keyword IS NULL OR LOWER(c.customerFullName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.emailAddress) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(c.telephone) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:status IS NULL OR c.customerStatus = :status)")
    List<Customer> searchCustomers(
            @org.springframework.data.repository.query.Param("keyword") String keyword,
            @org.springframework.data.repository.query.Param("status") Integer status
    );
}
