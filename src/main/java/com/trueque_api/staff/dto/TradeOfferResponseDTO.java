package com.trueque_api.staff.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record TradeOfferResponseDTO(
    UUID id,
    String status,
    LocalDateTime createdAt,
    LocalDateTime acceptedAt,
    LocalDateTime rejectedAt,
    UUID offeredListingId,
    UUID requestedListingId
) {}