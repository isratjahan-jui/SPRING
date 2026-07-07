import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-owner-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './owner-layout.component.html',
  styleUrls: ['./owner-layout.component.css'],
})
export class OwnerLayoutComponent {
  auth = inject(AuthService);
}
