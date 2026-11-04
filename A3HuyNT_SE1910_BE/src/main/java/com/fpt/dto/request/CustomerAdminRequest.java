package com.fpt.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerAdminRequest {

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(max = 100, message = "Họ và tên tối đa 100 ký tự")
    private String customerFullName;

    private String telephone;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String emailAddress;

    private LocalDate customerBirthday;

    private Integer customerStatus; // 1: Active, 0: Inactive

    // Mật khẩu (bắt buộc khi Staff tạo mới, để trống khi Staff cập nhật nếu không đổi)
    private String password;
}
