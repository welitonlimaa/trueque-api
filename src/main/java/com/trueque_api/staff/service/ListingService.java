package com.trueque_api.staff.service;

import com.trueque_api.staff.dto.ListingRequestDTO;
import com.trueque_api.staff.dto.ListingResponseDTO;
import com.trueque_api.staff.exception.NotFoundException;
import com.trueque_api.staff.model.Listing;
import com.trueque_api.staff.model.ListingImage;
import com.trueque_api.staff.model.User;
import com.trueque_api.staff.repository.ListingImageRepository;
import com.trueque_api.staff.repository.ListingRepository;
import com.trueque_api.staff.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class ListingService {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final ListingImageRepository listingImageRepository;

    public ListingService(ListingRepository listingRepository, UserRepository userRepository, ListingImageRepository listingImageRepository) {
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
        this.listingImageRepository = listingImageRepository;
    }

    public ListingResponseDTO createListing(ListingRequestDTO listingDTO, String authenticatedEmail) {
        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

        Listing listing = new Listing();
        listing.setUser(user);
        listing.setTitle(listingDTO.getTitle());
        listing.setDescription(listingDTO.getDescription());
        listing.setCategory(listingDTO.getCategory());
        listing.setCondition(listingDTO.getCondition());
        listing.setCity(listingDTO.getCity());
        listing.setState(listingDTO.getState());

        Listing savedListing = listingRepository.save(listing);

        if (listingDTO.getImages() != null && !listingDTO.getImages().isEmpty()) {
            for (String imageUrl : listingDTO.getImages()) {
                ListingImage listingImage = new ListingImage();
                listingImage.setListing(savedListing);
                listingImage.setImageUrl(imageUrl);
                listingImageRepository.save(listingImage);
            }
        }

        return toResponseDTO(savedListing);
    }


    public List<ListingResponseDTO> getAllListings() {
    List<Listing> listings = listingRepository.findAll();
    return listings.stream()
                   .map(this::toResponseDTO)
                   .toList();
    }


    public ListingResponseDTO getListingById(UUID id) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Anúncio não encontrado."));
        return toResponseDTO(listing);
    }

    public List<ListingResponseDTO> listUserListings(String authenticatedEmail) {
        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
    
        return listingRepository.findAllByUserId(user.getId())
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }
    
    @Transactional
    public void markAsExchanged(UUID listingId, String authenticatedEmail) {
        Listing listing = listingRepository.findByIdAndUserEmail(listingId, authenticatedEmail)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para alterar este anúncio."));

        if (!"negociando".equalsIgnoreCase(listing.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Anúncio só pode ser marcado como 'trocado' se estiver com status 'negociando'.");
        }

        listing.setStatus("trocado");
        listingRepository.save(listing);
    }

    private ListingResponseDTO toResponseDTO(Listing listing) {
        List<ListingImage> listingImages = listingImageRepository.findByListingId(listing.getId());
    
        List<String> imageUrls = listingImages.stream()
                .map(ListingImage::getImageUrl)
                .toList();
    
        return ListingResponseDTO.builder()
                .id(listing.getId())
                .title(listing.getTitle())
                .description(listing.getDescription())
                .category(listing.getCategory())
                .condition(listing.getCondition())
                .images(imageUrls)
                .city(listing.getCity())
                .state(listing.getState())
                .status(listing.getStatus())
                .createdAt(listing.getCreatedAt())
                .build();
    }
    
}