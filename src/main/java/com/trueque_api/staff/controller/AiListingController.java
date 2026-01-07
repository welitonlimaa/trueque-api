package com.trueque_api.staff.controller;

import com.trueque_api.staff.dto.AiListingRequestDTO;
import com.trueque_api.staff.dto.AiListingResponseDTO;
import com.trueque_api.staff.security.JwtUtil;
import com.trueque_api.staff.service.AiListingService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai/listings")
public class AiListingController {

    private final AiListingService service;
    private final JwtUtil jwtUtil;

    public AiListingController(AiListingService service, JwtUtil jwtUtil) {
        this.service = service;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/autofill")
    public AiListingResponseDTO autofill(
            @RequestBody AiListingRequestDTO request,
            @RequestHeader("Authorization") String token
    ) {
        String authenticatedEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        return service.autofillListing(request, authenticatedEmail);
    }
}