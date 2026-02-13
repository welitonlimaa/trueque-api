package com.trueque_api.staff.service;

import com.trueque_api.staff.dto.TradeOfferRequestDTO;
import com.trueque_api.staff.dto.TradeOfferResponseDTO;
import com.trueque_api.staff.exception.BadRequestException;
import com.trueque_api.staff.exception.NotFoundException;
import com.trueque_api.staff.exception.UnauthorizedAccessException;
import com.trueque_api.staff.model.Listing;
import com.trueque_api.staff.model.TradeOffer;
import com.trueque_api.staff.repository.ListingRepository;
import com.trueque_api.staff.repository.TradeOfferRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TradeOfferService {

    private final TradeOfferRepository tradeOfferRepository;
    private final ListingRepository listingRepository;

    public TradeOfferService(TradeOfferRepository tradeOfferRepository, ListingRepository listingRepository) {
        this.tradeOfferRepository = tradeOfferRepository;
        this.listingRepository = listingRepository;
    }

    public List<TradeOfferResponseDTO> getOffersForListing(UUID listingId, String authenticatedEmail) {
        listingRepository.findByIdAndUserEmail(listingId, authenticatedEmail)
            .orElseThrow(() -> new UnauthorizedAccessException("Você não tem permissão para visualizar essas ofertas."));
    
        List<TradeOffer> offers = tradeOfferRepository.findAllByListingId(listingId);
    
        return offers.stream()
            .map(this::toDTO)
            .toList();
    }    

    public TradeOfferResponseDTO createOffer(TradeOfferRequestDTO dto) {

        Listing offered = listingRepository.findById(dto.offeredListingId())
                .orElseThrow(() -> new NotFoundException("Offered listing not found"));

        Listing requested = listingRepository.findById(dto.requestedListingId())
                .orElseThrow(() -> new NotFoundException("Requested listing not found"));

        if (offered.getUser().getId().equals(requested.getUser().getId())) {
            throw new BadRequestException("Você não pode fazer uma proposta para você mesmo.");
        }

        TradeOffer offer = new TradeOffer();
        offer.setOfferedListing(offered);
        offer.setRequestedListing(requested);

        TradeOffer saved = tradeOfferRepository.save(offer);

        return toDTO(saved);
    }

    @Transactional
    public TradeOfferResponseDTO acceptOffer(UUID offerId, String authenticatedEmail) {
        TradeOffer offer = tradeOfferRepository.findByIdAndRequestedListingUserEmail(offerId, authenticatedEmail)
            .orElseThrow(() -> new UnauthorizedAccessException("Você não tem permissão para aceitar esta oferta."));

        if (!offer.getStatus().equals("pendente")) {
            throw new BadRequestException("Essa oferta já foi processada.");
        }

        Listing offered = listingRepository.findByIdForUpdate(offer.getOfferedListing().getId())
            .orElseThrow(() -> new NotFoundException("Listing não encontrado"));
    
        Listing requested = listingRepository.findByIdForUpdate(offer.getRequestedListing().getId())
                .orElseThrow(() -> new NotFoundException("Listing não encontrado"));
        
        if (!offered.getStatus().equals("ativo") || !requested.getStatus().equals("ativo")) {
            throw new BadRequestException("Um dos anúncios já foi trocado ou está em negociação.");
        }
    
        offer.setStatus("aceita");
        offer.setAcceptedAt(LocalDateTime.now());

        offer.getOfferedListing().setStatus("negociando");
        offer.getRequestedListing().setStatus("negociando");

        List<TradeOffer> relatedOffers = tradeOfferRepository.findByOfferedListingIdOrRequestedListingId(
            offer.getOfferedListing().getId(),
            offer.getRequestedListing().getId()
        );

        for (TradeOffer o : relatedOffers) {
            if (!o.getId().equals(offerId) && o.getStatus().equals("pendente")) {
                o.setStatus("recusada");
                o.setRejectedAt(LocalDateTime.now());
            }
        }

        return toDTO(offer);
    }

    public TradeOfferResponseDTO rejectOffer(UUID offerId, String authenticatedEmail) {
        TradeOffer offer = tradeOfferRepository.findByIdAndRequestedListingUserEmail(offerId, authenticatedEmail)
            .orElseThrow(() -> new UnauthorizedAccessException("Você não tem permissão para recusar esta oferta."));

        offer.setStatus("recusada");
        offer.setRejectedAt(LocalDateTime.now());

        return toDTO(offer);
    }

    private TradeOfferResponseDTO toDTO(TradeOffer offer) {
        return new TradeOfferResponseDTO(
            offer.getId(),
            offer.getStatus(),
            offer.getCreatedAt(),
            offer.getAcceptedAt(),
            offer.getRejectedAt(),
            offer.getOfferedListing().getId(),
            offer.getRequestedListing().getId()
        );
    }
}