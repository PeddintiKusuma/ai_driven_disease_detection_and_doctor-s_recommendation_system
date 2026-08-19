package com.aihealthcare.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private boolean success;
    private String token;
    private UserResponse user;
    private String message;
}
