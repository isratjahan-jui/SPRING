import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../../services/auth.service';
import { RegisterRequest } from '../../../../models/auth.model';

@Component({
  selector: 'app-hotel-owner-register',
  imports: [CommonModule, FormsModule],
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

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  saveOwner() {
    if (this.dto.password !== this.confirmPassword) {
      alert('Password and Confirm Password do not match.');
      return;
    }

    this.authService.register(this.dto).subscribe({
      next: (res) => {
        alert(res.message || 'Registration Successful! Please check your email to verify your account.');
        this.router.navigate(['/login']);
      },
      error: (err: any) => {
        console.error(err);
        alert(err.error?.message || 'Failed to Register');
      },
    });
  }

  resetForm() {
    this.dto = { name: '', email: '', password: '', phone: '', role: 'HOTEL_OWNER' };
    this.confirmPassword = '';
  }
}
