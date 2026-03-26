package com.trueque_api.staff.dto;

import lombok.Getter;

@Getter
public class ChatBotQueryRequestDTO {
    private String index;
    private String minScore;
    private String question;
}