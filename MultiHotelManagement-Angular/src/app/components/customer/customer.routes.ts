import { Routes } from '@angular/router';
import { CustomerLayoutComponent } from './customer-layout/customer-layout.component';

export default [
  {
    path: '',
    component: CustomerLayoutComponent,
    children: [
      { path: '', loadComponent: () => import('./dashboard/dashboard.component').then(c => c.CustomerDashboardComponent) },
      { path: 'bookings', loadComponent: () => import('./bookings/bookings.component').then(c => c.CustomerBookingsComponent) },
      { path: 'wishlist', loadComponent: () => import('./wishlist/wishlist.component').then(c => c.CustomerWishlistComponent) },
      { path: 'profile', loadComponent: () => import('./profile/profile.component').then(c => c.CustomerProfileComponent) },
      { path: 'support', loadComponent: () => import('./support/support.component').then(c => c.CustomerSupportComponent) },
    ],
  },
] as Routes;
