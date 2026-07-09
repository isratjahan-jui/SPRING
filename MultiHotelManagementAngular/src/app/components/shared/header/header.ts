import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-header',
  imports: [RouterLink],
  templateUrl: './header.html',
  styleUrl: './header.css',
})
export class Header {
  private auth = inject(AuthService);
  isLoggedIn = this.auth.isLoggedIn;
  role = this.auth.getRole();
  userName = this.auth.getUser()?.email;

  logout() {
    this.auth.logout();
  }
}
