import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { WishlistService } from '../../../services/wishlist.service';
import { AuthService } from '../../../services/auth.service';
import { CustomerService } from '../../../services/customer.service';
import { WishlistResponse } from '../../../models/wishlist.model';
import { environment } from '../../../../environments/environments';

@Component({
  selector: 'app-customer-wishlist',
  imports: [CommonModule, RouterLink],
  templateUrl: './wishlist.html',
  styleUrl: './wishlist.css',
})
export class CustomerWishlist implements OnInit {
  private auth = inject(AuthService);
  private customerService = inject(CustomerService);
  private wishlistService = inject(WishlistService);
  private cdr = inject(ChangeDetectorRef);

  items: WishlistResponse[] = [];
  loading = true;
  imageBaseUrl = environment.imageBaseUrl;

  showConfirmDialog = false;
  itemToRemove: WishlistResponse | null = null;
  removing = false;

  ngOnInit() {
    const userId = this.auth.getUser()?.userId;
    if (userId) {
      this.customerService.getCustomerByUserId(userId).subscribe({
        next: (customer) => {
          if (customer.id) {
            this.wishlistService.getByCustomer(customer.id).subscribe({
              next: (data) => {
                this.items = data;
                this.loading = false;
                this.cdr.markForCheck();
              },
              error: () => {
                this.loading = false;
                this.cdr.markForCheck();
              },
            });
          } else {
            this.loading = false;
            this.cdr.markForCheck();
          }
        },
        error: () => {
          this.loading = false;
          this.cdr.markForCheck();
        },
      });
    } else {
      this.loading = false;
    }
  }

  confirmRemove(item: WishlistResponse) {
    this.itemToRemove = item;
    this.showConfirmDialog = true;
    this.cdr.markForCheck();
  }

  cancelRemove() {
    this.itemToRemove = null;
    this.showConfirmDialog = false;
    this.cdr.markForCheck();
  }

  removeConfirmed() {
    if (!this.itemToRemove) return;
    this.removing = true;
    this.wishlistService.delete(this.itemToRemove.id).subscribe({
      next: () => {
        this.items = this.items.filter((i) => i.id !== this.itemToRemove!.id);
        this.itemToRemove = null;
        this.showConfirmDialog = false;
        this.removing = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.removing = false;
        this.cdr.markForCheck();
      },
    });
  }
}
