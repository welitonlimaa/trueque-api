package com.trueque_api.staff.repository;

import com.trueque_api.staff.model.Listing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ListingRepository extends JpaRepository<Listing, UUID> {
    List<Listing> findAllByUserId(UUID userId);
}