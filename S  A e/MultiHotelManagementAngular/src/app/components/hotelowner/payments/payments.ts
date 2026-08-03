import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../services/auth.service';
import { CommissionService } from '../../../services/commission.service';
import { CommissionResponse } from '../../../models/commission.model';

@Component({
  selector: 'app-payments',
  imports: [CommonModule],
  templateUrl: './payments.html',
  styleUrl: './payments.css',
})
export class Payments implements OnInit {
  private auth = inject(AuthService);
  private commissionService = inject(CommissionService);
  private cdr = inject(ChangeDetectorRef);

  commissions: CommissionResponse[] = [];
  totalEarnings = 0;
  loading = true;

  ngOnInit() {
    const ownerId = this.auth.getUser()?.ownerId;
    if (ownerId) {
      this.commissionService.getByOwner(ownerId).subscribe({
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

      this.commissionService.getOwnerTotal(ownerId).subscribe({
        next: (total) => {
          this.totalEarnings = total;
          this.cdr.markForCheck();
        },
      });
    } else {
      this.loading = false;
    }
  }
}
