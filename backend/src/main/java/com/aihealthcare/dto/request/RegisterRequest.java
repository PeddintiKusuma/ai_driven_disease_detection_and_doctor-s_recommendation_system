package com.aihealthcare.dto.request;

import com.aihealthcare.model.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String phone;

    @Min(value = 1, message = "Age must be positive")
    @Max(value = 150, message = "Invalid age")
    private Integer age;

    private Gender gender;
}
