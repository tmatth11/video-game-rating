import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { GamesComponent } from './games/games.component';
import { authGuard } from './auth.guard';
import { DetailsComponent } from './details/details.component';

export const routes: Routes = [
    {
        path: '',
        component: HomeComponent,
        title: 'Home'
    },
    {
        path: 'login',
        component: LoginComponent,
        title: 'Login'
    },
    {
        path: 'register',
        component: RegisterComponent,
        title: 'Register'
    },
    {
        path: 'games',
        component: GamesComponent,
        title: 'Games',
        canActivate: [authGuard]
    },
    {
        path: 'games/:gameId',
        component: DetailsComponent,
        title: 'Details',
        canActivate: [authGuard]
    }
];
