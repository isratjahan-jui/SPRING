import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../services/auth.service';
import { CustomerService } from '../../../services/customer.service';
import { WalletService } from '../../../services/wallet.service';
import { Wallet, WalletTransaction } from '../../../models/wallet.model';

@Component({
  selector: 'app-customer-wallet',
  imports: [CommonModule],
  templateUrl: './wallet.html',
  styleUrl: './wallet.css',
})
export class CustomerWallet implements OnInit {
  private auth = inject(AuthService);
  private customerService = inject(CustomerService);
  private walletService = inject(WalletService);
  private cdr = inject(ChangeDetectorRef);

  wallet: Wallet | null = null;
  transactions: WalletTransaction[] = [];
  loading = true;
  walletNotFound = false;

  ngOnInit() {
    const userId = this.auth.getUser()?.userId;
    if (!userId) {
      this.loading = false;
      this.walletNotFound = true;
      this.cdr.markForCheck();
      return;
    }

    this.customerService.getCustomerByUserId(userId).subscribe({
      next: () => {
        this.loadWallet(userId);
      },
      error: () => {
        this.walletNotFound = true;
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  private loadWallet(userId: number) {
    this.walletService.getByUser(userId).subscribe({
      next: (w) => {
        this.wallet = w;
        this.loadTransactions(w.id);
      },
      error: (err) => {
        this.walletNotFound = err.status === 404 || err.status === 400;
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  private loadTransactions(walletId: number) {
    this.walletService.getTransactions(walletId).subscribe({
      next: (txns) => {
        this.transactions = txns;
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
