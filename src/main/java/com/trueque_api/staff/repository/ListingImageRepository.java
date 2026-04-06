package com.trueque_api.staff.repository;

import com.trueque_api.staff.model.ListingImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ListingImageRepository extends JpaRepository<ListingImage, UUID> {
    List<ListingImage> findByListingId(UUID listingId);

    @Query("SELECT i FROM ListingImage i WHERE i.listing.id IN :listingIds")
    List<ListingImage> findByListingIdIn(@Param("listingIds") List<UUID> listingIds);
}
