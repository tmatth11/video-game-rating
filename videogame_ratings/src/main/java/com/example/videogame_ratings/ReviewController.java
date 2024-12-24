package com.example.videogame_ratings;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
public class ReviewController {
    private final ReviewRepository reviewRepository;
    private final GameRepository gameRepository;
    private final AppUserRepository userRepository;

    public ReviewController(ReviewRepository reviewRepository, GameRepository gameRepository,
            AppUserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    // GET endpoint to retrieve all reviews
    @GetMapping("/api/review")
    public Iterable<Review> getReviews() {
        return reviewRepository.findAll();
    }

    // GET endpoint to retrieve a review by ID
    @GetMapping("/api/review/{id}")
    public Review getReviewById(@PathVariable("id") Long id) {
        return reviewRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));
    }

    // POST endpoint to add a review
    @PostMapping("/api/review")
    @ResponseStatus(HttpStatus.CREATED)
    public Review addReview(@RequestBody Review review) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        Game game = gameRepository.findById(review.getGame().getGameId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        review.setOwner(currentUser);
        review.setGame(game);
        game.getReviews().add(review);
        game.updateAverageRating();

        Review savedReview = reviewRepository.save(review);
        gameRepository.save(game);

        return savedReview;
    }

    // PUT endpoint to update a review
    @PutMapping("/api/review/{id}")
    public Review updateReview(@PathVariable("id") Long id, @RequestBody Review reviewDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        return reviewRepository.findById(id)
                .map(existingReview -> {
                    if (!existingReview.getOwner().getId().equals(currentUser.getId())) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this review");
                    }
                    existingReview.setContent(reviewDetails.getContent());
                    existingReview.setRating(reviewDetails.getRating());
                    Review updatedReview = reviewRepository.save(existingReview);

                    // Update the game's average rating
                    Game game = existingReview.getGame();
                    game.updateAverageRating();
                    gameRepository.save(game);

                    return updatedReview;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // DELETE endpoint to delete a review
    @DeleteMapping("/api/review/{id}")
    public Review deleteReview(@PathVariable("id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        return reviewRepository.findById(id)
                .map(review -> {
                    if (!review.getOwner().getId().equals(currentUser.getId())) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this review");
                    }
                    Game game = review.getGame();
                    reviewRepository.delete(review);
                    game.updateAverageRating();
                    gameRepository.save(game);
                    return review;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
