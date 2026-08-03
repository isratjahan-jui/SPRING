import { Component } from '@angular/core';
import { StorageService } from '../../../services/storage.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-role-redirect',
  imports: [],
  templateUrl: './role-redirect.html',
  styleUrl: './role-redirect.css',
})
export class RoleRedirect {
  constructor(
    private storage: StorageService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    const role = this.storage.getRole();

    console.log(role);
    const map: Record<string, string> = {
      ADMIN: '/admin',
      HOTEL_OWNER: '/owner',
      CUSTOMER: '/customer',
    };
    this.router.navigate([map[role ?? ''] ?? '/login']);
  }
}
