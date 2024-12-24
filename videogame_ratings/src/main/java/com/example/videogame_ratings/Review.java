package com.example.videogame_ratings;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.GenerationType;

@Entity
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String content;

    @Column(columnDefinition = "DECIMAL(3,1)")
    private double rating;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private AppUser owner;

    public Review() {
    }

    public Review(String content, double rating, Game game, AppUser owner) {
        super();
        this.content = content;
        this.rating = rating;
        this.game = game;
        this.owner = owner;
        game.getReviews().add(this);
        game.updateAverageRating();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game")
    @JsonBackReference
    private Game game;

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
        game.getReviews().add(this);
    }

    public Long getGameId() {
        return game.getGameId();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        if (rating < 0.0 || rating > 10.0) {
            throw new IllegalArgumentException("Rating must be between 0.0 and 10.0");
        }
        this.rating = Double.parseDouble(String.format("%.1f", rating));
    }

    public AppUser getOwner() {
        return owner;
    }

    public void setOwner(AppUser owner) {
        this.owner = owner;
    }

    // Helper functions

    // Update the average rating of the game when a review is added or updated
    @PrePersist
    @PreUpdate
    public void updateGameAverageRating() {
        if (game != null) {
            game.updateAverageRating();
        }
    }

    @PreRemove
    public void beforeRemoval() {
        if (game != null) {
            game.getReviews().remove(this);
            game.updateAverageRating();
        }
    }
}
