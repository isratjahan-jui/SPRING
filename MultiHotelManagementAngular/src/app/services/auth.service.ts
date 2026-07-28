import { Injectable } from '@angular/core';

import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { StorageService } from './storage.service';
import {
  ForgotPasswordRequest,
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  ResetPasswordRequest,
} from '../models/auth.model';
import { Observable, tap } from 'rxjs';
import { environment } from '../../environments/environments';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private apiUrl = environment.apiUrl + 'auth';

  constructor(
    private http: HttpClient,
    private storage: StorageService,
    private router: Router,
  ) {}

  // ── Register ─────────────────────────────────────────

  register(dto: RegisterRequest): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`${this.apiUrl}/register`, dto);
  }

  // ── Login ────────────────────────────────────────────

  login(dto: LoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${this.apiUrl}/login`, dto)
      .pipe(tap((res) => this.storage.saveSession(res)));
  }

  // ── Logout ───────────────────────────────────────────

  logout(): void {
    this.storage.clearSession();
    this.router.navigate(['/login']);
  }

  // ── Forgot Password ──────────────────────────────────

  forgotPassword(dto: ForgotPasswordRequest): Observable<string> {
    return this.http.post(`${this.apiUrl}/forgot-password`, dto, { responseType: 'text' });
  }

  // ── Reset Password ───────────────────────────────────

  resetPassword(dto: ResetPasswordRequest): Observable<string> {
    return this.http.post(`${this.apiUrl}/reset-password`, dto, { responseType: 'text' });
  }

  // ── Verify Email ─────────────────────────────────────

  verifyEmail(token: string): Observable<string> {
    return this.http.get(`${this.apiUrl}/verify`, { params: { token }, responseType: 'text' });
  }

  // ── Change Password ─────────────────────────────────

  changePassword(userId: number, currentPassword: string, newPassword: string): Observable<string> {
    return this.http.post(
      `${this.apiUrl}/change-password`,
      { userId, currentPassword, newPassword },
      { responseType: 'text' },
    );
  }

  // ── Helpers ──────────────────────────────────────────

  isLoggedIn(): boolean {
    return this.storage.isLoggedIn();
  }
  getRole(): string | null {
    return this.storage.getRole();
  }
  getUser(): LoginResponse | null {
    return this.storage.getUser();
  }
}
