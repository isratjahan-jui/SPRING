import { Routes } from '@angular/router';

import { HomeComponent } from './components/shared/home/home';
import { Login } from './components/auth/login/login';
import { RegisterComponent } from './components/auth/register/register';
import { CustomerRegisterComponent } from './components/auth/register/customer-register/customer-register';
import { HotelOwnerRegister } from './components/auth/register/hotel-owner-register/hotel-owner-register';
import { ForgetPassword } from './components/auth/forget-password/forget-password';
import { ResetPassword } from './components/auth/reset-password/reset-password';
import { VeryfyEmail } from './components/auth/veryfy-email/veryfy-email';

import { CustomerBookings } from './components/customer/bookings/bookings';
import { CustomerWishlist } from './components/customer/wishlist/wishlist';
import { BookHotel } from './components/customer/book-hotel/book-hotel';
import { BookingDetails } from './components/customer/booking-details/booking-details';

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
import { OwnerHotelDetails } from './components/hotelowner/hotel-details/hotel-details';
import { OwnerBookings } from './components/hotelowner/bookings/bookings';
import { Payments } from './components/hotelowner/payments/payments';
import { RoleRedirect } from './components/auth/role-redirect/role-redirect';

// import { authGuard, roleGuard } from './guards/auth.guards';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'home', component: HomeComponent },
  { path: 'login', component: Login },
  { path: 'register', component: RegisterComponent },
  { path: 'register/customer', component: CustomerRegisterComponent },
  { path: 'register/hotel-owner', component: HotelOwnerRegister },
  { path: 'forgot-password', component: ForgetPassword },
  { path: 'reset-password', component: ResetPassword },
  { path: 'verify-email', component: VeryfyEmail },

  { path: 'hotels', component: HotelList },
  { path: 'hotels/add', component: AddHotel },
  { path: 'hotels/edit/:id', component: EditHotel },
  { path: 'hotels/:id', component: HotelDetails },
  { path: 'book-hotel/:hotelId/:roomId', component: BookHotel },

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
  { path: 'owner/hotel-details', component: OwnerHotelDetails },
  { path: 'owner/bookings', component: OwnerBookings },
  { path: 'owner/payments', component: Payments },

  { path: 'customer', component: CustomerDashboard },
  { path: 'customer/bookings', component: CustomerBookings },
  { path: 'customer/booking/:id', component: BookingDetails },
  { path: 'customer/wishlist', component: CustomerWishlist },

  { path: 'dashboard', component: RoleRedirect },

  // { path: 'admin', canActivate: [authGuard, roleGuard('ADMIN')], component: AdminDashboard },
  // { path: 'owner', canActivate: [authGuard, roleGuard('HOTEL_OWNER')], component: OwnerDashboard },
  // { path: 'customer', canActivate: [authGuard, roleGuard('CUSTOMER')], component: CustomerDashboard},
];
