import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { HotelService } from '../../../services/hotel.service';
import { AuthService } from '../../../services/auth.service';
import { Hotel } from '../../../models/hotel.model';
import { environment } from '../../../../environments/environments';

interface ReportItem {
  id: number;
  totalBookings: number;
  income: number;
  occupancyRate: number;
  type: string;
  hotelName: string;
  generatedAt: string;
  updatedAt: string;
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
  private http = inject(HttpClient);
  private cdr = inject(ChangeDetectorRef);

  hotels: Hotel[] = [];
  reports: ReportItem[] = [];
  selectedHotelId = 0;
  loading = true;
  loadingReports = false;
  generating = false;
  reportTypes = ['DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY'];
  selectedType = '';
  selectedReport: ReportItem | null = null;

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
    if (!this.selectedHotelId) {
      this.reports = [];
      this.selectedReport = null;
      this.cdr.markForCheck();
      return;
    }
    this.loadingReports = true;
    this.selectedReport = null;
    this.cdr.markForCheck();

    this.http
      .get<ReportItem[]>(`${environment.apiUrl}reports/hotel/${this.selectedHotelId}`)
      .subscribe({
        next: (data) => {
          this.reports = data.sort(
            (a, b) => new Date(b.generatedAt).getTime() - new Date(a.generatedAt).getTime(),
          );
          this.loadingReports = false;
          this.cdr.markForCheck();
        },
        error: () => {
          this.reports = [];
          this.loadingReports = false;
          this.cdr.markForCheck();
        },
      });
  }

  generateReport() {
    if (!this.selectedHotelId || !this.selectedType) return;
    this.generating = true;

    const payload = {
      hotelId: this.selectedHotelId,
      type: this.selectedType,
    };

    this.http.post<ReportItem>(`${environment.apiUrl}reports`, payload).subscribe({
      next: (data) => {
        this.reports.unshift(data);
        this.generating = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.generating = false;
        this.cdr.markForCheck();
      },
    });
  }

  viewReport(report: ReportItem) {
    this.selectedReport = report;
    this.cdr.markForCheck();
    setTimeout(() => {
      document
        .getElementById('report-detail')
        ?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }, 100);
  }

  closeDetail() {
    this.selectedReport = null;
    this.cdr.markForCheck();
  }

  printReport() {
    if (!this.selectedReport) return;
    const report = this.selectedReport;
    const win = window.open('', '_blank', 'width=800,height=600');
    if (!win) return;

    const dataSections = this.buildDataSectionsHTML(report);

    win.document.write(`
      <!DOCTYPE html>
      <html>
      <head>
        <title>Report - ${report.type} - ${report.hotelName}</title>
        <style>
          body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; padding: 30px; color: #333; }
          .header { text-align: center; border-bottom: 2px solid #2563eb; padding-bottom: 15px; margin-bottom: 25px; }
          .header h1 { margin: 0; color: #2563eb; font-size: 22px; }
          .header p { margin: 5px 0 0; color: #666; font-size: 13px; }
          .section { margin-bottom: 20px; padding: 15px; border: 1px solid #e5e7eb; border-radius: 8px; }
          .section h3 { margin: 0 0 10px; color: #1e40af; font-size: 15px; border-bottom: 1px solid #e5e7eb; padding-bottom: 8px; }
          .metric { display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid #f3f4f6; }
          .metric:last-child { border-bottom: none; }
          .metric-label { color: #6b7280; }
          .metric-value { font-weight: 600; color: #111827; }
          .footer { text-align: center; margin-top: 30px; padding-top: 15px; border-top: 1px solid #e5e7eb; color: #9ca3af; font-size: 11px; }
          @media print { body { padding: 15px; } }
        </style>
      </head>
      <body>
        <div class="header">
          <h1>TripNest - ${report.type} Report</h1>
          <p>Hotel: ${report.hotelName} | Generated: ${new Date(report.generatedAt).toLocaleString()}</p>
        </div>
        ${dataSections}
        <div class="footer">
          <p>TripNest Hotel Management System | Report ID: ${report.id}</p>
        </div>
        <script>window.onload = function() { window.print(); }</script>
      </body>
      </html>
    `);
    win.document.close();
  }

  downloadReport() {
    if (!this.selectedReport) return;
    const r = this.selectedReport;
    let text = `========================================\n`;
    text += `        TRIPNEST - ${r.type} REPORT\n`;
    text += `========================================\n\n`;
    text += `Hotel:       ${r.hotelName}\n`;
    text += `Report ID:   ${r.id}\n`;
    text += `Generated:   ${new Date(r.generatedAt).toLocaleString()}\n`;
    text += `Updated:     ${new Date(r.updatedAt).toLocaleString()}\n\n`;
    text += `--- REPORT DATA ---\n\n`;

    if (r.type === 'REVENUE' || r.income > 0) {
      text += `Income:      $${r.income?.toFixed(2) ?? 'N/A'}\n`;
    }
    if (r.type === 'BOOKING' || r.totalBookings > 0) {
      text += `Bookings:    ${r.totalBookings ?? 'N/A'}\n`;
    }
    if (r.type === 'OCCUPANCY' || r.occupancyRate > 0) {
      text += `Occupancy:   ${r.occupancyRate?.toFixed(1) ?? 'N/A'}%\n`;
    }

    if (r.income > 0 && !['REVENUE'].includes(r.type)) {
      text += `Income:      $${r.income?.toFixed(2) ?? 'N/A'}\n`;
    }
    if (r.totalBookings > 0 && !['BOOKING'].includes(r.type)) {
      text += `Bookings:    ${r.totalBookings ?? 'N/A'}\n`;
    }
    if (r.occupancyRate > 0 && !['OCCUPANCY'].includes(r.type)) {
      text += `Occupancy:   ${r.occupancyRate?.toFixed(1) ?? 'N/A'}%\n`;
    }

    text += `\n========================================\n`;
    text += `      Generated by TripNest System\n`;
    text += `========================================\n`;

    const blob = new Blob([text], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${r.type}_Report_${r.hotelName}_${new Date(r.generatedAt).toISOString().split('T')[0]}.txt`;
    a.click();
    URL.revokeObjectURL(url);
  }

  getReportTypeLabel(type: string): string {
    const labels: Record<string, string> = {
      DAILY: 'Daily Report',
      WEEKLY: 'Weekly Report',
      MONTHLY: 'Monthly Report',
      YEARLY: 'Yearly Report',
    };
    return labels[type] || type;
  }

  getReportTypeBadgeClass(type: string): string {
    const classes: Record<string, string> = {
      DAILY: 'badge-type-daily',
      WEEKLY: 'badge-type-weekly',
      MONTHLY: 'badge-type-monthly',
      YEARLY: 'badge-type-yearly',
    };
    return classes[type] || 'bg-secondary';
  }

  private buildDataSectionsHTML(report: ReportItem): string {
    let sections = '';

    sections += `
      <div class="section">
        <h3>Overview</h3>
        <div class="metric"><span class="metric-label">Report Type</span><span class="metric-value">${this.getReportTypeLabel(report.type)}</span></div>
        <div class="metric"><span class="metric-label">Hotel</span><span class="metric-value">${report.hotelName}</span></div>
        <div class="metric"><span class="metric-label">Generated At</span><span class="metric-value">${new Date(report.generatedAt).toLocaleString()}</span></div>
      </div>
    `;

    if (report.income != null && report.income > 0) {
      sections += `
        <div class="section">
          <h3>Revenue</h3>
          <div class="metric"><span class="metric-label">Total Income</span><span class="metric-value">$${report.income.toFixed(2)}</span></div>
        </div>
      `;
    }

    if (report.totalBookings != null && report.totalBookings > 0) {
      sections += `
        <div class="section">
          <h3>Bookings</h3>
          <div class="metric"><span class="metric-label">Total Bookings</span><span class="metric-value">${report.totalBookings}</span></div>
        </div>
      `;
    }

    if (report.occupancyRate != null && report.occupancyRate > 0) {
      sections += `
        <div class="section">
          <h3>Occupancy</h3>
          <div class="metric"><span class="metric-label">Occupancy Rate</span><span class="metric-value">${report.occupancyRate.toFixed(1)}%</span></div>
        </div>
      `;
    }

    return sections;
  }
}
