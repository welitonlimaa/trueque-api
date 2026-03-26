package com.trueque_api.staff.dto;

import lombok.Getter;

@Getter
public class ChatBotSourceDTO {
    private String text;
    private String category;
    private String condition;
    private String type;
    private Double score;
}
