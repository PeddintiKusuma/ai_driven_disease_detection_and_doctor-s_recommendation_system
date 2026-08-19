package com.aihealthcare.dto.response;

import com.aihealthcare.model.enums.Gender;
import com.aihealthcare.model.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private Integer age;
    private Gender gender;
    private Role role;
    private Boolean enabled;
}
