// package com.trueque_api.staff.controller;

// import com.trueque_api.staff.dto.ListingRequestDTO;
// import com.trueque_api.staff.dto.ListingResponseDTO;
// import com.trueque_api.staff.dto.TradeOfferResponseDTO;
// import com.trueque_api.staff.service.ListingService;
// import com.trueque_api.staff.security.JwtUtil;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;
// import java.util.UUID;

// @RestController
// @RequestMapping("/listings")
// public class ListingController {

//     private final ListingService listingService;
//     private final JwtUtil jwtUtil;

//     public ListingController(ListingService listingService, JwtUtil jwtUtil) {
//         this.listingService = listingService;
//         this.jwtUtil = jwtUtil;
//     }

//     @PostMapping
//     public ResponseEntity<ListingResponseDTO> create(@RequestBody ListingRequestDTO listingRequestDTO,
//                                                      @RequestHeader("Authorization") String token) {
//         String authenticatedEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));
//         ListingResponseDTO createdListing = listingService.createListing(listingRequestDTO, authenticatedEmail);
//         return ResponseEntity.ok(createdListing);
//     }

//     @GetMapping("/{id}")
//     public ResponseEntity<ListingResponseDTO> getById(@PathVariable UUID id) {
//         ListingResponseDTO listing = listingService.getListingById(id);
//         return ResponseEntity.ok(listing);
//     }

//     @GetMapping
//     public ResponseEntity<List<ListingResponseDTO>> listUserListings(@RequestHeader("Authorization") String token) {
//         String authenticatedEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));
//         List<ListingResponseDTO> listings = listingService.listUserListings(authenticatedEmail);
//         return ResponseEntity.ok(listings);
//     }
// }
