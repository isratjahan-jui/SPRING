import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Room, RoomRequest } from '../../../models/room.model';
import { Hotel } from '../../../models/hotel.model';
import { RoomService } from '../../../services/room.service';
import { HotelService } from '../../../services/hotel.service';

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

  constructor(
    private roomService: RoomService,
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
        next: (data) => (this.rooms = data),
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
}
