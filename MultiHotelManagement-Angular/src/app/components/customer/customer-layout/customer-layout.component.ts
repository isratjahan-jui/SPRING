import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-customer-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './customer-layout.component.html',
  styleUrls: ['./customer-layout.component.css'],
})
export class CustomerLayoutComponent {
  auth = inject(AuthService);
}
