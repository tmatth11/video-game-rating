import { Component, OnInit } from '@angular/core';
import { AuthService } from '../auth.service';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { RouterModule, Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { EditDialogComponent } from '../edit-dialog/edit-dialog.component';

export interface Review {
    id: number;
    content: string;
    rating: number;
    owner: {
        id: number;
        username: string;
        role: string;
    };
    gameId: number;
}

export interface Game {
    gameId: number;
    title: string;
    genre: string[];
    platform: string[];
    releaseYear: number;
    averageRating: number;
    reviews: Review[];
    owner: Owner;
}

export interface Owner {
    id: number;
    username: string;
}

@Component({
    selector: 'app-games',
    standalone: true,
    imports: [CommonModule, MatTableModule, RouterModule, MatButtonModule, MatDialogModule],
    templateUrl: './games.component.html',
    styleUrl: './games.component.css'
})

export class GamesComponent implements OnInit {
    games: Game[] = [];
    displayedColumns: string[] = ['title', 'releaseYear', 'averageRating'];
    isAdmin: boolean = false;

    constructor(private authService: AuthService, private router: Router, public dialog: MatDialog) { }

    openNewGameDialog(): void {
        const dialogRef = this.dialog.open(EditDialogComponent, {
            data: { isNew: true }
        });

        dialogRef.componentInstance.gameUpdated.subscribe((newGame: Game) => {
            this.games = [...this.games, newGame];
        });
    }

    ngOnInit() {
        this.loadGames();
        const role = sessionStorage.getItem('role');
        if (role && role === 'ADMIN') {
            this.isAdmin = true;
        }
    }

    loadGames() {
        this.authService.getGames().subscribe({
            next: (data) => {
                this.games = data;
            },
            error: (error) => {
                console.error('Error fetching games:', error);
            }
        });
    }

    viewGameDetails(game: Game) {
        this.router.navigate(['/games', game.gameId], { state: { game } });
    }
}