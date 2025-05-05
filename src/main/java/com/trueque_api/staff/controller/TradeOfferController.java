// package com.trueque_api.staff.controller;

// import com.trueque_api.staff.dto.TradeOfferRequestDTO;
// import com.trueque_api.staff.dto.TradeOfferResponseDTO;
// import com.trueque_api.staff.service.TradeOfferService;
// import com.trueque_api.staff.security.JwtUtil;

// import java.util.List;

// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// @RestController
// @RequestMapping("/tradeoffers")
// public class TradeOfferController {

//     private final TradeOfferService tradeOfferService;
//     private final JwtUtil jwtUtil;

//     public TradeOfferController(TradeOfferService tradeOfferService, JwtUtil jwtUtil) {
//         this.tradeOfferService = tradeOfferService;
//         this.jwtUtil = jwtUtil;
//     }

//     @PostMapping
//     public ResponseEntity<TradeOfferResponseDTO> create(@RequestBody TradeOfferRequestDTO offerRequestDTO,
//                                                         @RequestHeader("Authorization") String token) {
//         String authenticatedEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));
//         TradeOfferResponseDTO createdOffer = tradeOfferService.createTradeOffer(offerRequestDTO, authenticatedEmail);
//         return ResponseEntity.ok(createdOffer);
//     }

//     @GetMapping
//     public ResponseEntity<List<TradeOfferResponseDTO>> listUserTradeOffers(@RequestHeader("Authorization") String token) {
//         String authenticatedEmail = jwtUtil.extractEmail(token.replace("Bearer ", ""));
//         List<TradeOfferResponseDTO> tradeOffers = tradeOfferService.listUserTradeOffers(authenticatedEmail);
//         return ResponseEntity.ok(tradeOffers);
//     }
// }