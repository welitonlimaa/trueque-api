package com.trueque_api.staff.service;

import com.trueque_api.staff.dto.UserDataResponseDTO;
import com.trueque_api.staff.dto.AuthResponseDTO;
import com.trueque_api.staff.dto.PasswordUpdateDTO;
import com.trueque_api.staff.dto.UserDataRequestDTO;
import com.trueque_api.staff.exception.BadRequestException;
import com.trueque_api.staff.exception.EmailAlreadyExistsException;
import com.trueque_api.staff.exception.InvalidCredentialsException;
import com.trueque_api.staff.exception.NotFoundException;
import com.trueque_api.staff.exception.UnauthorizedAccessException;
import com.trueque_api.staff.model.User;
import com.trueque_api.staff.model.Role;
import com.trueque_api.staff.repository.RoleRepository;
import com.trueque_api.staff.repository.UserRepository;
import com.trueque_api.staff.security.JwtUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.UUID;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(
        UserRepository userRepository,
        RoleRepository roleRepository,
        PasswordEncoder passwordEncoder,
        JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponseDTO login(String email, String password) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Senha incorreta.");
        }

        String token = jwtUtil.generateToken(user);

        return AuthResponseDTO.builder()
                .name(user.getName())
                .email(user.getEmail())
                .token(token)
                .build();
    }

    public AuthResponseDTO register(UserDataRequestDTO userDataDTO) {

        String emailLower = userDataDTO.getEmail().toLowerCase();

        if (userRepository.findByEmail(emailLower).isPresent()) {
            throw new EmailAlreadyExistsException("Este email já está registrado.");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role padrão não encontrada."));

        User user = new User();
        user.setName(userDataDTO.getName());
        user.setPhone(userDataDTO.getPhone());
        user.setEmail(emailLower);
        user.setPassword(passwordEncoder.encode(userDataDTO.getPassword()));
        user.getRoles().add(userRole);

        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(savedUser);

        return AuthResponseDTO.builder()
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .token(token)
                .build();
    }
    

    public UserDataResponseDTO getById(UUID id, String authenticatedEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
        
        if (!user.getEmail().equals(authenticatedEmail)) {
            throw new UnauthorizedAccessException("Você não tem permissão para visualizar esses dados.");
        }
        
        return UserDataResponseDTO.builder()
            .id(user.getId())
            .email(user.getEmail())
            .name(user.getName())
            .phone(user.getPhone())
            .googleId(user.getGoogleId())
            .profilePicture(user.getProfilePicture())
            .city(user.getCity())
            .state(user.getState())
            .build();
    }

    public UserDataResponseDTO updateUserData(UUID id, User updatedData, String authenticatedEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
        
        if (!user.getEmail().equals(authenticatedEmail)) {
            throw new UnauthorizedAccessException("Você não tem permissão para modificar esses dados.");
        }
        
        user.setEmail(updatedData.getEmail());
        user.setName(updatedData.getName());
        user.setPhone(updatedData.getPhone());
        user.setProfilePicture(updatedData.getProfilePicture());
        user.setCity(updatedData.getCity());
        user.setState(updatedData.getState());
    
        User userUpdated = userRepository.save(user);

        return UserDataResponseDTO.builder()
            .id(userUpdated.getId())
            .email(userUpdated.getEmail())
            .name(userUpdated.getName())
            .phone(userUpdated.getPhone())
            .googleId(userUpdated.getGoogleId())
            .profilePicture(userUpdated.getProfilePicture())
            .city(userUpdated.getCity())
            .state(userUpdated.getState())
            .build();
    }
    
    public void updatePassword(UUID id, PasswordUpdateDTO passwordUpdateDTO, String authenticatedEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
        
        if (!user.getEmail().equals(authenticatedEmail)) {
            throw new UnauthorizedAccessException("Você não tem permissão para modificar a senha.");
        }
        
        if (!passwordEncoder.matches(passwordUpdateDTO.getOldPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Senha antiga incorreta.");
        }

        if (passwordEncoder.matches(passwordUpdateDTO.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("Senha igual a anterior.");
        }
        
        user.setPassword(passwordEncoder.encode(passwordUpdateDTO.getNewPassword()));
        userRepository.save(user);
    }
    
    public void delete(UUID id, String authenticatedEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
        
        if (!user.getEmail().equals(authenticatedEmail)) {
            throw new UnauthorizedAccessException("Você não tem permissão para deletar este usuário.");
        }
    
        userRepository.delete(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void addRoleToUser(UUID userId, String roleName, String authenticatedEmail) {
        userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new NotFoundException("Usuário autenticado não encontrado."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new NotFoundException("Role não encontrada."));

        user.getRoles().add(role);
    }
}