import { Component } from '@angular/core';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button'
import { FormControl, FormGroup } from '@angular/forms';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
    selector: 'app-register',
    imports: [MatFormFieldModule, MatInputModule, MatButtonModule, ReactiveFormsModule, MatSelectModule, MatOptionModule, CommonModule],
    templateUrl: './register.component.html',
    styleUrl: './register.component.css'
})
export class RegisterComponent {

    constructor(
        private authService: AuthService,
        private router: Router
    ) { }

    loginForm = new FormGroup({
        username: new FormControl(''),
        password: new FormControl(''),
        confirmPassword: new FormControl(''),
        role: new FormControl('')
    });

    showError = false;
    errorMessage = '';

    onSubmit() {
        const username = this.loginForm.get('username')?.value;
        const password = this.loginForm.get('password')?.value;
        const confirmPassword = this.loginForm.get('confirmPassword')?.value;
        const role = this.loginForm.get('role')?.value?.toUpperCase();

        if (username && password && confirmPassword && role) {
            if (password === confirmPassword) {
                this.showError = false;
                this.authService.register(username, password, role).subscribe({
                    next: () => {
                        this.router.navigate(['/login']);
                    },
                    error: (error) => {
                        this.showError = true;
                        this.errorMessage = error.error?.message || 'Registration failed';
                        setTimeout(() => {
                            this.showError = false;
                        }, 2000);
                    }
                });
            }
            else {
                this.showError = true;
                this.errorMessage = 'Passwords do not match.';
            }
        }
        else {
            this.showError = true;
            this.errorMessage = 'Please fill out all fields.';
        }

        if (this.showError) {
            setTimeout(() => {
                this.showError = false;
            }, 2000);
        }
    }
}