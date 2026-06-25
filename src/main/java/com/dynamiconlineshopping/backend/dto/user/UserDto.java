package com.dynamiconlineshopping.backend.dto.user;

import lombok.*;

/**
 * UserDto - Data Transfer Object for User information
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String defaultDeliveryAddress;
    private String role;
}
