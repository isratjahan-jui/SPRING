import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RoomService } from '../../../services/room.service';
import { HotelService } from '../../../services/hotel.service';
import { AuthService } from '../../../services/auth.service';
import { Room } from '../../../models/room.model';
import { Hotel } from '../../../models/hotel.model';

@Component({
  selector: 'app-owner-rooms',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './rooms.component.html',
  styleUrls: ['./rooms.component.css'],
})
export class OwnerRoomsComponent implements OnInit {
  private roomService = inject(RoomService);
  private hotelService = inject(HotelService);
  private auth = inject(AuthService);

  hotels: Hotel[] = [];
  rooms: Room[] = [];
  selectedHotelId = '';
  editingRoom: Room | null = null;
  selectedFile: File | null = null;

  formData: any = {
    roomType: '', totalRooms: 1, adults: 2, children: 0,
    pricePerNight: 0, amenities: '', description: '',
  };

  ngOnInit() {
    const oid = this.auth.user()?.ownerId;
    if (oid) this.hotelService.getByOwner(oid).subscribe(d => this.hotels = d);
  }

  onHotelChange() {
    this.loadRooms();
    this.cancelEdit();
  }

  loadRooms() {
    if (this.selectedHotelId) {
      this.roomService.getByHotel(Number(this.selectedHotelId)).subscribe(d => this.rooms = d);
    }
  }

  onFileSelect(e: any) {
    this.selectedFile = e.target.files[0] ?? null;
  }

  saveRoom() {
    const fd = new FormData();
    fd.append('data', new Blob([JSON.stringify({ ...this.formData, hotelId: Number(this.selectedHotelId) })], { type: 'application/json' }));
    if (this.selectedFile) fd.append('image', this.selectedFile);

    const req = this.editingRoom
      ? this.roomService.update(this.editingRoom.id, fd)
      : this.roomService.create(fd);

    req.subscribe(() => {
      this.loadRooms();
      this.cancelEdit();
    });
  }

  editRoom(r: Room) {
    this.editingRoom = r;
    this.formData = {
      roomType: r.roomType, totalRooms: r.totalRooms, adults: r.adults,
      children: r.children, pricePerNight: r.pricePerNight,
      amenities: r.amenities, description: r.description,
    };
    this.selectedFile = null;
    const el = document.getElementById('addRoomForm');
    if (el) el.classList.add('show');
  }

  cancelEdit() {
    this.editingRoom = null;
    this.formData = { roomType: '', totalRooms: 1, adults: 2, children: 0, pricePerNight: 0, amenities: '', description: '' };
    this.selectedFile = null;
  }

  deleteRoom(id: number) {
    if (confirm('Delete this room?')) this.roomService.delete(id).subscribe(() => this.loadRooms());
  }
}
