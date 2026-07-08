import { Component, OnInit, inject } from '@angular/core';
import { HotelRequest } from '../../../models/hotel.model';
import { HotelService } from '../../../services/hotel.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environments';

@Component({
  selector: 'app-add-hotel',
  imports: [CommonModule, FormsModule],
  templateUrl: './add-hotel.html',
  styleUrl: './add-hotel.css',
})
export class AddHotel implements OnInit {
  private http = inject(HttpClient);

  hotel: HotelRequest = {
    hotelName: '',
    address: '',
    description: '',
    rating: '',
    // image: '',
    status: 'PENDING_APPROVAL',
    locationId: 0,
    ownerId: 0,
  };

  locations: any[] = [];
  selectedImage?: File;
  preview: string | ArrayBuffer | null = null;

  constructor(
    private hotelService: HotelService,
    private router: Router,
  ) {}

  ngOnInit() {
    const ownerId = localStorage.getItem('ownerId');
    if (ownerId) {
      this.hotel.ownerId = Number(ownerId);
    }

    this.http.get<any[]>(`${environment.apiUrl}locations`).subscribe({
      next: (data) => {
        this.locations = data;
        if (data.length > 0 && !this.hotel.locationId) {
          this.hotel.locationId = data[0].id;
        }
      },
      error: (err) => console.error('Failed to load locations', err),
    });
  }

  onFileSelected(event: any): void {
    if (event.target.files && event.target.files.length > 0) {
      this.selectedImage = event.target.files[0];
      const reader = new FileReader();
      reader.onload = () => {
        this.preview = reader.result;
      };
      reader.readAsDataURL(this.selectedImage!);
    }
  }

  saveHotel() {
  this.hotelService.create(this.hotel, this.selectedImage).subscribe({
    next: () => {
      alert('Hotel added successfully!');
      this.router.navigate(['/hotels']);
    },
    error: (err: any) => {
      console.error(err);
      alert(err.error?.message || 'Failed to add hotel');
    },
  });
}


  
}
