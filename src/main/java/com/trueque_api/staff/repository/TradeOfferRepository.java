package com.trueque_api.staff.repository;

import com.trueque_api.staff.model.TradeOffer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TradeOfferRepository extends JpaRepository<TradeOffer, UUID> {
    List<TradeOffer> findByOfferedListingIdOrRequestedListingId(UUID offeredListingId, UUID requestedListingId);
    List<TradeOffer> findByRequestedListingId(UUID listingId);

    @Query("""
    SELECT t FROM TradeOffer t 
    WHERE t.id = :offerId AND t.requestedListing.user.email = :email
    """)
    Optional<TradeOffer> findByIdAndRequestedListingUserEmail(UUID offerId, String email);

    @Query("""
    SELECT t FROM TradeOffer t 
    WHERE t.offeredListing.id = :listingId OR t.requestedListing.id = :listingId
    """)
    List<TradeOffer> findAllByListingId(UUID listingId);

}
