package com.fpt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "Customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CustomerID")
    private Integer customerID;

    @Column(name = "CustomerFullName", nullable = false, length = 100)
    private String customerFullName;

    @Column(name = "Telephone", length = 20)
    private String telephone;

    @Column(name = "EmailAddress", nullable = false, unique = true, length = 100)
    private String emailAddress;

    @Column(name = "CustomerBirthday")
    private LocalDate customerBirthday;

    @Column(name = "CustomerStatus")
    private Integer customerStatus; // 1: Active, 0/2: Inactive

    @Column(name = "Password", nullable = false, length = 100)
    private String password;
}
