import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../../services/auth.service';
import { RegisterRequest } from '../../../../models/auth.model';

@Component({
  selector: 'app-customer-register',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './customer-register.html',
  styleUrl: './customer-register.css',
})
export class CustomerRegisterComponent implements OnInit {
  dto: RegisterRequest = {
    name: '',
    email: '',
    password: '',
    phone: '',
    role: 'CUSTOMER',
  };

  confirmPassword = '';
  showPassword = false;
  loading = false;
  returnUrl = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '';
  }

  register(): void {
    if (this.dto.password !== this.confirmPassword) {
      return;
    }

    this.loading = true;

    this.authService.register(this.dto).subscribe({
      next: (res) => {
        this.loading = false;
        alert(res.message || 'Registration Successful! Please check your email to verify your account.');
        const navExtras = this.returnUrl ? { queryParams: { returnUrl: this.returnUrl } } : {};
        this.router.navigate(['/login'], navExtras);
      },
      error: (err) => {
        this.loading = false;
        alert(err.error?.message || 'Registration Failed');
      },
    });
  }

  reset(): void {
    this.dto = { name: '', email: '', password: '', phone: '', role: 'CUSTOMER' };
    this.confirmPassword = '';
  }
}
