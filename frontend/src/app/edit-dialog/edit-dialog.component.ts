import { Component, EventEmitter, Inject, Output } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { ReactiveFormsModule } from '@angular/forms';
import { FormControl, FormGroup } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { Game } from '../games/games.component';
import { AuthService } from '../auth.service';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
    selector: 'app-edit-dialog',
    imports: [MatDialogModule, MatFormFieldModule, MatInputModule, MatButtonModule, ReactiveFormsModule, MatIconModule, MatSnackBarModule],
    templateUrl: './edit-dialog.component.html',
    styleUrl: './edit-dialog.component.css'
})
export class EditDialogComponent {
    @Output() gameUpdated = new EventEmitter<Game>();

    editForm: FormGroup;

    constructor(
        @Inject(MAT_DIALOG_DATA) public data: { game?: Game, isNew?: boolean },
        private dialogRef: MatDialogRef<EditDialogComponent>,
        private authService: AuthService,
        private snackBar: MatSnackBar
    ) {
        this.editForm = new FormGroup({
            title: new FormControl(data.game?.title || ''),
            genre: new FormControl(data.game?.genre.join(',') || ''),
            platform: new FormControl(data.game?.platform.join(',') || ''),
            releaseYear: new FormControl(data.game?.releaseYear || new Date().getFullYear()),
        });
    }

    onSubmit() {
        const title = this.editForm.get('title')?.value;
        const genre = this.editForm.get('genre')?.value.split(',').map((g: string) => g.trim()).filter((g: string) => g);
        const platform = this.editForm.get('platform')?.value.split(',').map((p: string) => p.trim()).filter((p: string) => p);
        const releaseYear = this.editForm.get('releaseYear')?.value;
    
        if (title && genre && platform && releaseYear) {
            const gameData = {
                title,
                genre,
                platform,
                releaseYear
            };
    
            if (this.data.isNew) {
                this.authService.addGame(gameData).subscribe({
                    next: (newGame) => {
                        this.gameUpdated.emit(newGame);
                        this.dialogRef.close();
                        this.snackBar.open('Game created successfully', 'Close', {
                            duration: 3000
                        });
                    },
                    error: (err) => {
                        console.error('Error creating game:', err);
                    }
                });
            } else {
                const updatedGame: Game = {
                    ...this.data.game!,
                    ...gameData
                };
    
                if (JSON.stringify(updatedGame) !== JSON.stringify(this.data.game)) {
                    this.authService.updateGame(this.data.game!.gameId.toString(), updatedGame).subscribe({
                        next: () => {
                            this.gameUpdated.emit(updatedGame);
                            this.dialogRef.close();
                            this.snackBar.open('Game updated successfully', 'Close', {
                                duration: 3000
                            });
                        },
                        error: (err) => {
                            console.error('Error updating game:', err);
                        }
                    });
                } else {
                    this.dialogRef.close();
                }
            }
        }
    }

    onClear() {
        this.editForm.reset();
    }

    closeDialog() {
        this.dialogRef.close();
    }
}