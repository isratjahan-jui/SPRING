import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { LocationService } from '../../../services/location.service';
import { Location } from '../../../models/location.model';
import { environment } from '../../../../environments/environments';

@Component({
  selector: 'app-admin-locations',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './locations.html',
  styleUrl: './locations.css',
})
export class AdminLocations implements OnInit {
  private locationService = inject(LocationService);
  private cdr = inject(ChangeDetectorRef);

  locations: Location[] = [];
  loading = true;

  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;

  showForm = false;
  editing = false;
  selectedId: number | null = null;
  form: any = {
    locationName: '',
    city: '',
    district: '',
    division: '',
    upazila: '',
  };
  selectedFile: File | null = null;
  imagePreview: string | null = null;

  ngOnInit() {
    this.loadLocations();
  }

  loadLocations() {
    this.loading = true;
    this.locationService.getPaginated(this.currentPage, this.pageSize).subscribe({
      next: (data) => {
        this.locations = data.content;
        this.totalElements = data.totalElements;
        this.totalPages = data.totalPages;
        this.loading = false;
        this.cdr.markForCheck();
      },
      error: () => {
        this.loading = false;
        this.cdr.markForCheck();
      },
    });
  }

  getSerial(index: number): number {
    return this.currentPage * this.pageSize + index + 1;
  }

  goToPage(page: number) {
    if (page < 0 || page >= this.totalPages) return;
    this.currentPage = page;
    this.loadLocations();
  }

  openCreate() {
    this.editing = false;
    this.selectedId = null;
    this.form = { locationName: '', city: '', district: '', division: '', upazila: '' };
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
      district: loc.district || '',
      division: loc.division || '',
      upazila: loc.upazila || '',
    };
    this.selectedFile = null;
    this.imagePreview = loc.locationImage
      ? `${environment.imageBaseUrl}/location/${loc.locationImage}`
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
    return `${environment.imageBaseUrl}/location/${filename}`;
  }
}
