package com.trueque_api.staff.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserDataRequestDTO {

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Formato de email inválido")
    private final String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private final String password;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private final String name;

    @NotBlank(message = "Telefone é obrigatório")
    @Size(max = 20, message = "Telefone deve ter no máximo 20 caracteres")
    private final String phone;

    private final String googleId;

    private final String profilePicture;

    @NotBlank(message = "Cidade é obrigatória")
    private final String city;

    @NotBlank(message = "Estado é obrigatório")
    private final String state;
}
