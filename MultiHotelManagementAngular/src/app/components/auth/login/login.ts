import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css'],
})
export class LoginComponent {
  loginData = {
    email: '',
    password: '',
  };

  constructor(
    private auth: AuthService,
    private router: Router,
  ) {}

  login() {
    this.auth.login(this.loginData).subscribe({
      next: (res) => {
        this.auth.setSession(res);

        switch (res.role) {
          case 'ADMIN':
            this.router.navigate(['/admin']);
            break;

          case 'HOTEL_OWNER':
            this.router.navigate(['/owner']);
            break;

          case 'CUSTOMER':
            this.router.navigate(['/customer']);
            break;

          default:
            this.router.navigate(['/']);
        }
      },

      error: (err) => {
        alert('Invalid Email or Password');

        console.log(err);
      },
    });
  }
}
