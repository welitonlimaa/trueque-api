package com.trueque_api.staff.controller;

import com.trueque_api.staff.model.User;
import com.trueque_api.staff.dto.AuthResponseDTO;
import com.trueque_api.staff.dto.PasswordUpdateDTO;
import com.trueque_api.staff.dto.UserDataRequestDTO;
import com.trueque_api.staff.dto.UserDataResponseDTO;
import com.trueque_api.staff.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

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

    @Operation(summary = "Registrar novo usuário")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário registrado com sucesso",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponseDTO.class))),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody UserDataRequestDTO userData) {
        AuthResponseDTO userDTO = userService.register(userData);
        return ResponseEntity.ok(userDTO);
    }

    @Operation(summary = "Buscar usuário pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserDataResponseDTO> getById(
            @Parameter(description = "ID do usuário") @PathVariable UUID id,
            @RequestHeader("Authorization") String token) {
        String authenticatedEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        UserDataResponseDTO user = userService.getById(id, authenticatedEmail);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Atualizar dados do usuário")
    @PutMapping("/{id}/data")
    public ResponseEntity<UserDataResponseDTO> update(
            @Parameter(description = "ID do usuário") @PathVariable UUID id,
            @Valid @RequestBody User updatedUserData,
            @RequestHeader("Authorization") String token) {
        String authenticatedEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        UserDataResponseDTO updated = userService.updateUserData(id, updatedUserData, authenticatedEmail);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Atualizar senha do usuário")
    @PutMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(
            @Parameter(description = "ID do usuário") @PathVariable UUID id,
            @Valid @RequestBody PasswordUpdateDTO passwordDataUpdated,
            @RequestHeader("Authorization") String token) {
        String authenticatedEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        userService.updatePassword(id, passwordDataUpdated, authenticatedEmail);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Deletar usuário")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID do usuário") @PathVariable UUID id,
            @RequestHeader("Authorization") String token) {
        String authenticatedEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        userService.delete(id, authenticatedEmail);
        return ResponseEntity.noContent().build();
    }
}