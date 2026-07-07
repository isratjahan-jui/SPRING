import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FacilityService } from '../../../services/facility.service';
import { HotelService } from '../../../services/hotel.service';
import { AuthService } from '../../../services/auth.service';
import { Hotel } from '../../../models/hotel.model';
import { Facility } from '../../../models/room.model';

@Component({
  selector: 'app-owner-facilities',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './facilities.component.html',
  styleUrls: ['./facilities.component.css'],
})
export class OwnerFacilitiesComponent implements OnInit {
  private svc = inject(FacilityService);
  private hotelSvc = inject(HotelService);
  private auth = inject(AuthService);

  hotels: Hotel[] = [];
  facilities: Facility[] = [];
  selectedHotelId = '';
  newFacilityName = '';
  newFacilityDesc = '';

  ngOnInit() {
    const oid = this.auth.user()?.ownerId;
    if (oid) this.hotelSvc.getByOwner(oid).subscribe(d => this.hotels = d);
  }

  loadFacilities() {
    if (this.selectedHotelId) this.svc.getByHotel(Number(this.selectedHotelId)).subscribe(d => this.facilities = d);
  }

  addFacility() {
    if (!this.newFacilityName.trim()) return;
    this.svc.create({ hotelId: Number(this.selectedHotelId), name: this.newFacilityName, description: this.newFacilityDesc })
      .subscribe(() => {
        this.loadFacilities();
        this.newFacilityName = '';
        this.newFacilityDesc = '';
      });
  }

  deleteFacility(id: number) {
    this.svc.delete(id).subscribe(() => this.loadFacilities());
  }
}
