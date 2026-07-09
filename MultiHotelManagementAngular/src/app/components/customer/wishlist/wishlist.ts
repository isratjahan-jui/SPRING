import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { WishlistService } from '../../../services/wishlist.service';
import { AuthService } from '../../../services/auth.service';
import { CustomerService } from '../../../services/customer.service';
import { WishlistResponse } from '../../../models/wishlist.model';

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

  items: WishlistResponse[] = [];
  loading = true;

  ngOnInit() {
    const userId = this.auth.userId();
    if (userId) {
      this.customerService.getCustomerByUserId(userId).subscribe({
        next: (customer) => {
          if (customer.id) {
            this.wishlistService.getByCustomer(customer.id).subscribe({
              next: (data) => {
                this.items = data;
                this.loading = false;
              },
              error: () => (this.loading = false),
            });
          } else {
            this.loading = false;
          }
        },
        error: () => (this.loading = false),
      });
    } else {
      this.loading = false;
    }
  }

  remove(id: number) {
    this.wishlistService.delete(id).subscribe({
      next: () => {
        this.items = this.items.filter((i) => i.id !== id);
      },
    });
  }
}
