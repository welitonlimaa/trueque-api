package com.trueque_api.staff.controller;

import com.trueque_api.staff.dto.TradeOfferRequestDTO;
import com.trueque_api.staff.dto.TradeOfferResponseDTO;
import com.trueque_api.staff.security.JwtUtil;
import com.trueque_api.staff.service.TradeOfferService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tradeoffers")
public class TradeOfferController {

    private final TradeOfferService tradeOfferService;
    private final JwtUtil jwtUtil;

    public TradeOfferController(TradeOfferService tradeOfferService, JwtUtil jwtUtil) {
        this.tradeOfferService = tradeOfferService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/listing/{id}/offers")
    public ResponseEntity<List<TradeOfferResponseDTO>> getOffersForListing(@PathVariable UUID id,
                                                                        @RequestHeader("Authorization") String token) {
        String authenticatedEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        List<TradeOfferResponseDTO> offers = tradeOfferService.getOffersForListing(id, authenticatedEmail);
        return ResponseEntity.ok(offers);
    }

    @PostMapping
    public ResponseEntity<TradeOfferResponseDTO> createOffer(@Valid @RequestBody TradeOfferRequestDTO dto) {
        return ResponseEntity.ok(tradeOfferService.createOffer(dto));
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<TradeOfferResponseDTO> acceptOffer(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        return ResponseEntity.ok(tradeOfferService.acceptOffer(id, email));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<TradeOfferResponseDTO> rejectOffer(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        return ResponseEntity.ok(tradeOfferService.rejectOffer(id, email));
    }
}