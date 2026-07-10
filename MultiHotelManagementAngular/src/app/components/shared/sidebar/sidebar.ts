import { Component, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, Router, NavigationEnd } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { filter, map } from 'rxjs';
import { toSignal } from '@angular/core/rxjs-interop';


@Component({
  selector: 'app-sidebar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar {
  
  private auth = inject(AuthService);
  private router = inject(Router);
  role = this.auth.getRole;
  isLoggedIn = this.auth.isLoggedIn;
  userName = this.auth.getUser()?.email;

  currentRole = toSignal(
    this.router.events.pipe(
      filter((e) => e instanceof NavigationEnd),
      map((e: NavigationEnd) => {
        const url = e.urlAfterRedirects || e.url;
        if (url.startsWith('/admin')) return 'ADMIN';
        if (url.startsWith('/owner')) return 'HOTEL_OWNER';
        if (url.startsWith('/customer')) return 'CUSTOMER';
        console.log(url);
        return this.role;
      }),
    ),
    { initialValue: this.role ?? this.#detectRoleFromUrl() },
  );

  #detectRoleFromUrl(): string | null {
    const url = this.router.url;
    if (url.startsWith('/admin')) return 'ADMIN';
    if (url.startsWith('/owner')) return 'HOTEL_OWNER';
    if (url.startsWith('/customer')) return 'CUSTOMER';
    return null;
  }

  logout() {
    this.auth.logout();
  }
}
