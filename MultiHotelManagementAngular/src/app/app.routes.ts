import { Routes } from '@angular/router';
import { AddHotelOwnerComponent } from './components/hotel/add-hotel-owner-component/add-hotel-owner-component';
import { HomeComponent } from './components/shared/home/home';
import { LoginComponent } from './components/auth/login/login';
import { RegisterComponent } from './components/auth/register/register';
import { CustomerRegisterComponent } from './components/auth/register/customer-register/customer-register';

import { HotelList } from './components/hotel/hotel-list/hotel-list';
import { AddHotel } from './components/hotel/add-hotel/add-hotel';
import { EditHotel} from './components/hotel/edit-hotel/edit-hotel';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'register/customer', component: CustomerRegisterComponent },
  { path: 'register/hotel-owner', component: AddHotelOwnerComponent },
  { path: 'hotel-owner', component: AddHotelOwnerComponent },
  
  { path: 'hotels', component: HotelList },        
  { path: 'hotels/add', component: AddHotel},      
  { path: 'hotels/edit/:id', component: EditHotel},
    // Delete → handled inside HotelListComponent (button + service call)

];
