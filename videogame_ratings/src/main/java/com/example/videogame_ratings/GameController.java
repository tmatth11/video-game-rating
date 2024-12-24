package com.example.videogame_ratings;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
public class GameController {
    private final GameRepository gameRepository;
    private final ReviewRepository reviewRepository;
    private final AppUserRepository userRepository;

    public GameController(GameRepository gameRepository, ReviewRepository reviewRepository,
            AppUserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    // GET endpoint to retrieve all games
    @GetMapping("/api/game")
    public Iterable<Game> getGames() {
        return gameRepository.findAll();
    }

    // GET endpoint to retrieve a game by ID
    @GetMapping("/api/game/{id}")
    public Game getGameById(@PathVariable("id") Long id) {
        return gameRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));
    }

    // POST endpoint to add a game
    @PostMapping("/api/game")
    @ResponseStatus(HttpStatus.CREATED)
    public Game addGame(@RequestBody Game game) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        game.setOwner(currentUser);
        return gameRepository.save(game);
    }

    // PUT endpoint to update a game
    @PutMapping("/api/game/{id}")
    public Game updateGame(@PathVariable("id") Long id, @RequestBody Game gameDetails) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        return gameRepository.findById(id)
                .map(existingGame -> {
                    if (!existingGame.getOwner().getId().equals(currentUser.getId())) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this game");
                    }
                    existingGame.setTitle(gameDetails.getTitle());
                    existingGame.setGenre(gameDetails.getGenre());
                    existingGame.setPlatform(gameDetails.getPlatform());
                    existingGame.setReleaseYear(gameDetails.getReleaseYear());
                    return gameRepository.save(existingGame);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // DELETE endpoint to delete a game
    @DeleteMapping("/api/game/{id}")
    public Game deleteGame(@PathVariable("id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

        return gameRepository.findById(id)
                .map(game -> {
                    if (!game.getOwner().getId().equals(currentUser.getId())) {
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this game");
                    }
                    // Remove all associated reviews
                    List<Review> reviews = new ArrayList<>(game.getReviews());
                    reviewRepository.deleteAll(reviews);
                    gameRepository.delete(game);
                    return game;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
