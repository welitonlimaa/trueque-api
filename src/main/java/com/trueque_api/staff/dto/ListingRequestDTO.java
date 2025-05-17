package com.trueque_api.staff.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ListingRequestDTO {

    @NotBlank(message = "Título é obrigatório")
    @Size(max = 150, message = "Título deve ter no máximo 100 caracteres")
    private String title;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
    private String description;

    @NotBlank(message = "Categoria é obrigatória")
    private String category;

    @NotBlank(message = "Condição é obrigatória")
    private String condition;

    @NotBlank(message = "Cidade é obrigatória")
    private String city;

    @NotBlank(message = "Estado é obrigatório")
    private String state;

    @NotEmpty(message = "É necessário informar ao menos uma imagem")
    private List<@NotBlank(message = "URL da imagem não pode estar vazia") String> images;
}