package com.trueque_api.staff.service;

import com.trueque_api.staff.dto.ChatBotQueryRequestDTO;
import com.trueque_api.staff.dto.ChatBotQueryResponseDTO;
import com.trueque_api.staff.exception.BadRequestException;
import com.trueque_api.staff.exception.NotFoundException;
import com.trueque_api.staff.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatBotService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Value("${chatbot.api-url}")
    private String chatbotApiUrl;

    public ChatBotService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.restTemplate = new RestTemplate();
    }
    
    public ChatBotQueryResponseDTO queryChatBot(ChatBotQueryRequestDTO dto) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<ChatBotQueryRequestDTO> request = new HttpEntity<>(dto, headers);

            ResponseEntity<ChatBotQueryResponseDTO> response = restTemplate.exchange(
                    chatbotApiUrl,
                    HttpMethod.POST,
                    request,
                    ChatBotQueryResponseDTO.class
            );

            return response.getBody();

        } catch (Exception e) {
            throw new BadRequestException("Erro ao consultar ChatBot service: " + e.getMessage());
        }
    }
}