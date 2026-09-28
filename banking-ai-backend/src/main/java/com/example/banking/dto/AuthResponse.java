package com.example.banking.dto;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuthResponse {
    private String token;
    private UserDto user;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UserDto {
        private Long id;
        private String name;
        private String email;
    }
}
