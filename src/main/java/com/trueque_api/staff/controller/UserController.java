package com.trueque_api.staff.controller;

import com.trueque_api.staff.model.User;
import com.trueque_api.staff.dto.UserDTO;
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
    public ResponseEntity<UserDTO> register(@RequestBody User user) {
        UserDTO userDTO = userService.register(user);
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/{id}/data")
    public ResponseEntity<User> update(@PathVariable UUID id, @RequestBody User updatedUser,
                                       @RequestHeader("Authorization") String token) {
        String authenticatedEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));

        User updated = userService.updateUserData(id, updatedUser, authenticatedEmail);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable UUID id, 
                                            @RequestParam String oldPassword, 
                                            @RequestParam String newPassword,
                                            @RequestHeader("Authorization") String token) {
        String authenticatedEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));

        userService.updatePassword(id, oldPassword, newPassword, authenticatedEmail);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        String authenticatedEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));

        User user = userService.getById(id, authenticatedEmail);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        String authenticatedEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));

        userService.delete(id, authenticatedEmail);
        return ResponseEntity.noContent().build();
    }
}
