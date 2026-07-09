import { Component, inject, OnInit } from '@angular/core';
import { AuthService } from '../../../services/auth.service';
import { HotelOwnerService } from '../../../services/hotel-owner.service';
import { CustomerService } from '../../../services/customer.service';
import { HotelService } from '../../../services/hotel.service';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-admin-dashboard',
  imports: [RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class AdminDashboard implements OnInit {
  private auth = inject(AuthService);
  private ownerService = inject(HotelOwnerService);
  private customerService = inject(CustomerService);
  private hotelService = inject(HotelService);

  userName = this.auth.userName;
  ownerCount = 0;
  customerCount = 0;
  approvedHotelCount = 0;
  loading = true;

  ngOnInit() {
    this.ownerService.getAllOwners().subscribe({
      next: (data) => (this.ownerCount = data.length),
    });
    this.customerService.getAllCustomers().subscribe({
      next: (data) => (this.customerCount = data.length),
    });
    this.hotelService.getAllApproved().subscribe({
      next: (data) => {
        this.approvedHotelCount = data.length;
        this.loading = false;
      },
      error: () => (this.loading = false),
    });
  }
}
