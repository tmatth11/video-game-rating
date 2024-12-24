import { Component, EventEmitter, Inject, Output } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { ReactiveFormsModule, FormControl, FormGroup } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBarModule, MatSnackBar } from '@angular/material/snack-bar';
import { Review } from '../games/games.component';
import { AuthService } from '../auth.service';

@Component({
    selector: 'app-edit-review',
    standalone: true,
    imports: [MatDialogModule, MatFormFieldModule, MatInputModule, MatButtonModule, ReactiveFormsModule, MatIconModule, MatSnackBarModule],
    templateUrl: './edit-review.component.html',
    styleUrl: './edit-review.component.css'
})
export class EditReviewComponent {
    @Output() reviewUpdated = new EventEmitter<Review>();
    editForm: FormGroup;

    constructor(
        @Inject(MAT_DIALOG_DATA) public data: { review?: Review, gameId?: number, isNew?: boolean },
        private dialogRef: MatDialogRef<EditReviewComponent>,
        private authService: AuthService,
        private snackBar: MatSnackBar
    ) {
        this.editForm = new FormGroup({
            content: new FormControl(data.review?.content || ''),
            rating: new FormControl(data.review?.rating || 10)
        });
    }

    onSubmit() {
        const content = this.editForm.get('content')?.value;
        const rating = this.editForm.get('rating')?.value;

        if (content && rating !== null) {
            const reviewData = {
                content,
                rating,
                game: { gameId: this.data.gameId }
            };

            if (this.data.isNew) {
                this.authService.addReview(reviewData).subscribe({
                    next: (newReview) => {
                        this.reviewUpdated.emit(newReview);
                        this.dialogRef.close();
                        this.snackBar.open('Review created successfully', 'Close', {
                            duration: 3000
                        });
                    },
                    error: (err) => {
                        console.error('Error creating review:', err);
                    }
                });
            }
            else {
                const updatedReview = {
                    ...this.data.review!,
                    content,
                    rating
                };

                if (JSON.stringify(updatedReview) !== JSON.stringify(this.data.review)) {
                    this.authService.updateReview(this.data.review!.id.toString(), updatedReview).subscribe({
                        next: (updated) => {
                            this.reviewUpdated.emit(updated);
                            this.dialogRef.close();
                            this.snackBar.open('Review updated successfully', 'Close', {
                                duration: 3000
                            });
                        },
                        error: (err) => {
                            console.error('Error updating review:', err);
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