import { Routes } from '@angular/router';
import { AdminLayoutComponent } from './admin-layout/admin-layout.component';

export default [
  {
    path: '',
    component: AdminLayoutComponent,
    children: [
      { path: '', loadComponent: () => import('./dashboard/dashboard.component').then(c => c.AdminDashboardComponent) },
      { path: 'hotels', loadComponent: () => import('./hotels/hotels.component').then(c => c.AdminHotelsComponent) },
      { path: 'owners', loadComponent: () => import('./owners/owners.component').then(c => c.AdminOwnersComponent) },
      { path: 'customers', loadComponent: () => import('./customers/customers.component').then(c => c.AdminCustomersComponent) },
      { path: 'commissions', loadComponent: () => import('./commissions/commissions.component').then(c => c.AdminCommissionsComponent) },
    ],
  },
] as Routes;
