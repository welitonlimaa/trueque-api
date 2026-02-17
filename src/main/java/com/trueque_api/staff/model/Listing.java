package com.trueque_api.staff.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "listings")
public class Listing {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    private String description;
    private String category;
    private String condition;
    private String city;
    private String state;

    @Column(nullable = false)
    private String status = "pendente";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ListingImage> images;

    @OneToMany(mappedBy = "offeredListing")
    private List<TradeOffer> asOfferedListing;

    @OneToMany(mappedBy = "requestedListing")
    private List<TradeOffer> asRequestedListing;
}