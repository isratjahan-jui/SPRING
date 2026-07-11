import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { AuthService } from '../../../services/auth.service';
import { LoginResponse } from '../../../models/auth.model';
import { HotelOwner } from '../../../models/hotel-owner.model';
import { KEYS, StorageService } from '../../../services/storage.service';
import { HotelOwnerService } from '../../../services/hotel-owner.service';
import { CommonModule } from '@angular/common';
import { environment } from '../../../../environments/environments';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-owner-dashboard',
  imports: [CommonModule,RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class OwnerDashboard implements OnInit {

  imageUrl = environment.imageurl + 'owner/';

  user: LoginResponse | null = null;
  userId!: number;
  owner: HotelOwner | null = null;

  private auth = inject(AuthService);
  private ownerService = inject(HotelOwnerService);
  private storage = inject(StorageService);
  private cdr = inject(ChangeDetectorRef);

  loadingParcels = false;


  ngOnInit(): void {
    this.user = this.storage.getUser();
    if (this.user?.userId) {
      this.userId = this.user.userId;
    }

    this.loadOwner();

  }


loadOwner(): void {
    this.ownerService.getOwnerByUserId(this.userId).subscribe({
      next: res => {
        this.owner = res;
        
        this.storage.saveData(KEYS.HOTEL_OWNER, res);
        this.cdr.markForCheck();
        console.log(this.owner);
       
      },
      error: err => console.error(err),
    });
  }



}
