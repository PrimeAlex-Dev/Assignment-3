package com.fpt.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    @Builder.Default
    private String type = "Bearer";
    private String email;
    private String role; // ROLE_STAFF hoặc ROLE_CUSTOMER
    private Integer customerId; // Null nếu là Staff
    private String fullName;
}
