# Video Game Rating Application

![Video Game Rating Application Cover Photo](https://github.com/user-attachments/assets/c0f9d847-0a3b-473d-8fd8-33dece2298ed)

This is a full-stack video game rating application built with Spring Boot and PostgreSQL for the back-end, Angular and Angular Material for the front-end, and JSON Web Tokens for user authentication. Administrators can view all games and reviews, create their own games, edit their own games, and delete their own games. Users can view all games and reviews, create their own reviews and ratings, edit their own reviews and ratings, and delete their own reviews. This application is meant to be a personal project to learn more about full-stack development using the technologies specified above.

## Prerequisites

- Java 17
- Node,js and npm
- Angular CLI
- PostgreSQL 17

## Setup

1. Clone the repository:
    ```sh
    git clone https://github.com/your-username/video-game-ratings.git
    cd video-game-ratings
    ```

2. Set up the PostgreSQL database:
    - Create a new PostgreSQL server on port `5433` with the username and password as `postgres`
    - Make a new database under that server called `videogame_ratings`
    - Check if the server is connected succesfully

3. Build and run the back-end (I used VS Code):
    - Click the arrow next to the run button
    - Select "Run Java"

4. Install front-end dependencies and run the development server:
    ```sh
    cd ../frontend
    npm install
    ng serve
    ```

## Running the Application
- The back-end server will be running at `http://localhost:8080`
- The front-end application will be running at `http://localhost:4200`
