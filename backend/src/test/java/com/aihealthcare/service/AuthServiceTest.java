package com.aihealthcare.service;

import com.aihealthcare.dto.request.RegisterRequest;
import com.aihealthcare.dto.response.AuthResponse;
import com.aihealthcare.model.enums.Gender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Test
    void testRegistration() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("testuser@example.com");
        request.setPassword("Test@123");
        request.setFirstName("Test");
        request.setLastName("User");
        request.setAge(25);
        request.setGender(Gender.MALE);

        AuthResponse response = authService.register(request);
        assertTrue(response.isSuccess());
        assertNotNull(response.getToken());
        assertEquals("testuser@example.com", response.getUser().getEmail());
    }

    @Test
    void testDuplicateRegistration() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("duplicate@example.com");
        request.setPassword("Test@123");
        request.setFirstName("Test");
        request.setLastName("User");

        authService.register(request);
        assertThrows(Exception.class, () -> authService.register(request));
    }
}
