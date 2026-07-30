import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { LoginRequest } from '../../../models/auth.model';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrls: ['./login.css'],
})
export class Login implements OnInit {
  dto: LoginRequest = { email: '', password: '' };

  showPassword = false;
  loading = false;
  errorMessage: string | null = null;
  returnUrl = '/dashboard';

  constructor(
    private auth: AuthService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/dashboard';
  }

  login(): void {
    this.loading = true;
    this.errorMessage = null;

    this.auth.login(this.dto).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigateByUrl(this.returnUrl);
      },
      error: (err) => {
        this.loading = false;
        if (err.status === 404) {
          this.errorMessage = 'NOT_REGISTERED';
        } else if (err.status === 401) {
          this.errorMessage = 'Invalid password.';
        } else if (err.status === 403) {
          this.errorMessage = 'Your account is not verified or has been disabled.';
        } else {
          this.errorMessage = 'Something went wrong. Please try again.';
        }
      },
    });
  }
}
