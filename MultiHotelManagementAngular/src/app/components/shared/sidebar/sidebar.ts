import { Component, inject, OnInit } from '@angular/core';
import { RouterLink, RouterLinkActive, Router, NavigationEnd } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { NotificationDropdown } from '../notification-dropdown/notification-dropdown';
import { NotificationService } from '../../../services/notification.service';
import { filter, map } from 'rxjs';
import { toSignal } from '@angular/core/rxjs-interop';
import { StorageService } from '../../../services/storage.service';

@Component({
  selector: 'app-sidebar',
  imports: [RouterLink, RouterLinkActive, NotificationDropdown],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar implements OnInit {
  private auth = inject(AuthService);
  private storage = inject(StorageService);
  private router = inject(Router);
  private notificationService = inject(NotificationService);
  role = () => this.auth.getRole();
  isLoggedIn = () => this.auth.isLoggedIn();
  userName = this.auth.getUser()?.email;


   userRole: string | null = null;

  ngOnInit() {
    this.notificationService.connect();
     this.userRole = this.storage.getRole();
    console.log(this.userRole);
  }

  // currentRole = toSignal(
  //   this.router.events.pipe(
  //     filter((e) => e instanceof NavigationEnd),
  //     map((e: NavigationEnd) => {
  //       const url = e.urlAfterRedirects || e.url;
  //       if (url.startsWith('/admin')) return 'ADMIN';
  //       if (url.startsWith('/owner')) return 'HOTEL_OWNER';
  //       if (url.startsWith('/customer')) return 'CUSTOMER';
  //       console.log(url);
  //       return this.role;
  //     }),
  //   ),
  //   { initialValue: this.role ?? this.#detectRoleFromUrl() },
  // );

  // #detectRoleFromUrl(): string | null {
  //   const url = this.router.url;
  //   if (url.startsWith('/admin')) return 'ADMIN';
  //   if (url.startsWith('/owner')) return 'HOTEL_OWNER';
  //   if (url.startsWith('/customer')) return 'CUSTOMER';
  //   return null;
  // }

  logout() {
    this.auth.logout();
  }
}
