package com.fpt.config;

import com.fpt.entity.Customer;
import com.fpt.entity.RoomInformation;
import com.fpt.entity.RoomType;
import com.fpt.repository.CustomerRepository;
import com.fpt.repository.RoomInformationRepository;
import com.fpt.repository.RoomTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomInformationRepository roomInformationRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // 1. Khởi tạo Loại phòng mẫu (RoomType) nếu chưa có
        if (roomTypeRepository.count() == 0) {
            RoomType standard = roomTypeRepository.save(RoomType.builder()
                    .roomTypeName("Standard Room")
                    .typeDescription("Phòng tiêu chuẩn đơn giản, đầy đủ tiện nghi cơ bản")
                    .typeNote("Dành cho 1-2 người")
                    .build());

            RoomType deluxe = roomTypeRepository.save(RoomType.builder()
                    .roomTypeName("Deluxe Room")
                    .typeDescription("Phòng cao cấp với tầm nhìn đẹp và trang thiết bị hiện đại")
                    .typeNote("Dành cho 2-3 người")
                    .build());

            RoomType suite = roomTypeRepository.save(RoomType.builder()
                    .roomTypeName("Suite Room")
                    .typeDescription("Phòng thượng hạng có phòng khách riêng biệt")
                    .typeNote("Dành cho gia đình hoặc khách VIP")
                    .build());

            // 2. Khởi tạo Phòng mẫu (RoomInformation)
            if (roomInformationRepository.count() == 0) {
                roomInformationRepository.save(RoomInformation.builder()
                        .roomNumber("101")
                        .roomDetailDescription("Phòng 101 tầng 1, hướng vườn, 1 giường đôi")
                        .roomMaxCapacity(2)
                        .roomType(standard)
                        .roomStatus(1) // 1: Hoạt động
                        .roomPricePerDay(BigDecimal.valueOf(350000))
                        .build());

                roomInformationRepository.save(RoomInformation.builder()
                        .roomNumber("102")
                        .roomDetailDescription("Phòng 102 tầng 1, 2 giường đơn")
                        .roomMaxCapacity(2)
                        .roomType(standard)
                        .roomStatus(1)
                        .roomPricePerDay(BigDecimal.valueOf(350000))
                        .build());

                roomInformationRepository.save(RoomInformation.builder()
                        .roomNumber("201")
                        .roomDetailDescription("Phòng 201 tầng 2, ban công hướng biển")
                        .roomMaxCapacity(3)
                        .roomType(deluxe)
                        .roomStatus(1)
                        .roomPricePerDay(BigDecimal.valueOf(650000))
                        .build());

                roomInformationRepository.save(RoomInformation.builder()
                        .roomNumber("301")
                        .roomDetailDescription("Phòng 301 tầng 3, căn hộ tổng thống")
                        .roomMaxCapacity(4)
                        .roomType(suite)
                        .roomStatus(1)
                        .roomPricePerDay(BigDecimal.valueOf(1200000))
                        .build());
            }
        }

        // 3. Khởi tạo Khách hàng mẫu (Customer) nếu chưa có
        if (customerRepository.count() == 0) {
            customerRepository.save(Customer.builder()
                    .customerFullName("Nguyen Van A")
                    .telephone("0912345678")
                    .emailAddress("customer@gmail.com")
                    .customerBirthday(LocalDate.of(2000, 1, 15))
                    .customerStatus(1) // 1: Active
                    .password(passwordEncoder.encode("123456")) // Mật khẩu mẫu: 123456
                    .build());
        }
    }
}
