import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HotelOwnerService } from '../../../services/hotel-owner.service';
import { HotelService } from '../../../services/hotel.service';
import { HotelOwner } from '../../../models/hotel-owner.model';
import { Hotel } from '../../../models/hotel.model';
import { environment } from '../../../../environments/environments';

@Component({
  selector: 'app-owner-details',
  imports: [CommonModule],
  templateUrl: './owner-details.html',
  styleUrl: './owner-details.css',
})
export class OwnerDetails implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private ownerService = inject(HotelOwnerService);
  private hotelService = inject(HotelService);
  private cdr = inject(ChangeDetectorRef);

  owner: HotelOwner | null = null;
  hotels: Hotel[] = [];
  loading = true;
  imageBaseUrl = environment.imageBaseUrl;

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) {
      this.loading = false;
      return;
    }

    this.ownerService.getOwnerById(id).subscribe({
      next: (data) => {
        this.owner = data;
        this.cdr.markForCheck();
        if (data.id) {
          this.hotelService.getByOwner(data.id).subscribe({
            next: (h) => {
              this.hotels = h;
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
  }

  goBack() {
    this.router.navigate(['/admin/owners']);
  }

  getImageUrl(image?: string): string {
    return image ? `${this.imageBaseUrl}/owners/${image}` : '';
  }

  getStatusBadge(status: string): string {
    switch (status) {
      case 'APPROVED':
        return 'bg-success';
      case 'PENDING':
        return 'bg-warning text-dark';
      case 'REJECTED':
        return 'bg-danger';
      default:
        return 'bg-secondary';
    }
  }
}
