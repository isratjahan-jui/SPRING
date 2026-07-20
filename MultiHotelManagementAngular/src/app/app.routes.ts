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
import { MakePayment } from './components/customer/make-payment/make-payment';
import { PaymentResult } from './components/customer/payment-result/payment-result';
import { CustomerInvoices } from './components/customer/invoices/invoices';

import { HotelList } from './components/hotel/hotel-list/hotel-list';
import { AddHotel } from './components/hotel/add-hotel/add-hotel';
import { EditHotel } from './components/hotel/edit-hotel/edit-hotel';
import { HotelDetails } from './components/hotel/hotel-details/hotel-details';

import { AdminDashboard } from './components/admin/dashboard/dashboard';
import { AdminOwners } from './components/admin/owners/owners';
import { AdminCustomers } from './components/admin/customers/customers';
import { AdminCommissions } from './components/admin/commissions/commissions';
import { AdminPayments } from './components/admin/payments/payments';
import { AdminNotifications } from './components/admin/notifications/notifications';
import { AdminSupport } from './components/admin/support/support';
import { AdminLocations } from './components/admin/locations/locations';
import { CustomerPayments } from './components/customer/payments/payments';
import { CustomerProfile } from './components/customer/profile/profile';
import { CustomerSupport } from './components/customer/support/support';
import { CustomerNotifications } from './components/customer/notifications/notifications';
import { CustomerWallet } from './components/customer/wallet/wallet';
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
import { OwnerBookingRooms } from './components/hotelowner/booking-rooms/booking-rooms';
import { OwnerDeals } from './components/hotelowner/deals/deals';
import { OwnerExtraServices } from './components/hotelowner/extra-services/extra-services';
import { ExtraServicesBookingComponent } from './components/hotelowner/extra-services-booking/extra-services-booking';
import { OwnerCoupons } from './components/hotelowner/coupons/coupons';
import { OwnerWallet } from './components/hotelowner/wallet/wallet';
import { OwnerNotifications } from './components/hotelowner/notifications/notifications';
import { OwnerReports } from './components/hotelowner/reports/reports';
import { OwnerProfile } from './components/hotelowner/profile/profile';
import { CustomerDeals } from './components/customer/deals/deals';
import { CustomerCoupons } from './components/customer/coupons/coupons';
import { Payments } from './components/hotelowner/payments/payments';
import { RoleRedirect } from './components/auth/role-redirect/role-redirect';

import { authGuard, roleGuard } from './guards/auth.guards';

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
  { path: 'deals', component: CustomerDeals },
  { path: 'hotels/add', component: AddHotel, canActivate: [authGuard, roleGuard(['HOTEL_OWNER'])] },
  {
    path: 'hotels/edit/:id',
    component: EditHotel,
    canActivate: [authGuard, roleGuard(['HOTEL_OWNER'])],
  },
  { path: 'hotels/:id', component: HotelDetails },
  {
    path: 'book-hotel/:hotelId/:roomId',
    component: BookHotel,
    canActivate: [authGuard, roleGuard(['CUSTOMER'])],
  },

  { path: 'admin', component: AdminDashboard, canActivate: [authGuard, roleGuard(['ADMIN'])] },
  {
    path: 'admin/hotels/manage',
    component: DeleteHotel,
    canActivate: [authGuard, roleGuard(['ADMIN'])],
  },
  { path: 'admin/owners', component: AdminOwners, canActivate: [authGuard, roleGuard(['ADMIN'])] },
  {
    path: 'admin/customers',
    component: AdminCustomers,
    canActivate: [authGuard, roleGuard(['ADMIN'])],
  },
  {
    path: 'admin/commissions',
    component: AdminCommissions,
    canActivate: [authGuard, roleGuard(['ADMIN'])],
  },
  {
    path: 'admin/payments',
    component: AdminPayments,
    canActivate: [authGuard, roleGuard(['ADMIN'])],
  },
  {
    path: 'admin/notifications',
    component: AdminNotifications,
    canActivate: [authGuard, roleGuard(['ADMIN'])],
  },
  {
    path: 'admin/support',
    component: AdminSupport,
    canActivate: [authGuard, roleGuard(['ADMIN'])],
  },
  {
    path: 'admin/locations',
    component: AdminLocations,
    canActivate: [authGuard, roleGuard(['ADMIN'])],
  },

  {
    path: 'owner',
    component: OwnerDashboard,
    canActivate: [authGuard, roleGuard(['HOTEL_OWNER'])],
  },
  {
    path: 'owner/my-hotels',
    component: MyHotels,
    canActivate: [authGuard, roleGuard(['HOTEL_OWNER'])],
  },
  {
    path: 'owner/rooms',
    component: OwnerRooms,
    canActivate: [authGuard, roleGuard(['HOTEL_OWNER'])],
  },
  {
    path: 'owner/facilities',
    component: OwnerFacilities,
    canActivate: [authGuard, roleGuard(['HOTEL_OWNER'])],
  },
  {
    path: 'owner/gallery',
    component: OwnerGallery,
    canActivate: [authGuard, roleGuard(['HOTEL_OWNER'])],
  },
  {
    path: 'owner/food-items',
    component: OwnerFoodItems,
    canActivate: [authGuard, roleGuard(['HOTEL_OWNER'])],
  },
  {
    path: 'owner/hotel-details',
    component: OwnerHotelDetails,
    canActivate: [authGuard, roleGuard(['HOTEL_OWNER'])],
  },
  {
    path: 'owner/bookings',
    component: OwnerBookings,
    canActivate: [authGuard, roleGuard(['HOTEL_OWNER'])],
  },
  {
    path: 'owner/booking-rooms',
    component: OwnerBookingRooms,
    canActivate: [authGuard, roleGuard(['HOTEL_OWNER'])],
  },
  {
    path: 'owner/payments',
    component: Payments,
    canActivate: [authGuard, roleGuard(['HOTEL_OWNER'])],
  },
  {
    path: 'owner/deals',
    component: OwnerDeals,
    canActivate: [authGuard, roleGuard(['HOTEL_OWNER'])],
  },
  {
    path: 'owner/extra-services',
    component: OwnerExtraServices,
    canActivate: [authGuard, roleGuard(['HOTEL_OWNER'])],
  },
  {
    path: 'owner/extra-services-booking',
    component: ExtraServicesBookingComponent,
    canActivate: [authGuard, roleGuard(['HOTEL_OWNER'])],
  },
  {
    path: 'owner/coupons',
    component: OwnerCoupons,
    canActivate: [authGuard, roleGuard(['HOTEL_OWNER'])],
  },
  {
    path: 'owner/wallet',
    component: OwnerWallet,
    canActivate: [authGuard, roleGuard(['HOTEL_OWNER'])],
  },
  {
    path: 'owner/notifications',
    component: OwnerNotifications,
    canActivate: [authGuard, roleGuard(['HOTEL_OWNER'])],
  },
  {
    path: 'owner/reports',
    component: OwnerReports,
    canActivate: [authGuard, roleGuard(['HOTEL_OWNER'])],
  },
  {
    path: 'owner/profile',
    component: OwnerProfile,
    canActivate: [authGuard, roleGuard(['HOTEL_OWNER'])],
  },

  {
    path: 'customer',
    component: CustomerDashboard,
    canActivate: [authGuard, roleGuard(['CUSTOMER'])],
  },
  {
    path: 'customer/bookings',
    component: CustomerBookings,
    canActivate: [authGuard, roleGuard(['CUSTOMER'])],
  },
  {
    path: 'customer/payments',
    component: CustomerPayments,
    canActivate: [authGuard, roleGuard(['CUSTOMER'])],
  },
  {
    path: 'customer/booking/:id',
    component: BookingDetails,
    canActivate: [authGuard, roleGuard(['CUSTOMER'])],
  },
  {
    path: 'customer/pay/:bookingId',
    component: MakePayment,
    canActivate: [authGuard, roleGuard(['CUSTOMER'])],
  },
  {
    path: 'customer/payment-result',
    component: PaymentResult,
    canActivate: [authGuard, roleGuard(['CUSTOMER'])],
  },
  {
    path: 'customer/invoices',
    component: CustomerInvoices,
    canActivate: [authGuard, roleGuard(['CUSTOMER'])],
  },
  {
    path: 'customer/wishlist',
    component: CustomerWishlist,
    canActivate: [authGuard, roleGuard(['CUSTOMER'])],
  },
  {
    path: 'customer/profile',
    component: CustomerProfile,
    canActivate: [authGuard, roleGuard(['CUSTOMER'])],
  },
  {
    path: 'customer/support',
    component: CustomerSupport,
    canActivate: [authGuard, roleGuard(['CUSTOMER'])],
  },
  {
    path: 'customer/notifications',
    component: CustomerNotifications,
    canActivate: [authGuard, roleGuard(['CUSTOMER'])],
  },
  {
    path: 'customer/wallet',
    component: CustomerWallet,
    canActivate: [authGuard, roleGuard(['CUSTOMER'])],
  },
  {
    path: 'customer/deals',
    component: CustomerDeals,
    canActivate: [authGuard, roleGuard(['CUSTOMER'])],
  },
  {
    path: 'customer/coupons',
    component: CustomerCoupons,
    canActivate: [authGuard, roleGuard(['CUSTOMER'])],
  },

  { path: 'dashboard', component: RoleRedirect },
];
