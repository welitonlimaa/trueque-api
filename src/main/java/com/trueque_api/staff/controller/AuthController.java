package com.trueque_api.staff.controller;

import com.trueque_api.staff.dto.LoginRequestDTO;
import com.trueque_api.staff.dto.AuthResponseDTO;
import com.trueque_api.staff.service.UserService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        AuthResponseDTO authResponse = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(authResponse);
    }
}