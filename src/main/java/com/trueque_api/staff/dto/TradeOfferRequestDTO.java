package com.trueque_api.staff.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TradeOfferRequestDTO(
    @NotNull(message = "ID do anúncio oferecido é obrigatório")
    UUID offeredListingId,

    @NotNull(message = "ID do anúncio desejado é obrigatório")
    UUID requestedListingId
) {}