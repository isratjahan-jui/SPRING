import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { LoginResponseDTO } from '../models';

@Injectable({ providedIn: 'root' })
export class LoginService {
  private baseUrl = 'http://localhost:8080/api/auth/login';

  constructor(private http: HttpClient) {}

  login(email: string, password: string): Observable<LoginResponseDTO> {
    return this.http.post<LoginResponseDTO>(this.baseUrl, { email, password }).pipe(
      catchError(err => {
        const message = err.error?.message || 'Login failed';
        return throwError(() => new Error(message));
      })
    );
  }
}