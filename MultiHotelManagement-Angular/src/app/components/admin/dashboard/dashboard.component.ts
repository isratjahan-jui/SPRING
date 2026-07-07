import { Component, inject, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../services/auth.service';
import { HotelService } from '../../../services/hotel.service';
import { API_URL } from '../../../constants';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class AdminDashboardComponent implements OnInit {
  private http = inject(HttpClient);
  private hotelSvc = inject(HotelService);
  auth = inject(AuthService);

  hotelCount = 0;
  totalEarnings = 0;

  ngOnInit() {
    this.hotelSvc.getAllApproved().subscribe(d => this.hotelCount = d.length);
    this.http.get<number>(`${API_URL}/commissions/admin/total`).subscribe(d => this.totalEarnings = d);
  }
}
