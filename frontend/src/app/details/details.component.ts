import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Game, Review } from '../games/games.component';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { EditDialogComponent } from '../edit-dialog/edit-dialog.component';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { EditReviewComponent } from '../edit-review/edit-review.component';

@Component({
    selector: 'app-details',
    imports: [CommonModule, MatButtonModule, MatDialogModule],
    templateUrl: './details.component.html',
    styleUrl: './details.component.css'
})
export class DetailsComponent implements OnInit {
    game!: Game;
    isOwner: boolean = false;
    isUser: boolean = false;

    constructor(private route: ActivatedRoute, public dialog: MatDialog, private authService: AuthService, private router: Router) { }

    ngOnInit() {
        const gameId = this.route.snapshot.paramMap.get('gameId');
        if (gameId) {
            this.authService.getGameById(gameId).subscribe({
                next: (game) => {
                    this.game = game;
                    const userId = sessionStorage.getItem('userId');
                    if (userId && this.game.owner.id === parseInt(userId, 10)) {
                        this.isOwner = true;
                        console.log("User is owner");
                    }
                    
                    const role = sessionStorage.getItem('role');
                    if (role && role === 'USER') {
                        this.isUser = true;
                    }
                },
                error: (error) => {
                    console.error('Error fetching game details:', error);
                }
            });
        } 
        else {
            console.error('No game ID found in route.');
        }
    }

    openDialog(): void {
        const dialogRef = this.dialog.open(EditDialogComponent, {
            data: { game: this.game }
        });

        dialogRef.componentInstance.gameUpdated.subscribe((updatedGame: Game) => {
            this.game = updatedGame;
        });

        dialogRef.afterClosed().subscribe(result => {
            console.log('The dialog was closed');
        });
    }

    deleteGame() {
        this.authService.deleteGame(this.game.gameId.toString()).subscribe({
            next: () => {
                console.log('Game deleted successfully');
                this.router.navigate(['/games']);
            },
            error: () => {
                console.error('Error deleting game');
            }
        });
    }

    isReviewOwner(review: Review): boolean {
        const userId = sessionStorage.getItem('userId');
        return Boolean(userId && review.owner.id === parseInt(userId));
    }

    openReviewDialog(review?: Review): void {
        const dialogRef = this.dialog.open(EditReviewComponent, {
            data: {
                review,
                gameId: this.game.gameId,
                isNew: !review
            }
        });

        dialogRef.componentInstance.reviewUpdated.subscribe((updatedReview: Review) => {
            this.refreshGameDetails();
        });
    }


    deleteReview(review: Review) {
        this.authService.deleteReview(review.id.toString()).subscribe({
            next: () => {
                // this.router.navigate(['/games']);
                this.refreshGameDetails();
            },
            error: () => {
                console.error('Error deleting review');
            }
        });
    }

    refreshGameDetails() {
        this.authService.getGameById(this.game.gameId.toString()).subscribe({
            next: (updatedGame) => {
                this.game = updatedGame;
            },
            error: (error) => {
                console.error('Error refreshing game details:', error);
            }
        });
    }
}