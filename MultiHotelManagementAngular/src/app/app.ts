import { Component, importProvidersFrom, inject, signal } from '@angular/core';
import { Router, RouterOutlet, NavigationEnd, provideRouter } from '@angular/router';
import { Header } from './components/shared/header/header';
import { Footer } from './components/shared/footer/footer';

import { FormsModule } from '@angular/forms';


import { bootstrapApplication } from '@angular/platform-browser';
import { routes } from './app.routes';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, FormsModule, Header, Footer],
  templateUrl: './app.html',
  styleUrl: './app.css',
  
 
})
export class App {
  protected readonly title = signal('MultiHotelManagementAngular');
  private router = inject(Router);

 


}
// 🔥 এখানে একই ফাইল থেকে bootstrap করা হচ্ছ

bootstrapApplication(App, {
  providers: [
    provideRouter(routes),
    importProvidersFrom(HttpClientModule, FormsModule)
  ]
});
