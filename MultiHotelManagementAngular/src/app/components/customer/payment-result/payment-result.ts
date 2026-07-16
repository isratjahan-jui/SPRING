import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-payment-result',
  imports: [CommonModule],
  template: `
    <div class="container mt-5">
      <div class="row justify-content-center">
        <div class="col-md-6">
          <div class="card shadow-sm">
            <div class="card-body text-center p-5">
              <div *ngIf="status === 'success'" class="text-success">
                <i class="bi bi-check-circle-fill" style="font-size: 4rem;"></i>
                <h2 class="mt-3">Payment Successful!</h2>
                <p class="text-muted">
                  Your booking has been confirmed. A confirmation email will be sent shortly.
                </p>
              </div>
              <div *ngIf="status === 'fail'" class="text-danger">
                <i class="bi bi-x-circle-fill" style="font-size: 4rem;"></i>
                <h2 class="mt-3">Payment Failed</h2>
                <p class="text-muted">Your payment could not be processed. Please try again.</p>
              </div>
              <div *ngIf="status === 'cancel'" class="text-warning">
                <i class="bi bi-exclamation-circle-fill" style="font-size: 4rem;"></i>
                <h2 class="mt-3">Payment Cancelled</h2>
                <p class="text-muted">You have cancelled the payment. No charges were made.</p>
              </div>
              <div *ngIf="status === 'loading'" class="text-muted">
                <div class="spinner-border text-primary" role="status">
                  <span class="visually-hidden">Loading...</span>
                </div>
                <h2 class="mt-3">Processing...</h2>
                <p>Please wait while we confirm your payment.</p>
              </div>
              <div class="mt-4">
                <button class="btn btn-primary me-2" (click)="goToBookings()">
                  View My Bookings
                </button>
                <button class="btn btn-outline-secondary" (click)="goToHome()">Go to Home</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class PaymentResult implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  status: string = 'loading';

  ngOnInit() {
    this.route.queryParams.subscribe((params) => {
      this.status = params['status'] || 'loading';
    });
  }

  goToBookings() {
    this.router.navigate(['/customer/bookings']);
  }

  goToHome() {
    this.router.navigate(['/']);
  }
}
