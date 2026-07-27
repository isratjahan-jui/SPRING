import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { LoginService } from '../../services/login.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="login-wrapper">
      <div class="login-card">
        <div class="login-brand">
          <div class="brand-icon">M</div>
          <h1>Multi Hotel Management</h1>
          <p>Sign in to your account</p>
        </div>

        @if (loginError) {
          <div class="error-card">
            <span class="error-icon">!</span>
            <span>{{ loginError }}</span>
          </div>
        }

        <form (ngSubmit)="onLogin()">
          <div class="form-group">
            <label>Email</label>
            <input
              type="email"
              [(ngModel)]="email"
              name="email"
              placeholder="you@example.com"
              required
            />
          </div>
          <div class="form-group">
            <label>Password</label>
            <input
              type="password"
              [(ngModel)]="password"
              name="password"
              placeholder="Enter your password"
              required
            />
          </div>
          <button type="submit" class="login-btn" [disabled]="loading">
            {{ loading ? 'Signing in...' : 'Sign In' }}
          </button>
        </form>

        <div class="demo-accounts">
          <p>Demo Accounts (any role works):</p>
          <div class="demo-row">
            <button (click)="fillDemo('admin@mhm.com', 'admin123')">Admin</button>
            <button (click)="fillDemo('owner@mhm.com', 'owner123')">Owner</button>
            <button (click)="fillDemo('customer@mhm.com', 'cust123')">Customer</button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .login-wrapper {
      height: 100vh; display: flex; align-items: center; justify-content: center;
      background: #0a0a0f;
    }

    .login-card {
      width: 420px; background: #111118; border: 1px solid rgba(255,255,255,0.06);
      border-radius: 16px; padding: 40px;
    }

    .login-brand { text-align: center; margin-bottom: 28px; }

    .login-brand .brand-icon {
      width: 56px; height: 56px; background: linear-gradient(135deg, #c9a96e, #8b6914);
      border-radius: 14px; display: flex; align-items: center; justify-content: center;
      font-weight: 700; font-size: 28px; color: #0a0a0f; margin: 0 auto 16px;
    }

    .login-brand h1 { font-size: 20px; font-weight: 600; margin: 0 0 4px; }
    .login-brand p { color: #666; font-size: 14px; margin: 0; }

    .error-card {
      background: rgba(231,76,60,0.1); border: 1px solid rgba(231,76,60,0.3);
      color: #e74c3c; padding: 12px 16px; border-radius: 8px; font-size: 14px;
      margin-bottom: 20px; display: flex; align-items: center; gap: 10px;
    }

    .error-icon {
      width: 24px; height: 24px; border-radius: 50%; background: #e74c3c;
      color: white; display: flex; align-items: center; justify-content: center;
      font-weight: 700; font-size: 13px; flex-shrink: 0;
    }

    .form-group { margin-bottom: 20px; }

    .form-group label {
      display: block; font-size: 12px; font-weight: 500; color: #888;
      text-transform: uppercase; letter-spacing: 1px; margin-bottom: 8px;
    }

    .form-group input {
      width: 100%; padding: 12px 16px; background: rgba(255,255,255,0.04);
      border: 1px solid rgba(255,255,255,0.08); border-radius: 8px;
      color: #e8e6e3; font-size: 14px; outline: none; transition: border-color 0.2s;
      box-sizing: border-box;
    }

    .form-group input:focus { border-color: #c9a96e; }

    .login-btn {
      width: 100%; padding: 12px; background: linear-gradient(135deg, #c9a96e, #8b6914);
      border: none; border-radius: 8px; color: #0a0a0f; font-weight: 600;
      font-size: 14px; cursor: pointer; transition: opacity 0.2s;
    }

    .login-btn:disabled { opacity: 0.5; cursor: not-allowed; }

    .demo-accounts {
      margin-top: 24px; padding-top: 20px; border-top: 1px solid rgba(255,255,255,0.06);
      text-align: center;
    }

    .demo-accounts p { font-size: 12px; color: #666; margin: 0 0 10px; }

    .demo-row { display: flex; gap: 8px; justify-content: center; flex-wrap: wrap; }

    .demo-row button {
      background: rgba(201,169,110,0.08); border: 1px solid rgba(201,169,110,0.2);
      color: #c9a96e; padding: 6px 14px; border-radius: 6px; font-size: 12px;
      cursor: pointer; transition: all 0.2s;
    }

    .demo-row button:hover { background: rgba(201,169,110,0.15); }
  `]
})
export class LoginComponent {
  email = '';
  password = '';
  loading = false;
  loginError = '';

  constructor(
    private loginService: LoginService,
    private authService: AuthService,
    private router: Router
  ) {}

  fillDemo(email: string, password: string) {
    this.email = email;
    this.password = password;
  }

  onLogin() {
    this.loading = true;
    this.loginError = '';

    this.loginService.login(this.email, this.password).subscribe({
      next: (res) => {
        this.loading = false;
        if (res.message === 'Login successful!') {
          this.router.navigate(['/admin/invoices']);
        }
      },
      error: (err: any) => {
        this.loading = false;
        const message = err.message || '';
        if (message.includes('not registered')) {
          this.loginError = 'This email is not registered.';
        } else if (message.includes('password')) {
          this.loginError = 'Incorrect password.';
        } else if (message.includes('locked')) {
          this.loginError = 'Account temporarily locked. Please try again later.';
        } else {
          this.loginError = message || 'Login failed. Please try again.';
        }
      }
    });
  }
}