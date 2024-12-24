import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private apiUrl = 'http://localhost:8080';

    // Handles account endpoints
    private loginUrl = `${this.apiUrl}/login`;
    private registerUrl = `${this.apiUrl}/api/register`;
    private refreshUrl = `${this.apiUrl}/refresh`;
    private logoutUrl = `${this.apiUrl}/api/logout`;

    // Handles user endpoints
    private gameUrl = `${this.apiUrl}/api/game`;
    private gameIdUrl = `${this.apiUrl}/api/game/`;
    private reviewUrl = `${this.apiUrl}/api/review`;
    private reviewIdUrl = `${this.apiUrl}/api/review/`;

    constructor(private http: HttpClient) { }

    // POST /login
    login(username: string, password: string): Observable<any> {
        return this.http.post(this.loginUrl, { username, password }, { observe: 'response' }).pipe(
            tap((response: HttpResponse<any>) => {
                const token = response.headers.get('Authorization');
                if (token) {
                    const tokenWithoutBearer = token.replace('Bearer', '');
                    sessionStorage.setItem('jwt', tokenWithoutBearer);
                    console.log('Login successful');
                }
            }),
            catchError((error: HttpErrorResponse) => {
                console.error('Login error:', error.message);
                return throwError(() => error);
            })
        );
    }

    // POST /api/register
    register(username: string, password: string, role: string): Observable<any> {
        return this.http.post(this.registerUrl, { username, password, role });
    }

    // POST /refresh
    refresh(token: string): Observable<any> {
        return this.http.post(this.refreshUrl, {}, {
            headers: {
                Authorization: `Bearer${token}`
            }
        }).pipe(
            tap((response: any) => {
                if (response && response.token) {
                    sessionStorage.setItem('jwt', response.token);
                }
            })
        );
    }

    // POST /api/logout
    logout(): Observable<any> {
        const token = sessionStorage.getItem('jwt');
        // Remove token first so user sees immediate UI feedback
        sessionStorage.removeItem('jwt');
        
        // If no token, just return success
        if (!token) {
          return new Observable(subscriber => {
            subscriber.next();
            subscriber.complete();
          });
        }
      
        return this.http.post(this.logoutUrl, {}, {
          headers: new HttpHeaders({
            'Authorization': `Bearer${token}`
          })
        }).pipe(
          catchError(error => {
            console.error('Logout error:', error);
            // Even if server call fails, we want UI to update
            return new Observable(subscriber => {
              subscriber.next();
              subscriber.complete();
            });
          })
        );
      }

    // GET /api/game
    getGames(): Observable<any> {
        return this.http.get(this.gameUrl, {
            headers: this.getAuthHeaders()
        });
    }

    // GET /api/game/{id}
    getGameById(id: string): Observable<any> {
        return this.http.get(`${this.gameIdUrl}${id}`, {
            headers: this.getAuthHeaders()
        });
    }

    // POST /api/game
    addGame(game: any): Observable<any> {
        return this.http.post(this.gameUrl, game, {
            headers: this.getAuthHeaders()
        });
    }

    // PUT /api/game/{id}
    updateGame(id: string, game: any): Observable<any> {
        return this.http.put(`${this.gameIdUrl}${id}`, game, {
            headers: this.getAuthHeaders()
        });
    }

    // DELETE /api/game/{id}
    deleteGame(id: string): Observable<any> {
        return this.http.delete(`${this.gameIdUrl}${id}`, {
            headers: this.getAuthHeaders()
        });
    }

    // GET /api/review
    getReviews(): Observable<any> {
        return this.http.get(this.reviewUrl, {
            headers: this.getAuthHeaders()
        });
    }

    // GET /api/review/{id}
    getReviewById(id: string): Observable<any> {
        return this.http.get(`${this.reviewIdUrl}${id}`, {
            headers: this.getAuthHeaders()
        });
    }

    // POST /api/review
    addReview(review: any): Observable<any> {
        return this.http.post(this.reviewUrl, review, {
            headers: this.getAuthHeaders()
        });
    }

    // PUT /api/review/{id}
    updateReview(id: string, review: any): Observable<any> {
        return this.http.put(`${this.reviewIdUrl}${id}`, review, {
            headers: this.getAuthHeaders()
        });
    }

    // DELETE /api/review/{id}
    deleteReview(id: string): Observable<any> {
        return this.http.delete(`${this.reviewIdUrl}${id}`, {
            headers: this.getAuthHeaders()
        });
    }

    // Helper functions

    private getAuthHeaders(): HttpHeaders {
        const token = sessionStorage.getItem('jwt');
        return new HttpHeaders({
            Authorization: `Bearer${token}`
        });
    }

    isLoggedIn(): boolean {
        return !!sessionStorage.getItem('jwt');
    }
}