package com.trueque_api.staff.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class ChatBotQueryResponseDTO {
    private String answer;
    private List<ChatBotSourceDTO> sources;
    private String question;
    private String model;
    private Integer tokensUsed;
}
