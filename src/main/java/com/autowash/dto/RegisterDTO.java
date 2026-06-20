package com.autowash.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String fullName;
    private String email;
    private String username;
    private String password;
    private String phoneNumber;
}