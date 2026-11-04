package com.fpt.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {

    private Integer customerID;
    private String customerFullName;
    private String telephone;
    private String emailAddress;
    private LocalDate customerBirthday;
    private Integer customerStatus; // 1: Hoạt động, 0: Khóa/Ngưng hoạt động
}
