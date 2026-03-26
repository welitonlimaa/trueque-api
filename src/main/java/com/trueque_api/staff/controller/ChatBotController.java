package com.trueque_api.staff.controller;

import com.trueque_api.staff.dto.ChatBotQueryRequestDTO;
import com.trueque_api.staff.dto.ChatBotQueryResponseDTO;
import com.trueque_api.staff.security.JwtUtil;
import com.trueque_api.staff.service.ChatBotService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai/chatbot")
public class ChatBotController {

    private final ChatBotService service;
    private final JwtUtil jwtUtil;

    public ChatBotController(ChatBotService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/query")
    public ChatBotQueryResponseDTO query(
            @RequestBody ChatBotQueryRequestDTO request,
            @RequestHeader("Authorization") String token
    ) {
        String authenticatedEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        return service.queryChatBot(request, authenticatedEmail);
    }
}