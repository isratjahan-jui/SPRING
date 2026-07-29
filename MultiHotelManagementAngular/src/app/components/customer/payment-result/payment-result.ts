import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-payment-result',
  imports: [CommonModule],
  templateUrl: './payment-result.html',
  styleUrl: './payment-result.css',
})
export class PaymentResult implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  status: string = 'loading';
  tranId: string = '';

  ngOnInit() {
    this.route.queryParams.subscribe((params) => {
      this.status = params['status'] || 'loading';
      this.tranId = params['tran_id'] || '';
    });
  }

  goToBookings() {
    this.router.navigate(['/customer/bookings']);
  }

  goToHome() {
    this.router.navigate(['/']);
  }
}
