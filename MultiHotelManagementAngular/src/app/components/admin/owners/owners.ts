import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HotelOwnerService } from '../../../services/hotel-owner.service';
import { HotelOwner } from '../../../models/hotel-owner.model';
import { environment } from '../../../../environments/environments';

@Component({
  selector: 'app-admin-owners',
  imports: [CommonModule, FormsModule],
  templateUrl: './owners.html',
  styleUrl: './owners.css',
})
export class AdminOwners implements OnInit {
  private ownerService = inject(HotelOwnerService);
  private cdr = inject(ChangeDetectorRef);

  owners: HotelOwner[] = [];
  filteredOwners: HotelOwner[] = [];
  loading = true;
  searchTerm = '';
  imageBaseUrl = environment.imageBaseUrl;
  deleteModalOwner: HotelOwner | null = null;

  ngOnInit() {
    this.loadOwners();
  }

  loadOwners() {
    this.loading = true;
    this.ownerService.getAllOwners().subscribe({
      next: (data) => {
        this.owners = data;
        this.filteredOwners = data;
        this.loading = false;
        this.cdr.detectChanges();   // UI refresh after data load
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();   // UI refresh even on error
      },
    });
  }

  onSearch() {
    const term = this.searchTerm.toLowerCase();
    this.filteredOwners = this.owners.filter(
      (o) =>
        o.name.toLowerCase().includes(term) ||
        o.email.toLowerCase().includes(term) ||
        o.phone.toLowerCase().includes(term) ||
        (o.address && o.address.toLowerCase().includes(term)),
    );
    this.cdr.detectChanges();   // UI refresh after search
  }

  confirmDelete(owner: HotelOwner) {
    this.deleteModalOwner = owner;
    this.cdr.detectChanges();
  }

  cancelDelete() {
    this.deleteModalOwner = null;
    this.cdr.detectChanges();
  }

  deleteOwner() {
    if (!this.deleteModalOwner?.id) return;
    this.ownerService.deleteOwner(this.deleteModalOwner.id).subscribe({
      next: () => {
        this.owners = this.owners.filter((o) => o.id !== this.deleteModalOwner!.id);
        this.filteredOwners = this.filteredOwners.filter(
          (o) => o.id !== this.deleteModalOwner!.id,
        );
        this.deleteModalOwner = null;
        this.cdr.detectChanges();   // UI refresh after delete
      },
      error: () => {
        this.deleteModalOwner = null;
        this.cdr.detectChanges();
      },
    });
  }

  getImageUrl(image?: string): string {
    return image ? `${this.imageBaseUrl}/owners/${image}` : '';
  }
}
