import { Routes } from '@angular/router';

import { HomeComponent } from './components/shared/home/home';
import { LoginComponent } from './components/auth/login/login';
import { RegisterComponent } from './components/auth/register/register';
import { CustomerRegisterComponent } from './components/auth/register/customer-register/customer-register';
import { HotelOwnerRegister } from './components/auth/register/hotel-owner-register/hotel-owner-register';

import { CustomerBookings } from './components/customer/bookings/bookings';
import { CustomerWishlist } from './components/customer/wishlist/wishlist';
import { CustomerProfile } from './components/customer/profile/profile';
import { CustomerSupport } from './components/customer/support/support';

import { HotelList } from './components/hotel/hotel-list/hotel-list';
import { AddHotel } from './components/hotel/add-hotel/add-hotel';
import { EditHotel } from './components/hotel/edit-hotel/edit-hotel';
import { HotelDetails } from './components/hotel/hotel-details/hotel-details';

import { AdminDashboard } from './components/admin/dashboard/dashboard';
import { AdminOwners } from './components/admin/owners/owners';
import { AdminCustomers } from './components/admin/customers/customers';
import { AdminCommissions } from './components/admin/commissions/commissions';
import { DeleteHotel } from './components/hotel/delete-hotel/delete-hotel';
import { OwnerDashboard } from './components/hotelowner/dashboard/dashboard';
import { CustomerDashboard } from './components/customer/dashboard/dashboard';

import { MyHotels } from './components/hotelowner/my-hotels/my-hotels';
import { OwnerRooms } from './components/hotelowner/rooms/rooms';
import { OwnerFacilities } from './components/hotelowner/facilities/facilities';
import { OwnerGallery } from './components/hotelowner/gallery/gallery';
import { OwnerFoodItems } from './components/hotelowner/food-items/food-items';
import { OwnerBookings } from './components/hotelowner/bookings/bookings';

// import { authGuard, roleGuard } from './guards/auth.guards';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'register/customer', component: CustomerRegisterComponent },
  { path: 'register/hotel-owner', component: HotelOwnerRegister },

  { path: 'hotels', component: HotelList },
  { path: 'hotels/add', component: AddHotel },
  { path: 'hotels/edit/:id', component: EditHotel },
  { path: 'hotels/:id', component: HotelDetails },

  { path: 'admin', component: AdminDashboard },
  { path: 'admin/hotels/manage', component: DeleteHotel },
  { path: 'admin/owners', component: AdminOwners },
  { path: 'admin/customers', component: AdminCustomers },
  { path: 'admin/commissions', component: AdminCommissions },
  { path: 'owner', component: OwnerDashboard },
  { path: 'owner/my-hotels', component: MyHotels },
  { path: 'owner/rooms', component: OwnerRooms },
  { path: 'owner/facilities', component: OwnerFacilities },
  { path: 'owner/gallery', component: OwnerGallery },
  { path: 'owner/food-items', component: OwnerFoodItems },
  { path: 'owner/bookings', component: OwnerBookings },
  { path: 'customer', component: CustomerDashboard },
  { path: 'customer/bookings', component: CustomerBookings },
  { path: 'customer/wishlist', component: CustomerWishlist },
  { path: 'customer/profile', component: CustomerProfile },
  { path: 'customer/support', component: CustomerSupport },

  // { path: 'admin', canActivate: [authGuard, roleGuard('ADMIN')], component: AdminDashboard },
  // { path: 'owner', canActivate: [authGuard, roleGuard('HOTEL_OWNER')], component: OwnerDashboard },
  // { path: 'customer', canActivate: [authGuard, roleGuard('CUSTOMER')], component: CustomerDashboard},
];
