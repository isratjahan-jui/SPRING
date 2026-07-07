import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

import { environment } from '../../environments/environments';
import { LoginRequest, LoginResponse, RegisterRequest, User } from '../models/auth.model';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private API_URL = environment.apiUrl;

  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_KEY = 'auth_user';

  readonly #token = signal<string | null>(localStorage.getItem(this.TOKEN_KEY));
  readonly #user = signal<LoginResponse | null>(this.#getStoredUser());

  readonly token = this.#token.asReadonly();
  readonly user = this.#user.asReadonly();
  readonly isLoggedIn = computed(() => !!this.#token());
  readonly role = computed(() => this.#user()?.role ?? null);
  readonly userId = computed(() => this.#user()?.userId ?? null);
  readonly userName = computed(() => this.#user()?.name ?? null);

  constructor(private http: HttpClient, private router: Router) {}

  login(data: LoginRequest) {
    return this.http.post<LoginResponse>(`${this.API_URL}/auth/login`, data);
  }

  register(data: RegisterRequest) {
    return this.http.post<{ message: string }>(`${this.API_URL}/auth/register`, data);
  }

  verifyEmail(token: string) {
    return this.http.get<{ message: string }>(`${this.API_URL}/auth/verify`, { params: { token } });
  }

  forgotPassword(email: string) {
    return this.http.post<{ message: string }>(`${this.API_URL}/auth/forgot-password`, { email });
  }

  resetPassword(token: string, newPassword: string) {
    return this.http.post<{ message: string }>(`${this.API_URL}/auth/reset-password`, { token, newPassword });
  }

  setSession(response: LoginResponse) {
    localStorage.setItem(this.TOKEN_KEY, response.token);
    localStorage.setItem(this.USER_KEY, JSON.stringify(response));
    this.#token.set(response.token);
    this.#user.set(response);
  }

  logout() {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.#token.set(null);
    this.#user.set(null);
    this.router.navigate(['/login']);
  }

  getToken() {
    return this.#token();
  }

  #getStoredUser(): LoginResponse | null {
    try {
      const u = localStorage.getItem(this.USER_KEY);
      return u ? JSON.parse(u) : null;
    } catch {
      return null;
    }
  }
}
