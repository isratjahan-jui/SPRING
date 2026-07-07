import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css'],
})
export class ResetPasswordComponent {
  private auth = inject(AuthService);
  private route = inject(ActivatedRoute);

  newPassword = '';
  loading = false;
  message = '';
  error = '';

  onSubmit() {
    const token = this.route.snapshot.queryParamMap.get('token');
    if (!token) { this.error = 'Invalid reset link'; return; }
    this.loading = true;
    this.auth.resetPassword(token, this.newPassword).subscribe({
      next: (res) => { this.message = res.message; this.loading = false; },
      error: (err) => { this.error = err.error?.message || 'Failed'; this.loading = false; },
    });
  }
}
