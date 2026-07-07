import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isLoggedIn()) return true;
  return router.parseUrl('/login');
};

export const roleGuard = (...roles: string[]): CanActivateFn => {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);

    if (!auth.isLoggedIn()) return router.parseUrl('/login');
    if (roles.includes(auth.role() ?? '')) return true;
    return router.parseUrl('/');
  };
};

export const loginGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isLoggedIn()) {
    const role = auth.role();
    if (role === 'ADMIN') return router.parseUrl('/admin');
    if (role === 'HOTEL_OWNER') return router.parseUrl('/owner');
    if (role === 'CUSTOMER') return router.parseUrl('/customer');
    return router.parseUrl('/');
  }

  return true;
};

export const AuthGuard: CanActivateFn = (route) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.isLoggedIn()) return router.parseUrl('/login');

  const requiredRole = route.data?.['role'];
  if (requiredRole && auth.role() !== requiredRole) {
    return router.parseUrl('/');
  }

  return true;
};
