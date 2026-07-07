import { Routes } from '@angular/router';
import { AddHotelOwnerComponent } from './components/hotel/add-hotel-owner-component/add-hotel-owner-component';
import { HomeComponent } from './components/shared/home/home';
import { LoginComponent } from './components/auth/login/login';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'hotel-owner', component: AddHotelOwnerComponent },
];
