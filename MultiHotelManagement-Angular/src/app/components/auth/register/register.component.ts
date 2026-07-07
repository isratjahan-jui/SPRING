import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { RegisterRequest } from '../../../models/auth.model';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
})
export class RegisterComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  model: RegisterRequest = {
    name: '',
    email: '',
    password: '',
    phone: '',
    role: 'CUSTOMER',
  };
  confirmPassword = '';
  loading = false;
  message = '';
  error = '';

  onSubmit() {
    if (this.model.password !== this.confirmPassword) {
      this.error = 'Passwords do not match';
      return;
    }
    this.loading = true;
    this.error = '';
    this.message = '';
    this.auth.register(this.model).subscribe({
      next: (res) => {
        this.message = res.message;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Registration failed';
        this.loading = false;
      },
    });
  }
}
