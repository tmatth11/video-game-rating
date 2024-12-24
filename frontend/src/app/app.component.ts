import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';
import { AuthService } from './auth.service';

@Component({
    selector: 'app-root',
    imports: [RouterModule, MatToolbarModule, MatButtonModule, CommonModule],
    templateUrl: `./app.component.html`,
    styleUrl: './app.component.css'
})
export class AppComponent {
    title = 'video-game-ratings';

    constructor(
        private router: Router,
        private authService: AuthService
    ) { }

    isLoggedIn() {
        return this.authService.isLoggedIn();
    }

    navigateToHome() {
        this.router.navigate(['/']);
    }

    logout() {
        this.authService.logout().subscribe({
            next: () => {
                this.router.navigate(['/']);
                console.log('Logout successful');
            }
        });
    }

}
