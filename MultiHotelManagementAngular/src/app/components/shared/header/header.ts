import { Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-header',
  imports: [RouterLink],
  templateUrl: './header.html',
  styleUrl: './header.css',
})

// export class Header {
//   private auth = inject(AuthService);
//   isLoggedIn = this.auth.isLoggedIn;
//   role = this.auth.getRole();
//   userName = this.auth.getUser()?.email;

//   logout() {
//     this.auth.logout();
//   }
// }

export class Header implements OnInit {
  private auth = inject(AuthService);

  isLoggedIn = false;
  role: string | null = null;
  userName: string | null = null;

  ngOnInit() {
    this.isLoggedIn = this.auth.isLoggedIn();
    this.role = this.auth.getRole();
    this.userName = this.auth.getUser()?.email ?? null;
  }

  logout() {
    this.auth.logout();
  }
}