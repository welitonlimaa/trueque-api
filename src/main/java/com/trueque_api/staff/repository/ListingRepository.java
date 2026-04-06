package com.trueque_api.staff.repository;

import com.trueque_api.staff.model.Listing;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ListingRepository extends JpaRepository<Listing, UUID> {
    @Query("SELECT l FROM Listing l WHERE l.id = :listingId AND l.user.email = :email")
    Optional<Listing> findByIdAndUserEmail(UUID listingId, String email);

    List<Listing> findAllByUserId(UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM Listing l WHERE l.id = :id")
    Optional<Listing> findByIdForUpdate(@Param("id") UUID id);

    List<Listing> findAllByStatus(String status);

    List<Listing> findAllByUserIdAndStatus(UUID userId, String status);


    @Query("""
        SELECT DISTINCT l FROM Listing l
        JOIN FETCH l.user
        WHERE l.status = :status
    """)
    List<Listing> findAllByStatusWithUser(@Param("status") String status);

    @Query("""
        SELECT DISTINCT l FROM Listing l
        JOIN FETCH l.user
        WHERE l.user.id = :userId
    """)
    List<Listing> findAllByUserIdWithUser(@Param("userId") UUID userId);

    @Query("""
        SELECT DISTINCT l FROM Listing l
        JOIN FETCH l.user
        WHERE l.user.id = :userId AND l.status = :status
    """)
    List<Listing> findAllByUserIdAndStatusWithUser(
        @Param("userId") UUID userId,
        @Param("status") String status
    );

    @Query("""
        SELECT DISTINCT l FROM Listing l
        JOIN FETCH l.user
        WHERE l.id = :id
    """)
    Optional<Listing> findByIdWithUser(@Param("id") UUID id);

    @Query("SELECT DISTINCT l FROM Listing l JOIN FETCH l.user WHERE l.id IN :ids")
    List<Listing> findAllByIdsWithUser(@Param("ids") List<UUID> ids);
}