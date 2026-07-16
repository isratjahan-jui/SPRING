import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HotelService } from '../../../services/hotel.service';
import { AuthService } from '../../../services/auth.service';
import { Hotel } from '../../../models/hotel.model';

interface ReportItem {
  id: number;
  hotelId: number;
  hotelName: string;
  reportType: string;
  reportData: string;
  generatedAt: string;
}

@Component({
  selector: 'app-owner-reports',
  imports: [CommonModule, FormsModule],
  templateUrl: './reports.html',
  styleUrl: './reports.css',
})
export class OwnerReports implements OnInit {
  private hotelService = inject(HotelService);
  private auth = inject(AuthService);
  private cdr = inject(ChangeDetectorRef);

  hotels: Hotel[] = [];
  reports: ReportItem[] = [];
  selectedHotelId = 0;
  loading = true;
  generating = false;
  reportTypes = ['REVENUE', 'BOOKING', 'OCCUPANCY'];
  selectedType = '';

  ngOnInit() {
    const ownerId = this.auth.getUser()?.ownerId;
    if (ownerId) {
      this.hotelService.getByOwner(ownerId).subscribe({
        next: (data) => {
          this.hotels = data;
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
    }
  }

  loadReports() {
    if (!this.selectedHotelId) return;
    this.reports = [];
    this.cdr.markForCheck();
  }

  generateReport() {
    if (!this.selectedHotelId || !this.selectedType) return;
    this.generating = true;

    const payload = {
      hotelId: this.selectedHotelId,
      reportType: this.selectedType,
    };

    fetch(`${this.getApiUrl()}/reports`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${localStorage.getItem('cm_token') ? '' : ''}`,
      },
      body: JSON.stringify(payload),
    })
      .then((res) => res.json())
      .then((data: ReportItem) => {
        this.reports.unshift(data);
        this.generating = false;
        this.cdr.markForCheck();
      })
      .catch(() => {
        this.generating = false;
        this.cdr.markForCheck();
      });
  }

  private getApiUrl(): string {
    return 'http://localhost:8085/api';
  }
}
