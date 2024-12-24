package com.example.videogame_ratings;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class VideogameRatingsApplication implements CommandLineRunner {
    private final GameRepository gameRepository;
    private final ReviewRepository reviewRepository;
    private final AppUserRepository userRepository;

    public VideogameRatingsApplication(GameRepository gameRepository, ReviewRepository reviewRepository,
            AppUserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(VideogameRatingsApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Create a password encoder
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Create ADMIN users
        AppUser admin1 = new AppUser("admin1", encoder.encode("123"), "ADMIN");
        AppUser admin2 = new AppUser("admin2", encoder.encode("123"), "ADMIN");

        // Create USER users
        AppUser user1 = new AppUser("user1", encoder.encode("123"), "USER");
        AppUser user2 = new AppUser("user2", encoder.encode("123"), "USER");
        AppUser user3 = new AppUser("user3", encoder.encode("123"), "USER");

        // Save users to the repository
        userRepository.saveAll(Arrays.asList(admin1, admin2, user1, user2, user3));

        // Create games owned by admin1
        Game game1 = new Game("Half Life", List.of("FPS"), List.of("Windows", "Playstation 2", "macOS", "Linux"), 1998,
                admin1);
        Game game2 = new Game("Half Life: Opposing Force", List.of("FPS"), List.of("Windows", "OS X", "Linux"), 1999,
                admin1);
        Game game3 = new Game("Half Life: Blue Shift", List.of("FPS"), List.of("Windows", "OS X", "Linux"), 2001,
                admin1);

        // Create games owned by admin2
        Game game4 = new Game("Half Life: Decay", List.of("FPS"), List.of("Playstation 2"), 2001, admin2);
        Game game5 = new Game("Half Life 2", List.of("FPS"),
                List.of("Windows", "Xbox", "Xbox 360", "PlayStation 3", "Mac OS X", "Linux", "Android"), 2004, admin2);
        Game game6 = new Game("Half Life: Alyx", List.of("FPS"), List.of("Windows", "Linux"), 2020, admin2);

        // Save games to the repository
        gameRepository.saveAll(Arrays.asList(game1, game2, game3, game4, game5, game6));

        // Create reviews by user1
        Review review1 = new Review("This game is awesome!", 10.0, game1, user1);
        Review review2 = new Review("I loved playing as Adrian Shephard!", 8.7, game2, user1);
        Review review3 = new Review("This game is too short!", 3.0, game3, user1);
        Review review4 = new Review("I had to buy a PlayStation 2 to play this game!", 5.2, game4, user1);
        Review review5 = new Review("This was a very innovative game!", 9.0, game5, user1);
        Review review6 = new Review("I had to buy a VR headset to play this game!", 4.5, game6, user1);

        // Create reviews by user2
        Review review7 = new Review("The graphics look old, but the gameplay is still fun!", 7.5, game1, user2);
        Review review8 = new Review("I wanted to kill Gordon Freeman!", 1.1, game2, user2);
        Review review9 = new Review("Shortest game ever!", 2.0, game3, user2);
        Review review10 = new Review("I couldn't play this game because I don't have a PlayStation 2!", 0.0, game4, user2);
        Review review11 = new Review("GIVE ME EPISODE THREE!", 9.9, game5, user2);
        Review review12 = new Review("Does this mean Half Life 3 will come soon?", 8.2, game6, user2);

        // Create reviews by user3
        Review review13 = new Review("I loved how there's no cinematic cutscenes!", 7.8, game1, user3);
        Review review14 = new Review("I loved the new weapons!", 9.0, game2, user3);
        Review review15 = new Review("I think this game is too short!", 3.0, game3, user3);
        Review review16 = new Review("Very underrated game!", 8.0, game4, user3);
        Review review17 = new Review("I loved the new Source engine!", 9.5, game5, user3);
        Review review18 = new Review("I loved the new Source 2 engine!", 9.5, game6, user3);

        // Save reviews to the repository
        reviewRepository.saveAll(Arrays.asList(
                review1, review2, review3, review4, review5,
                review6, review7, review8, review9, review10, review11, review12, review13, review14, review15, review16, review17, review18));

        // Update the games to recalculate average ratings
        gameRepository.saveAll(Arrays.asList(game1, game2, game3, game4, game5, game6));
    }

}
