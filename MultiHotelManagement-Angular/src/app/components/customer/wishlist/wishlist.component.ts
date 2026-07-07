import { Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { WishlistService } from '../../../services/wishlist.service';
import { WishlistItem } from '../../../models/wishlist.model';

@Component({
  selector: 'app-customer-wishlist',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './wishlist.component.html',
  styleUrls: ['./wishlist.component.css'],
})
export class CustomerWishlistComponent implements OnInit {
  private auth = inject(AuthService);
  private wishlistSvc = inject(WishlistService);
  items: WishlistItem[] = [];

  ngOnInit() {
    this.wishlistSvc.getByUserId(this.auth.userId()!).subscribe(d => this.items = d);
  }

  remove(id: number) {
    this.wishlistSvc.remove(id).subscribe(() => {
      this.items = this.items.filter(i => i.id !== id);
    });
  }
}
