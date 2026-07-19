import { Component, inject, signal } from '@angular/core';
import { Router, RouterOutlet, NavigationEnd } from '@angular/router';
import { Header } from './components/shared/header/header';
import { Footer } from './components/shared/footer/footer';
import { Sidebar } from './components/shared/sidebar/sidebar';
import { filter, map } from 'rxjs';
import { toSignal } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header, Footer, Sidebar],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('TripNest');
  private router = inject(Router);

  private publicPaths = [
    '/',
    '/home',
    '/login',
    '/register',
    '/register/customer',
    '/register/hotel-owner',
  ];

  showPublicLayout = toSignal(
    this.router.events.pipe(
      filter((e) => e instanceof NavigationEnd),
      map((e: NavigationEnd) => {
        const url = e.urlAfterRedirects || e.url;
        return (
          this.publicPaths.includes(url) ||
          (url.startsWith('/hotels') &&
            !url.startsWith('/hotels/add') &&
            !url.startsWith('/hotels/edit'))
        );
      }),
    ),
    { initialValue: true },
  );

  showFooter = toSignal(
    this.router.events.pipe(
      filter((e) => e instanceof NavigationEnd),
      map((e: NavigationEnd) => {
        const url = e.urlAfterRedirects || e.url;
        return url === '/' || url === '/home';
      }),
    ),
    { initialValue: true },
  );
}
