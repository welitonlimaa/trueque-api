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
@Table(name = "trade_offers")
public class TradeOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String status = "pendente";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @ManyToMany
    @JoinTable(
        name = "listing_trade_offer",
        joinColumns = @JoinColumn(name = "trade_offer_id"),
        inverseJoinColumns = @JoinColumn(name = "listing_id")
    )
    private List<Listing> listings;
}