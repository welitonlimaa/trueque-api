package com.trueque_api.staff.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.trueque_api.staff.client.OpenAiClient;
import com.trueque_api.staff.dto.AiListingRequestDTO;
import com.trueque_api.staff.dto.AiListingResponseDTO;
import com.trueque_api.staff.exception.NotFoundException;
import com.trueque_api.staff.repository.UserRepository;

@Service
public class AiListingService {

    private final OpenAiClient openAiClient;
    private final UserRepository userRepository;

    public AiListingService(OpenAiClient openAiClient, UserRepository userRepository) {
        this.openAiClient = openAiClient;
        this.userRepository = userRepository;
    }

    public AiListingResponseDTO autofillListing(AiListingRequestDTO dto, String authenticatedEmail) {
        userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

        try {
            return openAiClient.analyzeImage(dto.getImageBase64());
        } catch (Exception e) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Não foi possível analisar a imagem"
            );
        }
    }
}
