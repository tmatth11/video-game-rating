import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { HTTP_INTERCEPTORS, provideHttpClient } from '@angular/common/http';
import { AuthInterceptor } from './auth.interceptor';
import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
    providers: [
      provideZoneChangeDetection({ eventCoalescing: true }),
      provideRouter(routes),
      provideAnimationsAsync(),
      provideHttpClient(),
      { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
    ]
  };