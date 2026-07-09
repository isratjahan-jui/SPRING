import { Component, inject } from '@angular/core';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-owner-dashboard',
  imports: [],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class OwnerDashboard {
  private auth = inject(AuthService);
  userName = this.auth.userName;
}
