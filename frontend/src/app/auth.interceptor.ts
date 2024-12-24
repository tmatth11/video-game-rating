import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const token = sessionStorage.getItem('jwt');
        if (token && (req.url.includes('/api/game') || req.url.includes('/api/review'))) {
            const cloned = req.clone({
                headers: req.headers.set('Authorization', `Bearer${token}`)
            });
            return next.handle(cloned);
        }
        return next.handle(req);
    }
}