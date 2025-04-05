package com.trueque_api.staff.controller;

import com.trueque_api.staff.dto.LoginRequestDTO;
import com.trueque_api.staff.dto.UserDTO;
import com.trueque_api.staff.service.UserService;
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
    public ResponseEntity<UserDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        UserDTO userDTO = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(userDTO);
    }
}