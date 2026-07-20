import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { LocationService } from '../../../services/location.service';
import { Location } from '../../../models/location.model';

@Component({
  selector: 'app-admin-locations',
  imports: [CommonModule, FormsModule],
  templateUrl: './locations.html',
  styleUrl: './locations.css',
})
export class AdminLocations implements OnInit {
  private locationService = inject(LocationService);
  private cdr = inject(ChangeDetectorRef);

  locations: Location[] = [];
  loading = true;

  showForm = false;
  editing = false;
  selectedId: number | null = null;
  form = {
    locationName: '',
    city: '',
  };
  selectedFile: File | null = null;
  imagePreview: string | null = null;

  ngOnInit() {
    this.loadLocations();
  }

  loadLocations() {
    this.loading = true;
    this.locationService.getAll().subscribe({
      next: (data) => {
        this.locations = data;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  openCreate() {
    this.editing = false;
    this.selectedId = null;
    this.form = { locationName: '', city: '' };
    this.selectedFile = null;
    this.imagePreview = null;
    this.showForm = true;
    this.cdr.markForCheck();
  }

  openEdit(loc: Location) {
    this.editing = true;
    this.selectedId = loc.id;
    this.form = {
      locationName: loc.locationName,
      city: loc.city,
    };
    this.selectedFile = null;
    this.imagePreview = loc.locationImage
      ? `http://localhost:8085/location/${loc.locationImage}`
      : null;
    this.showForm = true;
    this.cdr.markForCheck();
  }

  closeForm() {
    this.showForm = false;
    this.cdr.markForCheck();
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      const reader = new FileReader();
      reader.onload = () => {
        this.imagePreview = reader.result as string;
        this.cdr.markForCheck();
      };
      reader.readAsDataURL(this.selectedFile);
    }
  }

  save() {
    const formData = new FormData();
    const dataBlob = new Blob([JSON.stringify(this.form)], { type: 'application/json' });
    formData.append('data', dataBlob);
    if (this.selectedFile) {
      formData.append('image', this.selectedFile);
    }

    if (this.editing && this.selectedId) {
      this.locationService.update(this.selectedId, formData).subscribe({
        next: () => {
          this.closeForm();
          this.loadLocations();
        },
        error: (err) => console.error('Update failed', err),
      });
    } else {
      this.locationService.create(formData).subscribe({
        next: () => {
          this.closeForm();
          this.loadLocations();
        },
        error: (err) => console.error('Create failed', err),
      });
    }
  }

  confirmDelete(id: number) {
    if (!confirm('Are you sure you want to delete this location?')) return;
    this.locationService.delete(id).subscribe({
      next: () => this.loadLocations(),
      error: (err) => console.error('Delete failed', err),
    });
  }

  getImageUrl(filename: string | undefined): string {
    if (!filename) return '';
    return `http://localhost:8085/location/${filename}`;
  }
}
