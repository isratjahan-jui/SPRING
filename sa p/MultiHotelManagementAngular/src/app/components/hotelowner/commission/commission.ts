import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CommissionService } from '../../../services/commission.service';
import { HotelOwnerService } from '../../../services/hotel-owner.service';
import { KEYS, StorageService } from '../../../services/storage.service';
import { CommissionResponse } from '../../../models/commission.model';
import { HotelOwner } from '../../../models/hotel-owner.model';
import { LoginResponse } from '../../../models/auth.model';

@Component({
  selector: 'app-owner-commission',
  imports: [CommonModule, FormsModule],
  templateUrl: './commission.html',
  styleUrl: './commission.css',
})
export class OwnerCommission implements OnInit {
  private commissionService = inject(CommissionService);
  private ownerService = inject(HotelOwnerService);
  private storage = inject(StorageService);
  private cdr = inject(ChangeDetectorRef);

  user: LoginResponse | null = null;
  ownerId = 0;
  commissions: CommissionResponse[] = [];
  filteredCommissions: CommissionResponse[] = [];
  loading = true;

  searchTerm = '';
  currentPage = 1;
  pageSize = 10;
  totalPages = 1;

  totalCommissionPaid = 0;
  totalAdminEarnings = 0;
  avgRate = 0;

  ngOnInit() {
    this.user = this.storage.getUser();
    if (this.user?.userId) {
      this.loadOwner();
    }
  }

  loadOwner() {
    const userId = this.user!.userId;
    this.ownerService.getOwnerByUserId(userId).subscribe({
      next: (res) => {
        this.ownerId = res.id ?? 0;
        this.storage.saveData(KEYS.HOTEL_OWNER, res);
        if (this.ownerId) {
          this.loadCommissions();
        }
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  loadCommissions() {
    this.commissionService.getByOwner(this.ownerId).subscribe({
      next: (data) => {
        this.commissions = data;
        this.totalCommissionPaid = data.reduce((sum, c) => sum + (c.adminEarnings || 0), 0);
        this.totalAdminEarnings = data.reduce((sum, c) => sum + (c.adminEarnings || 0), 0);
        this.avgRate = data.length > 0
          ? data.reduce((sum, c) => sum + c.commissionRate, 0) / data.length
          : 0;
        this.applyFilter();
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.commissions = [];
        this.filteredCommissions = [];
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  applyFilter() {
    let result = [...this.commissions];

    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      result = result.filter(
        (c) =>
          (c.bookingId && c.bookingId.toString().includes(term)) ||
          (c.hotelName && c.hotelName.toLowerCase().includes(term)) ||
          (c.customerName && c.customerName.toLowerCase().includes(term)) ||
          (c.paymentMethod && c.paymentMethod.toLowerCase().includes(term))
      );
    }

    this.totalPages = Math.max(1, Math.ceil(result.length / this.pageSize));
    if (this.currentPage > this.totalPages) {
      this.currentPage = 1;
    }

    const start = (this.currentPage - 1) * this.pageSize;
    this.filteredCommissions = result.slice(start, start + this.pageSize);
  }

  onSearch() {
    this.currentPage = 1;
    this.applyFilter();
  }

  goToPage(page: number) {
    if (page >= 1 && page <= this.totalPages) {
      this.currentPage = page;
      this.applyFilter();
    }
  }

  get pages(): number[] {
    const arr: number[] = [];
    for (let i = 1; i <= this.totalPages; i++) {
      arr.push(i);
    }
    return arr;
  }
}
