package com.trueque_api.staff.repository;

import com.trueque_api.staff.model.ListingImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ListingImageRepository extends JpaRepository<ListingImage, UUID> {
    List<ListingImage> findByListingId(UUID listingId);
}
