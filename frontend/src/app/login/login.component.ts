import { Component } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button'
import { FormControl, FormGroup } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';

@Component({
    selector: 'app-login',
    imports: [MatFormFieldModule, MatInputModule, MatButtonModule, ReactiveFormsModule, CommonModule],
    templateUrl: './login.component.html',
    styleUrl: './login.component.css'
})
export class LoginComponent {
    loginForm = new FormGroup({
        username: new FormControl(''),
        password: new FormControl('')
    });

    showError = false;
    errorMessage = '';

    constructor(private authService: AuthService, private router: Router) { }

    onSubmit() {
        const username = this.loginForm.get('username')?.value;
        const password = this.loginForm.get('password')?.value;

        if (username && password) {
            this.authService.login(username, password).subscribe({
                next: (response) => {
                    this.showError = false;
                    const userId = response.body.userId;
                    sessionStorage.setItem('userId', userId);
                    sessionStorage.setItem('role', response.body.role);
                    this.router.navigate(['/games']);
                },
                error: () => {
                    this.showError = true;
                    this.errorMessage = 'Invalid account credentials';
                    setTimeout(() => {
                        this.showError = false;
                    }, 2000);
                }
            });
        }
        else {
            this.showError = true;
            this.errorMessage = 'Please fill out all fields.';
            setTimeout(() => {
                this.showError = false;
            }, 2000);
        }
    }
}