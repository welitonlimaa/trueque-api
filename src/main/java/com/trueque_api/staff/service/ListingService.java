package com.trueque_api.staff.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trueque_api.staff.dto.ListingRequestDTO;
import com.trueque_api.staff.dto.ListingResponseDTO;
import com.trueque_api.staff.dto.SearchListingResponseDTO;
import com.trueque_api.staff.dto.UserSummaryDTO;
import com.trueque_api.staff.exception.BadRequestException;
import com.trueque_api.staff.exception.InvalidCredentialsException;
import com.trueque_api.staff.exception.NotFoundException;
import com.trueque_api.staff.exception.UnauthorizedAccessException;
import com.trueque_api.staff.model.Listing;
import com.trueque_api.staff.model.ListingImage;
import com.trueque_api.staff.model.User;
import com.trueque_api.staff.repository.ListingImageRepository;
import com.trueque_api.staff.repository.ListingRepository;
import com.trueque_api.staff.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ListingService {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final ListingImageRepository listingImageRepository;

    @Value("${elasticsearch.url}")
    private String elasticBaseUrl;

    public ListingService(
        ListingRepository listingRepository, 
        UserRepository userRepository, 
        ListingImageRepository listingImageRepository) 
    {
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
    List<Listing> listings = listingRepository.findAllByStatus("ativo");
    return listings.stream()
                   .map(this::toResponseDTO)
                   .toList();
    }
    
    public List<ListingResponseDTO> getPendingListings() {
        return listingRepository.findAllByStatus("pendente")
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ListingResponseDTO getListingById(UUID id, String authenticatedEmail) {

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Anúncio não encontrado."));

        if (listing.getStatus().equals("ativo")) {
            return toResponseDTO(listing);
        }

        if (authenticatedEmail == null) {
            throw new InvalidCredentialsException(
                    "Você precisa estar autenticado para visualizar este anúncio.");
        }

        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

        boolean isOwner = listing.getUser().equals(user);

        if (!isOwner && !user.isAdminOrModerator()) {
            throw new UnauthorizedAccessException(
                    "Você não tem permissão para visualizar este anúncio.");
        }

        return toResponseDTO(listing);
    }

    public List<ListingResponseDTO> listUserListings(String authenticatedEmail, String status) {

        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

        List<Listing> listings;

        if (status == null || status.isBlank()) {
            listings = listingRepository.findAllByUserId(user.getId());
        } else {
            listings = listingRepository.findAllByUserIdAndStatus(user.getId(), status);
        }

        return listings.stream()
                .map(this::toResponseDTO)
                .toList();
    }
    
    @Transactional
    public void markAsExchanged(UUID listingId, String authenticatedEmail) {
        Listing listing = listingRepository.findByIdAndUserEmail(listingId, authenticatedEmail)
            .orElseThrow(() -> new UnauthorizedAccessException("Você não tem permissão para alterar este anúncio."));

        if (!"negociando".equalsIgnoreCase(listing.getStatus())) {
            throw new BadRequestException("Anúncio só pode ser marcado como 'trocado' se estiver com status 'negociando'.");
        }

        listing.setStatus("trocado");
        listingRepository.save(listing);
    }

    @Transactional
    public ListingResponseDTO updateListingStatus(UUID listingId, String newStatus) {

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new NotFoundException("Anúncio não encontrado."));

        listing.setStatus(newStatus);

        return toResponseDTO(listing);
    }

    public void deleteListing(UUID id, String authenticatedEmail) {

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Anúncio não encontrado."));

        if (authenticatedEmail == null) {
            throw new InvalidCredentialsException(
                    "Você precisa estar autenticado para visualizar este anúncio.");
        }

        User user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

        boolean isOwner = listing.getUser().equals(user);

        if (!isOwner && !user.isAdminOrModerator()) {
            throw new UnauthorizedAccessException(
                    "Você não tem permissão para deletar este anúncio.");
        }

        listingRepository.delete(listing);
    }

    public SearchListingResponseDTO searchListings(String query, int page, int size) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            String elasticUrl = elasticBaseUrl + "/" + "itens" + "/_search";

            String body = """
            {
            "from": %d,
            "size": %d,
            "query": {
                "multi_match": {
                "query": "%s",
                "fields": ["text^2"],
                "fuzziness": "AUTO"
                }
            }
            }
            """.formatted(page * size, size, query);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    elasticUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            JsonNode hits = root.path("hits").path("hits");

            List<UUID> ids = new ArrayList<>();
            Map<UUID, Double> scoreMap = new HashMap<>();

            for (JsonNode hit : hits) {
                String idStr = hit.path("_id").asText(null);
                if (idStr == null) continue;

                try {
                    UUID id = UUID.fromString(idStr);
                    double score = hit.path("_score").asDouble(0);

                    ids.add(id);
                    scoreMap.put(id, score);
                } catch (IllegalArgumentException ignored) {
                }
            }

            if (ids.isEmpty()) {
                return SearchListingResponseDTO.builder()
                        .content(List.of())
                        .page(page)
                        .totalPages(0)
                        .totalElements(0)
                        .build();
            }

            List<Listing> listings = listingRepository.findAllById(ids);

            listings.sort((a, b) -> Double.compare(
                    scoreMap.getOrDefault(b.getId(), 0.0),
                    scoreMap.getOrDefault(a.getId(), 0.0)
            ));

            List<ListingResponseDTO> content = listings.stream()
                    .map(this::toResponseDTO)
                    .toList();

            long total = root.path("hits").path("total").path("value").asLong(0);

            return SearchListingResponseDTO.builder()
                    .content(content)
                    .page(page)
                    .totalPages((int) Math.ceil((double) total / size))
                    .totalElements(total)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Erro na busca", e);
        }
    }


    private ListingResponseDTO toResponseDTO(Listing listing) {
        User user = listing.getUser();

        UserSummaryDTO userSummary = UserSummaryDTO.builder()
            .id(user.getId())
            .name(user.getName())
            .profilePicture(user.getProfilePicture())
            .build();

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
                .user(userSummary)
                .build();
    }
    
}