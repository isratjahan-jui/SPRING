import { Routes } from '@angular/router';

export default [
  { path: '', loadComponent: () => import('./hotel-list/hotel-list.component').then(c => c.HotelListComponent) },
  { path: ':id', loadComponent: () => import('./hotel-detail/hotel-detail.component').then(c => c.HotelDetailComponent) },
] as Routes;
