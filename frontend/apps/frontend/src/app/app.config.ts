import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { appRoutes } from './app.routes';
import { provideHttpClient, withInterceptors, withXhr } from "@angular/common/http";
import { authInterceptor } from "./auth/auth.interceptor";
import { provideApi } from '@lib/api';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(appRoutes),
    provideHttpClient(withXhr(), withInterceptors([authInterceptor])),
    provideApi({ basePath: '' }),
  ],
};
