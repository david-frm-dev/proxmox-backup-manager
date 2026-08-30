import { computed, inject, Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { AuthControllerService, LoginRequest, LoginResponse } from '@lib/api';

const TOKEN_KEY = 'pbm.token';
type payloadType = { sub: string; roles: string; exp: number };

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly api = inject(AuthControllerService);
  private readonly router = inject(Router);

  private readonly _token = signal<string | null>(localStorage.getItem(TOKEN_KEY));
  readonly token = this._token.asReadonly();
  readonly isLoggedIn = computed(() => !this.isExpired(this._token()));
  readonly username= computed(() => this.decodePayload(this.token())?.sub.toUpperCase() ?? '-');

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.api.login(credentials).pipe(
      tap((res) => this.setToken(res.token ?? null))
    );
  }

  logout(): void {
    this.setToken(null);
    this.router.navigate(['/login']);
  }

  private setToken(token: string | null): void {
    if (token) {
      localStorage.setItem(TOKEN_KEY, token);
    } else {
      localStorage.removeItem(TOKEN_KEY);
    }
    this._token.set(token);
  }

  private isExpired(token: string | null): boolean {
    if (!token) return true;
    const payload = this.decodePayload(token);
    return payload === null || payload.exp * 1000 <= Date.now();
  }

  private decodePayload(token: string | null): payloadType | null {
    if (!token) return null;
    try {
      return JSON.parse(atob(token.split('.')[1]));
    } catch {
      return null;
    }
  }

}
