import { Component, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-verify-email',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './verify-email.component.html',
  styleUrls: ['./verify-email.component.css'],
})
export class VerifyEmailComponent implements OnInit {
  private auth = inject(AuthService);
  private route = inject(ActivatedRoute);

  loading = true;
  message = '';
  error = '';

  ngOnInit() {
    const token = this.route.snapshot.queryParamMap.get('token');
    if (!token) {
      this.error = 'Invalid verification link';
      this.loading = false;
      return;
    }
    this.auth.verifyEmail(token).subscribe({
      next: () => {
        this.message = 'Email verified successfully! You can now login.';
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Verification failed';
        this.loading = false;
      },
    });
  }
}
