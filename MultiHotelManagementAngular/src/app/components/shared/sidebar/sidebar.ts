import { Component, inject, OnInit } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { NotificationService } from '../../../services/notification.service';
import { StorageService } from '../../../services/storage.service';

@Component({
  selector: 'app-sidebar',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css',
})
export class Sidebar implements OnInit {
  private auth = inject(AuthService);
  private storage = inject(StorageService);
  private notificationService = inject(NotificationService);

  userRole: string | null = null;

  ngOnInit() {
    this.notificationService.connect();
    this.userRole = this.storage.getRole();
  }

  logout() {
    this.auth.logout();
  }
}
