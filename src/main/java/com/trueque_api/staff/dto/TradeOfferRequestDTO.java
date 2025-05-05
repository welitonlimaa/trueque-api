package com.trueque_api.staff.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TradeOfferRequestDTO {
    private UUID offeredItemId;
    private UUID requestedItemId;
}