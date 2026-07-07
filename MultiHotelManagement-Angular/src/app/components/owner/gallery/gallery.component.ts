import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HotelService } from '../../../services/hotel.service';
import { AuthService } from '../../../services/auth.service';
import { GalleryService } from '../../../services/gallery.service';
import { GalleryImage } from '../../../models/gallery.model';
import { Hotel } from '../../../models/hotel.model';

@Component({
  selector: 'app-owner-gallery',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './gallery.component.html',
  styleUrls: ['./gallery.component.css'],
})
export class OwnerGalleryComponent implements OnInit {
  private gallerySvc = inject(GalleryService);
  private hotelSvc = inject(HotelService);
  private auth = inject(AuthService);

  hotels: Hotel[] = [];
  images: GalleryImage[] = [];
  selectedHotelId = '';
  category = 'GENERAL';
  selectedFiles: File[] = [];

  ngOnInit() {
    const oid = this.auth.user()?.ownerId;
    if (oid) this.hotelSvc.getByOwner(oid).subscribe(d => this.hotels = d);
  }

  loadGallery() {
    if (this.selectedHotelId) this.gallerySvc.getByHotel(Number(this.selectedHotelId)).subscribe(d => this.images = d);
  }

  onFilesSelect(e: any) {
    this.selectedFiles = Array.from(e.target.files || []);
  }

  upload() {
    if (!this.selectedFiles.length) return;
    this.gallerySvc.uploadMultiple(Number(this.selectedHotelId), this.selectedFiles, this.category)
      .subscribe(() => {
        this.loadGallery();
        this.selectedFiles = [];
      });
  }

  deleteImage(id: number) {
    if (confirm('Delete this image?')) this.gallerySvc.delete(id).subscribe(() => this.loadGallery());
  }
}
