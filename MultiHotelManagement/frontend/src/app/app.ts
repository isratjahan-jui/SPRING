import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    @if (auth.isLoggedIn) {
      <div class="layout">
        <aside class="sidebar">
          <div class="sidebar-brand">
            <div class="brand-icon">M</div>
            <span class="brand-text">MHM</span>
          </div>

          <nav class="sidebar-nav">
            @if (auth.isAdmin) {
              <a routerLink="/admin/invoices" routerLinkActive="active" class="nav-item">
                <span class="nav-icon">&#9783;</span>
                <span>Invoices</span>
              </a>
              <a routerLink="/admin/receipts" routerLinkActive="active" class="nav-item">
                <span class="nav-icon">&#9998;</span>
                <span>Receipts</span>
              </a>
              <a routerLink="/admin/payments" routerLinkActive="active" class="nav-item">
                <span class="nav-icon">&#9878;</span>
                <span>Payments</span>
              </a>
            }
            @if (auth.isHotelOwner) {
              <a routerLink="/owner/invoices" routerLinkActive="active" class="nav-item">
                <span class="nav-icon">&#9783;</span>
                <span>Invoices</span>
              </a>
              <a routerLink="/owner/receipts" routerLinkActive="active" class="nav-item">
                <span class="nav-icon">&#9998;</span>
                <span>Receipts</span>
              </a>
            }
            @if (auth.isCustomer) {
              <a routerLink="/customer/invoices" routerLinkActive="active" class="nav-item">
                <span class="nav-icon">&#9783;</span>
                <span>My Invoices</span>
              </a>
              <a routerLink="/customer/receipts" routerLinkActive="active" class="nav-item">
                <span class="nav-icon">&#9998;</span>
                <span>My Receipts</span>
              </a>
            }
          </nav>

          <div class="sidebar-footer">
            <div class="user-info">
              <div class="user-avatar">{{ auth.currentUser?.name?.charAt(0) || 'U' }}</div>
              <div class="user-details">
                <span class="user-name">{{ auth.currentUser?.name }}</span>
                <span class="user-role">{{ auth.currentUser?.role }}</span>
              </div>
            </div>
            <button class="logout-btn" (click)="auth.logout()">Logout</button>
          </div>
        </aside>

        <main class="main-content">
          <router-outlet />
        </main>
      </div>
    } @else {
      <div class="login-wrapper">
        <div class="login-card">
          <div class="login-brand">
            <div class="brand-icon large">M</div>
            <h1>Multi Hotel Management</h1>
            <p>Sign in to your account</p>
          </div>
          <form (submit)="onLogin($event)">
            <div class="form-group">
              <label>Email</label>
              <input type="email" [(ngModel)]="loginEmail" name="email" placeholder="admin&#64;mhm.com" required>
            </div>
            <div class="form-group">
              <label>Password</label>
              <input type="password" [(ngModel)]="loginPassword" name="password" placeholder="password" required>
            </div>
            @if (loginError) {
              <div class="error-msg">{{ loginError }}</div>
            }
            <button type="submit" class="login-btn" [disabled]="loginLoading">
              {{ loginLoading ? 'Signing in...' : 'Sign In' }}
            </button>
          </form>
          <div class="demo-accounts">
            <p>Demo Accounts:</p>
            <button (click)="fillDemo('admin@mhm.com', 'admin123')">Admin</button>
            <button (click)="fillDemo('owner@mhm.com', 'owner123')">Hotel Owner</button>
            <button (click)="fillDemo('customer@mhm.com', 'cust123')">Customer</button>
          </div>
        </div>
      </div>
    }
  `,
  styles: [`
    :host { display: block; height: 100vh; font-family: 'DM Sans', sans-serif; }

    .layout { display: flex; height: 100vh; background: #0a0a0f; color: #e8e6e3; }

    .sidebar {
      width: 260px; background: #111118; border-right: 1px solid rgba(255,255,255,0.06);
      display: flex; flex-direction: column; padding: 0;
    }

    .sidebar-brand {
      padding: 24px 20px; display: flex; align-items: center; gap: 12px;
      border-bottom: 1px solid rgba(255,255,255,0.06);
    }

    .brand-icon {
      width: 36px; height: 36px; background: linear-gradient(135deg, #c9a96e, #8b6914);
      border-radius: 8px; display: flex; align-items: center; justify-content: center;
      font-weight: 700; font-size: 18px; color: #0a0a0f;
    }

    .brand-icon.large { width: 56px; height: 56px; font-size: 28px; border-radius: 14px; }

    .brand-text { font-weight: 600; font-size: 16px; letter-spacing: 2px; color: #c9a96e; }

    .sidebar-nav { flex: 1; padding: 16px 12px; display: flex; flex-direction: column; gap: 4px; }

    .nav-item {
      display: flex; align-items: center; gap: 12px; padding: 12px 16px;
      border-radius: 8px; color: #8a8a8a; text-decoration: none; font-size: 14px;
      transition: all 0.2s ease; cursor: pointer;
    }

    .nav-item:hover { background: rgba(201,169,110,0.08); color: #c9a96e; }

    .nav-item.active {
      background: rgba(201,169,110,0.12); color: #c9a96e;
      box-shadow: inset 3px 0 0 #c9a96e;
    }

    .nav-icon { font-size: 18px; width: 24px; text-align: center; }

    .sidebar-footer {
      padding: 16px 20px; border-top: 1px solid rgba(255,255,255,0.06);
      display: flex; align-items: center; justify-content: space-between;
    }

    .user-info { display: flex; align-items: center; gap: 10px; }

    .user-avatar {
      width: 32px; height: 32px; border-radius: 50%; background: #c9a96e;
      display: flex; align-items: center; justify-content: center;
      font-weight: 600; font-size: 13px; color: #0a0a0f;
    }

    .user-details { display: flex; flex-direction: column; }

    .user-name { font-size: 13px; font-weight: 500; }

    .user-role { font-size: 11px; color: #666; text-transform: uppercase; letter-spacing: 1px; }

    .logout-btn {
      background: none; border: 1px solid rgba(255,255,255,0.1); color: #888;
      padding: 6px 12px; border-radius: 6px; font-size: 12px; cursor: pointer;
      transition: all 0.2s;
    }

    .logout-btn:hover { border-color: #e74c3c; color: #e74c3c; }

    .main-content { flex: 1; overflow-y: auto; padding: 32px; }

    /* Login */
    .login-wrapper {
      height: 100vh; display: flex; align-items: center; justify-content: center;
      background: #0a0a0f;
    }

    .login-card {
      width: 400px; background: #111118; border: 1px solid rgba(255,255,255,0.06);
      border-radius: 16px; padding: 40px;
    }

    .login-brand { text-align: center; margin-bottom: 32px; }

    .login-brand .brand-icon { margin: 0 auto 16px; }

    .login-brand h1 { font-size: 20px; font-weight: 600; margin: 0 0 4px; }

    .login-brand p { color: #666; font-size: 14px; margin: 0; }

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

    .error-msg {
      background: rgba(231,76,60,0.1); border: 1px solid rgba(231,76,60,0.3);
      color: #e74c3c; padding: 10px 14px; border-radius: 8px; font-size: 13px;
      margin-bottom: 16px;
    }

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

    .demo-accounts button {
      background: rgba(201,169,110,0.08); border: 1px solid rgba(201,169,110,0.2);
      color: #c9a96e; padding: 6px 14px; border-radius: 6px; font-size: 12px;
      cursor: pointer; margin: 0 4px; transition: all 0.2s;
    }

    .demo-accounts button:hover { background: rgba(201,169,110,0.15); }
  `]
})
export class AppComponent {
  loginEmail = '';
  loginPassword = '';
  loginError = '';
  loginLoading = false;

  constructor(public auth: AuthService) {}

  onLogin(e: Event) {
    e.preventDefault();
    this.loginLoading = true;
    this.loginError = '';
    this.auth.login(this.loginEmail, this.loginPassword).subscribe({
      next: () => { this.loginLoading = false; },
      error: (err) => {
        this.loginLoading = false;
        this.loginError = err.error?.message || 'Login failed. Check credentials.';
      }
    });
  }

  fillDemo(email: string, password: string) {
    this.loginEmail = email;
    this.loginPassword = password;
  }
}
