import { Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'home', loadComponent: () => import('./components/public/home/home.component').then(c => c.HomeComponent) },
  { path: 'hotels', loadChildren: () => import('./components/public/hotel.routes') },
  { path: 'login', loadComponent: () => import('./components/auth/login/login.component').then(c => c.LoginComponent) },
  { path: 'register', loadComponent: () => import('./components/auth/register/register.component').then(c => c.RegisterComponent) },
  { path: 'verify-email', loadComponent: () => import('./components/auth/verify-email/verify-email.component').then(c => c.VerifyEmailComponent) },
  { path: 'forgot-password', loadComponent: () => import('./components/auth/forgot-password/forgot-password.component').then(c => c.ForgotPasswordComponent) },
  { path: 'reset-password', loadComponent: () => import('./components/auth/reset-password/reset-password.component').then(c => c.ResetPasswordComponent) },
  {
    path: 'admin',
    canActivate: [AuthGuard],
    data: { role: 'ADMIN' },
    loadChildren: () => import('./components/admin/admin.routes'),
  },
  {
    path: 'owner',
    canActivate: [AuthGuard],
    data: { role: 'HOTEL_OWNER' },
    loadChildren: () => import('./components/owner/owner.routes'),
  },
  {
    path: 'customer',
    canActivate: [AuthGuard],
    data: { role: 'CUSTOMER' },
    loadChildren: () => import('./components/customer/customer.routes'),
  },
  { path: '**', redirectTo: '/home' },
];
