package com.trueque_api.staff.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class AiListingResponseDTO {
    private String title;
    private String description;
    private String category;
    private String condition;
}