import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth.service';
import { CustomerService } from '../../../services/customer.service';
import { Customer } from '../../../models/customer.model';

@Component({
  selector: 'app-customer-profile',
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class CustomerProfile implements OnInit {
  private auth = inject(AuthService);
  private customerService = inject(CustomerService);

  customer?: Customer;
  loading = true;
  editing = false;

  ngOnInit() {
    const userId = this.auth.userId();
    if (userId) {
      this.customerService.getCustomerByUserId(userId).subscribe({
        next: (data) => {
          this.customer = data;
          this.loading = false;
        },
        error: () => (this.loading = false),
      });
    } else {
      this.loading = false;
    }
  }
}
