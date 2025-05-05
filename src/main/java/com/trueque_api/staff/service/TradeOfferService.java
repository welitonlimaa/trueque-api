// package com.trueque_api.staff.service;

// import com.trueque_api.staff.dto.TradeOfferRequestDTO;
// import com.trueque_api.staff.dto.TradeOfferResponseDTO;
// import com.trueque_api.staff.exception.NotFoundException;
// import com.trueque_api.staff.model.Listing;
// import com.trueque_api.staff.model.TradeOffer;
// import com.trueque_api.staff.model.User;
// import com.trueque_api.staff.repository.ListingRepository;
// import com.trueque_api.staff.repository.TradeOfferRepository;
// import com.trueque_api.staff.repository.UserRepository;

// import java.util.List;

// import org.springframework.stereotype.Service;


// @Service
// public class TradeOfferService {

//     private final TradeOfferRepository tradeOfferRepository;
//     private final ListingRepository listingRepository;
//     private final UserRepository userRepository;

//     public TradeOfferService(TradeOfferRepository tradeOfferRepository, ListingRepository listingRepository, UserRepository userRepository) {
//         this.tradeOfferRepository = tradeOfferRepository;
//         this.listingRepository = listingRepository;
//         this.userRepository = userRepository;
//     }

//     public TradeOfferResponseDTO createTradeOffer(TradeOfferRequestDTO offerDTO, String authenticatedEmail) {
//         User offeredBy = userRepository.findByEmail(authenticatedEmail)
//                 .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

//         Listing offeredItem = listingRepository.findById(offerDTO.getOfferedItemId())
//                 .orElseThrow(() -> new NotFoundException("Item oferecido não encontrado."));

//         Listing requestedItem = listingRepository.findById(offerDTO.getRequestedItemId())
//                 .orElseThrow(() -> new NotFoundException("Item solicitado não encontrado."));

//         User receivedBy = requestedItem.getUser();

//         TradeOffer offer = new TradeOffer();
//         offer.setOfferedBy(offeredBy);
//         offer.setReceivedBy(receivedBy);
//         offer.setOfferedItem(offeredItem);
//         offer.setRequestedItem(requestedItem);

//         TradeOffer savedOffer = tradeOfferRepository.save(offer);

//         return toResponseDTO(savedOffer);
//     }

//     public List<TradeOfferResponseDTO> listUserTradeOffers(String authenticatedEmail) {
//         User user = userRepository.findByEmail(authenticatedEmail)
//                 .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
    
//         return tradeOfferRepository.findAllByOfferedByIdOrReceivedById(user.getId(), user.getId())
//                 .stream()
//                 .map(this::toResponseDTO)
//                 .toList();
//     }    

//     private TradeOfferResponseDTO toResponseDTO(TradeOffer offer) {
//         return TradeOfferResponseDTO.builder()
//                 .id(offer.getId())
//                 .offeredItemId(offer.getOfferedItem().getId())
//                 .requestedItemId(offer.getRequestedItem().getId())
//                 .status(offer.getStatus())
//                 .createdAt(offer.getCreatedAt())
//                 .acceptedAt(offer.getAcceptedAt())
//                 .build();
//     }
// }