package com.trueque_api.staff.service;

import com.trueque_api.staff.dto.UserDTO;
import com.trueque_api.staff.exception.InvalidCredentialsException;
import com.trueque_api.staff.exception.UserNotFoundException;
import com.trueque_api.staff.model.User;
import com.trueque_api.staff.repository.UserRepository;
import com.trueque_api.staff.security.JwtUtil;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public UserDTO login(String email, String password) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Senha incorreta.");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return UserDTO.builder()
                .name(user.getName())
                .email(user.getEmail())
                .token(token)
                .build();
    }
}