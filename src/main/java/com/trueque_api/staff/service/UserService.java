package com.trueque_api.staff.service;

import com.trueque_api.staff.dto.UserDTO;
import com.trueque_api.staff.exception.EmailAlreadyExistsException;
import com.trueque_api.staff.exception.InvalidCredentialsException;
import com.trueque_api.staff.exception.UserNotFoundException;
import com.trueque_api.staff.exception.UnauthorizedAccessException;
import com.trueque_api.staff.model.User;
import com.trueque_api.staff.repository.UserRepository;
import com.trueque_api.staff.security.JwtUtil;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;


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

    public UserDTO register(User user) {
        if (userRepository.findByEmail(user.getEmail().toLowerCase()).isPresent()) {
            throw new EmailAlreadyExistsException("Este email já está registrado.");
        }

        user.setName(user.getName());
        user.setPhone(user.getPhone());
        user.setEmail(user.getEmail().toLowerCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(savedUser.getEmail());

        return UserDTO.builder()
            .email(savedUser.getEmail())
            .name(savedUser.getName())
            .token(token)
            .build();
    }

    public User updateUserData(UUID id, User updatedUser, String authenticatedEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));
        
        if (!user.getEmail().equals(authenticatedEmail)) {
            throw new UnauthorizedAccessException("Você não tem permissão para modificar esses dados.");
        }
        
        user.setName(updatedUser.getName());
        user.setPhone(updatedUser.getPhone());
        user.setCity(updatedUser.getCity());
        user.setState(updatedUser.getState());
    
        return userRepository.save(user);
    }
    
    public void updatePassword(UUID id, String oldPassword, String newPassword, String authenticatedEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));
        
        if (!user.getEmail().equals(authenticatedEmail)) {
            throw new UnauthorizedAccessException("Você não tem permissão para modificar a senha.");
        }
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Senha antiga incorreta.");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    public User getById(UUID id, String authenticatedEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));
        
        if (!user.getEmail().equals(authenticatedEmail)) {
            throw new UnauthorizedAccessException("Você não tem permissão para visualizar esses dados.");
        }
        
        return user;
    }
    
    public void delete(UUID id, String authenticatedEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado."));
        
        if (!user.getEmail().equals(authenticatedEmail)) {
            throw new UnauthorizedAccessException("Você não tem permissão para deletar este usuário.");
        }
    
        userRepository.delete(user);
    }
}