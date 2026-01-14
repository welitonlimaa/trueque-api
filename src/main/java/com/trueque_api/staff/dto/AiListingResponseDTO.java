package com.trueque_api.staff.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiListingResponseDTO {
    private String title;
    private String description;
    private String category;
    private String condition;
}