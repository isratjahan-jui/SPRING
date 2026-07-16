import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../../services/auth.service';
import { RegisterRequest } from '../../../../models/auth.model';

@Component({
  selector: 'app-customer-register',
  imports: [CommonModule, FormsModule],
  templateUrl: './customer-register.html',
  styleUrl: './customer-register.css',
})
export class CustomerRegisterComponent {
  dto: RegisterRequest = {
    name: '',
    email: '',
    password: '',
    phone: '',
    role: 'CUSTOMER',
  };

  confirmPassword = '';

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  register(): void {
    if (this.dto.password !== this.confirmPassword) {
      alert('Passwords do not match');
      return;
    }

    this.authService.register(this.dto).subscribe({
      next: (res) => {
        alert(res.message || 'Registration Successful! Please check your email to verify your account.');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error(err);
        alert(err.error?.message || 'Registration Failed');
      },
    });
  }

  reset(): void {
    this.dto = { name: '', email: '', password: '', phone: '', role: 'CUSTOMER' };
    this.confirmPassword = '';
  }
}
