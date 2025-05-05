package com.trueque_api.staff.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class TradeOfferResponseDTO {
    private UUID id;
    private UUID offeredItemId;
    private UUID requestedItemId;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime acceptedAt;
}