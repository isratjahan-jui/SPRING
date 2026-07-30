import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../../services/auth.service';
import { RegisterRequest } from '../../../../models/auth.model';

@Component({
  selector: 'app-hotel-owner-register',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './hotel-owner-register.html',
  styleUrl: './hotel-owner-register.css',
})
export class HotelOwnerRegister {
  dto: RegisterRequest = {
    name: '',
    email: '',
    password: '',
    phone: '',
    role: 'HOTEL_OWNER',
  };

  confirmPassword: string = '';
  showPassword = false;
  loading = false;

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  saveOwner() {
    if (this.dto.password !== this.confirmPassword) {
      return;
    }

    this.loading = true;

    this.authService.register(this.dto).subscribe({
      next: (res) => {
        this.loading = false;
        alert(res.message || 'Registration Successful! Please check your email to verify your account.');
        this.router.navigate(['/login']);
      },
      error: (err: any) => {
        this.loading = false;
        alert(err.error?.message || 'Failed to Register');
      },
    });
  }

  resetForm() {
    this.dto = { name: '', email: '', password: '', phone: '', role: 'HOTEL_OWNER' };
    this.confirmPassword = '';
  }
}
