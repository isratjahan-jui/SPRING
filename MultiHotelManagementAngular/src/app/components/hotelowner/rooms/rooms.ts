import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Room, RoomRequest } from '../../../models/room.model';
import { Hotel } from '../../../models/hotel.model';
import { RoomService } from '../../../services/room.service';
import { HotelService } from '../../../services/hotel.service';
import { AuthService } from '../../../services/auth.service';
import { environment } from '../../../../environments/environments';

@Component({
  selector: 'app-owner-rooms',
  imports: [CommonModule, FormsModule],
  templateUrl: './rooms.html',
  styleUrl: './rooms.css',
})
export class OwnerRooms implements OnInit {
  hotels: Hotel[] = [];
  rooms: Room[] = [];
  selectedHotelId = 0;
  showForm = false;
  editingId: number | null = null;
  form: RoomRequest = this.emptyForm();
  selectedImage?: File;
  preview: string | ArrayBuffer | null = null;
  loading = false;
  private authService = inject(AuthService);
  private cdr = inject(ChangeDetectorRef);

  constructor(
    private roomService: RoomService,
    private hotelService: HotelService,
  ) {}

  ngOnInit() {
    const ownerId = this.authService.getUser()?.ownerId;
    if (ownerId) {
      this.hotelService.getByOwner(ownerId).subscribe({
        next: (data) => {
          this.hotels = data;
          this.cdr.markForCheck();
        },
        error: () => alert('Failed to load hotels'),
      });
    }
  }

  emptyForm(): RoomRequest {
    return {
      roomType: '',
      description: '',
      amenities: '',
      price: 0,
      totalRooms: 1,
      availableRooms: 0,
      adults: 1,
      children: 0,
      hotelId: 0,
    };
  }

  onHotelChange() {
    this.rooms = [];
    this.showForm = false;
    if (this.selectedHotelId) {
      this.roomService.getByHotel(this.selectedHotelId).subscribe({
        next: (data) => {
          this.rooms = data;
          this.cdr.markForCheck();
        },
        error: () => alert('Failed to load rooms'),
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

  edit(room: Room) {
    this.editingId = room.id;
    this.form = {
      roomType: room.roomType,
      description: room.description,
      amenities: room.amenities,
      price: room.price,
      totalRooms: room.totalRooms,
      availableRooms: room.availableRooms,
      adults: room.adults,
      children: room.children,
      hotelId: room.hotelId,
    };
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
    this.loading = true;
    const request = this.editingId
      ? this.roomService.update(this.editingId, this.form, this.selectedImage)
      : this.roomService.create(this.form, this.selectedImage);

    request.subscribe({
      next: () => {
        this.loading = false;
        this.cancelForm();
        this.onHotelChange();
      },
      error: (err) => {
        this.loading = false;
        alert(err.error?.message || 'Failed to save room');
      },
    });
  }

  deleteRoom(id: number) {
    if (!confirm('Delete this room?')) return;
    this.roomService.delete(id).subscribe({
      next: () => this.onHotelChange(),
      error: () => alert('Failed to delete room'),
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

  editingAvail: { [roomId: number]: boolean } = {};
  availInput: { [roomId: number]: number } = {};

  toggleAvailEdit(room: Room) {
    const id = room.id;
    if (this.editingAvail[id]) {
      this.cancelAvailEdit(id);
    } else {
      this.editingAvail[id] = true;
      this.availInput[id] = room.availableRooms;
    }
  }

  saveAvailability(roomId: number) {
    const count = this.availInput[roomId];
    if (count < 0) return alert('Count cannot be negative');
    this.roomService.updateAvailability(roomId, count).subscribe({
      next: () => {
        this.editingAvail[roomId] = false;
        delete this.availInput[roomId];
        this.onHotelChange();
      },
      error: (err) => alert(err.error?.message || 'Failed to update availability'),
    });
  }

  cancelAvailEdit(roomId: number) {
    this.editingAvail[roomId] = false;
    delete this.availInput[roomId];
  }

  getImageUrl(image: string): string {
    return image ? `${environment.imageBaseUrl}/room/${image}` : '';
  }
}
