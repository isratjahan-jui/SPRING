import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  email = '';
  password = '';
  loading = false;
  error = '';

  onSubmit() {
    this.loading = true;
    this.error = '';
    this.auth.login({ email: this.email, password: this.password }).subscribe({
      next: (res) => {
        this.auth.setSession(res);
        const role = res.role;
        if (role === 'ADMIN') this.router.navigate(['/admin']);
        else if (role === 'HOTEL_OWNER') this.router.navigate(['/owner']);
        else if (role === 'CUSTOMER') this.router.navigate(['/customer']);
        else this.router.navigate(['/home']);
      },
      error: (err) => {
        this.error = err.error?.message || 'Login failed';
        this.loading = false;
      },
    });
  }
}
