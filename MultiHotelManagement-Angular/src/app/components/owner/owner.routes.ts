import { Routes } from '@angular/router';
import { OwnerLayoutComponent } from './owner-layout/owner-layout.component';

export default [
  {
    path: '',
    component: OwnerLayoutComponent,
    children: [
      { path: '', loadComponent: () => import('./dashboard/dashboard.component').then(c => c.OwnerDashboardComponent) },
      { path: 'my-hotels', loadComponent: () => import('./my-hotels/my-hotels.component').then(c => c.MyHotelsComponent) },
      { path: 'rooms', loadComponent: () => import('./rooms/rooms.component').then(c => c.OwnerRoomsComponent) },
      { path: 'bookings', loadComponent: () => import('./bookings/bookings.component').then(c => c.OwnerBookingsComponent) },
      { path: 'facilities', loadComponent: () => import('./facilities/facilities.component').then(c => c.OwnerFacilitiesComponent) },
      { path: 'food-items', loadComponent: () => import('./food-items/food-items.component').then(c => c.OwnerFoodItemsComponent) },
      { path: 'gallery', loadComponent: () => import('./gallery/gallery.component').then(c => c.OwnerGalleryComponent) },
    ],
  },
] as Routes;
