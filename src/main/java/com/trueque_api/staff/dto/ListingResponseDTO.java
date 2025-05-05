package com.trueque_api.staff.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class ListingResponseDTO {
    private UUID id;
    private String title;
    private String description;
    private String category;
    private String condition;
    private List<String> images;
    private String city;
    private String state;
    private String status;
    private LocalDateTime createdAt;
}