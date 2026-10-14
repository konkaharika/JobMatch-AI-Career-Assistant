
package com.jobportal.controller;

import com.jobportal.dto.AuthDtos.AuthResponse;
import com.jobportal.dto.AuthDtos.LoginRequest;
import com.jobportal.dto.AuthDtos.RegisterRequest;
import com.jobportal.service.AuthService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public AuthResponse register(
            @Valid @RequestBody RegisterRequest request) {
        return service.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest request) {
        return service.login(request);
    }
}
