package com.aihealthcare.service;

import com.aihealthcare.dto.response.UserResponse;
import com.aihealthcare.exception.AppException;
import com.aihealthcare.model.entity.User;
import com.aihealthcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthService authService;

    public UserResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
        return authService.toUserResponse(user);
    }

    public UserResponse updateProfile(Long userId, User updates) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        if (updates.getFirstName() != null) user.setFirstName(updates.getFirstName());
        if (updates.getLastName() != null) user.setLastName(updates.getLastName());
        if (updates.getPhone() != null) user.setPhone(updates.getPhone());
        if (updates.getAge() != null) user.setAge(updates.getAge());
        if (updates.getGender() != null) user.setGender(updates.getGender());

        return authService.toUserResponse(userRepository.save(user));
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(authService::toUserResponse)
                .collect(Collectors.toList());
    }
}
