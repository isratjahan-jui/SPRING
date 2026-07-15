import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HotelOwnerService } from '../../../services/hotel-owner.service';
import { HotelOwner } from '../../../models/hotel-owner.model';

@Component({
  selector: 'app-admin-owners',
  imports: [CommonModule],
  templateUrl: './owners.html',
  styleUrl: './owners.css',
})
export class AdminOwners implements OnInit {
  private ownerService = inject(HotelOwnerService);
  owners: HotelOwner[] = [];
  loading = true;

  ngOnInit() {
    this.ownerService.getAllOwners().subscribe({
      next: (data) => {
        this.owners = data;
        this.loading = false;
      },
      error: () => (this.loading = false),
    });
  }
}
