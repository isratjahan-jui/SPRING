import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router, NavigationEnd } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { NotificationDropdown } from '../notification-dropdown/notification-dropdown';
import { filter, map } from 'rxjs';

@Component({
  selector: 'app-header',
  imports: [CommonModule, RouterLink, NotificationDropdown],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header implements OnInit {
  private auth = inject(AuthService);
  private router = inject(Router);

  isLoggedIn = false;
  isDashboard = false;
  role: string | null = null;
  userName: string | null = null;
  userInitial = 'U';
  pageTitle = 'Dashboard';
  dashboardRoute = '/customer';

  private pageTitles: Record<string, string> = {
    '/admin': 'Admin Dashboard',
    '/admin/hotels/manage': 'Manage Hotels',
    '/admin/owners': 'Manage Owners',
    '/admin/customers': 'Manage Customers',
    '/admin/commissions': 'Commissions',
    '/admin/payments': 'Payments',
    '/owner': 'Owner Dashboard',
    '/owner/my-hotels': 'My Hotels',
    '/owner/hotel-details': 'Hotel Details',
    '/owner/rooms': 'Rooms',
    '/owner/facilities': 'Facilities',
    '/owner/gallery': 'Gallery',
    '/owner/food-items': 'Food Items',
    '/owner/bookings': 'Bookings',
    '/owner/deals': 'Deals & Offers',
    '/owner/extra-services': 'Extra Services',
    '/owner/coupons': 'Coupons',
    '/owner/wallet': 'Wallet',
    '/owner/payments': 'Payments',
    '/owner/notifications': 'Notifications',
    '/owner/reports': 'Reports',
    '/owner/profile': 'Profile',
    '/customer': 'Customer Dashboard',
    '/customer/bookings': 'My Bookings',
    '/customer/payments': 'Payments',
    '/customer/invoices': 'Invoices',
    '/customer/wishlist': 'Wishlist',
    '/customer/deals': 'Deals & Offers',
    '/customer/coupons': 'Coupons',
    '/customer/profile': 'Profile',
    '/customer/support': 'Support',
  };

  ngOnInit() {
    this.isLoggedIn = this.auth.isLoggedIn();
    this.role = this.auth.getRole();
    const email = this.auth.getUser()?.email;
    this.userName = email ?? null;
    if (email) {
      this.userInitial = email.charAt(0).toUpperCase();
    }

    if (this.role === 'ADMIN') {
      this.dashboardRoute = '/admin';
    } else if (this.role === 'HOTEL_OWNER') {
      this.dashboardRoute = '/owner';
    } else {
      this.dashboardRoute = '/customer';
    }

    this.router.events
      .pipe(
        filter((e) => e instanceof NavigationEnd),
        map((e: NavigationEnd) => e.urlAfterRedirects || e.url),
      )
      .subscribe((url) => {
        this.isDashboard =
          url.startsWith('/admin') || url.startsWith('/owner') || url.startsWith('/customer');
        this.pageTitle = this.pageTitles[url] || this.getPageTitle(url);
      });

    const currentUrl = this.router.url;
    this.isDashboard =
      currentUrl.startsWith('/admin') ||
      currentUrl.startsWith('/owner') ||
      currentUrl.startsWith('/customer');
    this.pageTitle = this.getPageTitle(currentUrl);
  }

  private getPageTitle(url: string): string {
    for (const [path, title] of Object.entries(this.pageTitles)) {
      if (url === path) return title;
    }
    return 'Dashboard';
  }

  logout() {
    this.auth.logout();
  }
}
