import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DealService } from '../../../services/deal.service';
import { DealResponse } from '../../../models/deal.model';

@Component({
  selector: 'app-customer-deals',
  imports: [CommonModule, RouterLink],
  templateUrl: './deals.html',
  styleUrl: './deals.css',
})
export class CustomerDeals implements OnInit {
  private dealService = inject(DealService);
  private cdr = inject(ChangeDetectorRef);

  deals: DealResponse[] = [];
  loading = true;

  ngOnInit() {
    this.dealService.getAll().subscribe({
      next: (data) => {
        this.deals = data;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }
}
