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
      <router-outlet />
    }
  `,
  styles: [`
    :host { display: block; height: 100vh; font-family: -apple-system, BlinkMacSystemFont, sans-serif; }

    .layout { display: flex; height: 100vh; background: #0a0a0f; color: #e8e6e3; }

    .sidebar {
      width: 260px; background: #111118; border-right: 1px solid rgba(255,255,255,0.06);
      display: flex; flex-direction: column;
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

    .brand-text { font-weight: 600; font-size: 16px; letter-spacing: 2px; color: #c9a96e; }

    .sidebar-nav { flex: 1; padding: 16px 12px; display: flex; flex-direction: column; gap: 4px; }

    .nav-item {
      display: flex; align-items: center; gap: 12px; padding: 12px 16px;
      border-radius: 8px; color: #8a8a8a; text-decoration: none; font-size: 14px;
      transition: all 0.2s; cursor: pointer;
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
  `]
})
export class AppComponent {
  constructor(public auth: AuthService) {}
}