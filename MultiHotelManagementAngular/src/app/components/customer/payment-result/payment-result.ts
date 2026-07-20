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
              @if (status === 'success') {
                <div class="text-success">
                  <div style="font-size: 5rem;">&#10003;</div>
                  <h2 class="mt-3">Payment Successful!</h2>
                  <p class="text-muted mb-1">Your payment has been confirmed and processed.</p>
                  @if (tranId) {
                    <p class="small text-muted">
                      Transaction ID: <strong>{{ tranId }}</strong>
                    </p>
                  }
                </div>
                <div class="alert alert-success mt-3">
                  <small>
                    Your booking is now confirmed. The hotel has been notified.<br />
                    You will receive a confirmation shortly. Please save your transaction ID for
                    reference.
                  </small>
                </div>
              } @else if (status === 'fail') {
                <div class="text-danger">
                  <div style="font-size: 5rem;">&#10007;</div>
                  <h2 class="mt-3">Payment Failed</h2>
                  <p class="text-muted">
                    Your payment could not be processed. This may be due to insufficient balance or
                    a network issue.
                  </p>
                </div>
                <div class="alert alert-warning mt-3">
                  <small
                    >No money has been deducted from your account. Please try again or use a
                    different payment method.</small
                  >
                </div>
              } @else if (status === 'cancel') {
                <div class="text-warning">
                  <div style="font-size: 5rem;">&#9888;</div>
                  <h2 class="mt-3">Payment Cancelled</h2>
                  <p class="text-muted">You have cancelled the payment. No charges were made.</p>
                </div>
                <div class="alert alert-info mt-3">
                  <small
                    >You can retry payment anytime from your bookings page. Your reservation is
                    still held.</small
                  >
                </div>
              } @else {
                <div class="text-muted">
                  <div
                    class="spinner-border text-primary"
                    role="status"
                    style="width: 4rem; height: 4rem;"
                  >
                    <span class="visually-hidden">Loading...</span>
                  </div>
                  <h2 class="mt-3">Processing Payment...</h2>
                  <p>Please wait while we confirm your payment. Do not close this page.</p>
                </div>
              }
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
