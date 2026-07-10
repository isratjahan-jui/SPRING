import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Gallery, GalleryRequest } from '../../../models/gallery.model';
import { Hotel } from '../../../models/hotel.model';
import { GalleryService } from '../../../services/gallery.service';
import { HotelService } from '../../../services/hotel.service';

@Component({
  selector: 'app-owner-gallery',
  imports: [CommonModule, FormsModule],
  templateUrl: './gallery.html',
  styleUrl: './gallery.css',
})
export class OwnerGallery implements OnInit {
  hotels: Hotel[] = [];
  galleryItems: Gallery[] = [];
  selectedHotelId = 0;
  showForm = false;
  editingId: number | null = null;
  form: GalleryRequest = this.emptyForm();
  selectedImage?: File;
  preview: string | ArrayBuffer | null = null;
  loading = false;

  constructor(
    private galleryService: GalleryService,
    private hotelService: HotelService,
  ) {}

  ngOnInit() {
    const ownerId = localStorage.getItem('ownerId');
    if (ownerId) {
      this.hotelService.getByOwner(Number(ownerId)).subscribe({
        next: (data) => (this.hotels = data),
        error: () => alert('Failed to load hotels'),
      });
    }
  }

  emptyForm(): GalleryRequest {
    return { caption: '', category: 'GENERAL', hotelId: 0 };
  }

  onHotelChange() {
    this.galleryItems = [];
    this.showForm = false;
    if (this.selectedHotelId) {
      this.galleryService.getByHotel(this.selectedHotelId).subscribe({
        next: (data) => (this.galleryItems = data),
        error: () => alert('Failed to load gallery'),
      });
    }
  }

  addNew() {
    this.editingId = null;
    this.form = { ...this.emptyForm(), hotelId: this.selectedHotelId };
    this.selectedImage = undefined;
    this.preview = null;
    this.showForm = true;
  }

  edit(item: Gallery) {
    this.editingId = item.id;
    this.form = { caption: item.caption, category: item.category, hotelId: item.hotelId };
    this.preview = null;
    this.showForm = true;
  }

  cancelForm() {
    this.showForm = false;
    this.editingId = null;
    this.selectedImage = undefined;
    this.preview = null;
  }

  save() {
    if (!this.editingId && !this.selectedImage) {
      alert('Please select an image');
      return;
    }
    this.loading = true;

    const request = this.editingId
      ? this.galleryService.update(this.editingId, this.form)
      : this.galleryService.create(this.form, this.selectedImage!);

    request.subscribe({
      next: () => {
        this.loading = false;
        this.cancelForm();
        this.onHotelChange();
      },
      error: (err) => {
        this.loading = false;
        alert(err.error?.message || 'Failed to save gallery item');
      },
    });
  }

  deleteItem(id: number) {
    if (!confirm('Delete this image?')) return;
    this.galleryService.delete(id).subscribe({
      next: () => this.onHotelChange(),
      error: () => alert('Failed to delete gallery item'),
    });
  }

  onFileSelected(event: any) {
    if (event.target.files?.length) {
      this.selectedImage = event.target.files[0];
      const reader = new FileReader();
      reader.onload = () => (this.preview = reader.result);
      reader.readAsDataURL(this.selectedImage!);
    }
  }
}
