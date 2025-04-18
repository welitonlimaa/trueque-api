package com.trueque_api.staff.controller;

import com.trueque_api.staff.model.User;
import com.trueque_api.staff.dto.AuthResponseDTO;
import com.trueque_api.staff.dto.PasswordUpdateDTO;
import com.trueque_api.staff.dto.UserDataResponseDTO;
import com.trueque_api.staff.service.UserService;
import com.trueque_api.staff.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody User user) {
        AuthResponseDTO userDTO = userService.register(user);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDataResponseDTO> getById(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        String authenticatedEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));

        UserDataResponseDTO user = userService.getById(id, authenticatedEmail);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/data")
    public ResponseEntity<UserDataResponseDTO> update(@PathVariable UUID id, @RequestBody User updatedUser,
                                       @RequestHeader("Authorization") String token) {
        String authenticatedEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));

        UserDataResponseDTO updated = userService.updateUserData(id, updatedUser, authenticatedEmail);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable UUID id, 
                                            @RequestBody PasswordUpdateDTO passwordDataUpdated,
                                            @RequestHeader("Authorization") String token) {
        String authenticatedEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));

        userService.updatePassword(id, passwordDataUpdated, authenticatedEmail);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        String authenticatedEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));

        userService.delete(id, authenticatedEmail);
        return ResponseEntity.noContent().build();
    }
}
