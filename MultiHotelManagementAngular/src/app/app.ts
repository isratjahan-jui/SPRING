import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Header } from './components/shared/header/header';
import { Footer } from './components/shared/footer/footer';
import { Sidebar } from './components/shared/sidebar/sidebar';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet,Header, Footer, Sidebar],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('MultiHotelManagementAngular');
}
