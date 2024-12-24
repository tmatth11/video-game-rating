package com.example.videogame_ratings;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long gameId;

    private String title;
    private List<String> genre;
    private List<String> platform;
    private int releaseYear;
    
    @Column(columnDefinition = "DECIMAL(3,1)")
    private double averageRating;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "game")
    @JsonManagedReference
    private List<Review> reviews = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private AppUser owner;
    
    public Game() {
    }
    
    public Game(String title, List<String> genre, List<String> platform, int releaseYear, AppUser owner) {
        super();
        this.title = title;
        this.genre = genre;
        this.platform = platform;
        this.reviews = new ArrayList<>();
        this.releaseYear = releaseYear;
        this.owner = owner;
    }

    // Getters and setters

    public Long getGameId() {
        return gameId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getGenre() {
        return genre;
    }

    public void setGenre(List<String> genre) {
        this.genre = genre;
    }

    public List<String> getPlatform() {
        return platform;
    }

    public void setPlatform(List<String> platform) {
        this.platform = platform;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
        updateAverageRating();
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public AppUser getOwner() {
        return owner;
    }

    public void setOwner(AppUser owner) {
        this.owner = owner;
    }

    // Helper functions

    // Update the average rating of the game based on the reviews
    public void updateAverageRating() {
        OptionalDouble average = reviews.stream()
                                        .mapToDouble(Review::getRating)
                                        .average();
        this.averageRating = average.isPresent() ? formatToOneDecimalPlace(average.getAsDouble()) : 0.0;
    }

    // Format the average rating to one decimal place
    private double formatToOneDecimalPlace(double value) {
        double clampedValue = Math.min(10.0, Math.max(0.0, value));
        return Double.parseDouble(String.format("%.1f", clampedValue)); 
    }
}
