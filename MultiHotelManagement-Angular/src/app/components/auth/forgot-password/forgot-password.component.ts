import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.css'],
})
export class ForgotPasswordComponent {
  private auth = inject(AuthService);
  email = '';
  loading = false;
  message = '';
  error = '';

  onSubmit() {
    this.loading = true;
    this.error = '';
    this.auth.forgotPassword(this.email).subscribe({
      next: (res) => {
        this.message = res.message;
        this.loading = false;
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed';
        this.loading = false;
      },
    });
  }
}
