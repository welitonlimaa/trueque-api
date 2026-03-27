package com.trueque_api.staff.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SearchListingResponseDTO {
    private List<ListingResponseDTO> content;
    private int page;
    private int totalPages;
    private long totalElements;
}