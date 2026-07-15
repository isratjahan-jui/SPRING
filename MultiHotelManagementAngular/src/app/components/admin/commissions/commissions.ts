import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CommissionService } from '../../../services/commission.service';
import { CommissionResponse } from '../../../models/commission.model';

@Component({
  selector: 'app-admin-commissions',
  imports: [CommonModule],
  templateUrl: './commissions.html',
  styleUrl: './commissions.css',
})
export class AdminCommissions implements OnInit {
  private commissionService = inject(CommissionService);
  private cdr = inject(ChangeDetectorRef);

  commissions: CommissionResponse[] = [];
  totalEarnings = 0;
  loading = true;

  ngOnInit() {
    this.commissionService.getAll().subscribe({
      next: (data) => {
        this.commissions = data;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });

    this.commissionService.getAdminTotal().subscribe({
      next: (total) => {
        this.totalEarnings = total;
        this.cdr.markForCheck();
      },
    });
  }
}
