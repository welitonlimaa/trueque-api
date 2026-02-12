package com.trueque_api.staff.controller;

import com.trueque_api.staff.dto.ListingRequestDTO;
import com.trueque_api.staff.dto.ListingResponseDTO;
import com.trueque_api.staff.dto.StatusUpdateRequest;
import com.trueque_api.staff.service.ListingService;

import jakarta.validation.Valid;

import com.trueque_api.staff.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/listings")
public class ListingController {

    private final ListingService listingService;
    private final JwtUtil jwtUtil;

    public ListingController(ListingService listingService, JwtUtil jwtUtil) {
        this.listingService = listingService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/createlisting")
    public ResponseEntity<ListingResponseDTO> create(@Valid @RequestBody ListingRequestDTO listingRequestDTO,
                                                     @RequestHeader("Authorization") String token) {
        String authenticatedEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        ListingResponseDTO createdListing = listingService.createListing(listingRequestDTO, authenticatedEmail);
        return ResponseEntity.ok(createdListing);
    }

    @GetMapping("/")
    public ResponseEntity<List<ListingResponseDTO>> getAllListings() {
        List<ListingResponseDTO> listings = listingService.getAllListings();
        return ResponseEntity.ok(listings);
    }    

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<List<ListingResponseDTO>> getPendingListings() {
        List<ListingResponseDTO> listings = listingService.getPendingListings();
        return ResponseEntity.ok(listings);
    }   

    @GetMapping("/{id}")
    public ResponseEntity<ListingResponseDTO> getById(@PathVariable UUID id) {
        ListingResponseDTO listing = listingService.getListingById(id);
        return ResponseEntity.ok(listing);
    }

    @GetMapping("/my")
    public ResponseEntity<List<ListingResponseDTO>> listUserListings(@RequestHeader("Authorization") String token) {
        String authenticatedEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        List<ListingResponseDTO> listings = listingService.listUserListings(authenticatedEmail);
        return ResponseEntity.ok(listings);
    }

    @PatchMapping("/{id}/mark-as-exchanged")
    public ResponseEntity<Void> markAsExchanged(@PathVariable UUID id,
                                                @RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractEmail(token.replace("Bearer ", ""));
        listingService.markAsExchanged(id, email);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<ListingResponseDTO> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StatusUpdateRequest request
    ) {
        ListingResponseDTO listing =
            listingService.updateListingStatus(id, request.getStatus());

        return ResponseEntity.ok(listing);
    }
}