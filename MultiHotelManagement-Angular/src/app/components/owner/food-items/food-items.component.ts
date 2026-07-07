import { Component, inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { HotelService } from '../../../services/hotel.service';
import { AuthService } from '../../../services/auth.service';
import { Hotel } from '../../../models/hotel.model';
import { FoodItem } from '../../../models/food-item.model';
import { API_URL } from '../../../constants';

@Component({
  selector: 'app-owner-food-items',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './food-items.component.html',
  styleUrls: ['./food-items.component.css'],
})
export class OwnerFoodItemsComponent implements OnInit {
  private http = inject(HttpClient);
  private hotelSvc = inject(HotelService);
  private auth = inject(AuthService);

  hotels: Hotel[] = [];
  items: FoodItem[] = [];
  selectedHotelId = '';
  form = { name: '', price: 0, category: '', description: '' };

  ngOnInit() {
    const oid = this.auth.user()?.ownerId;
    if (oid) this.hotelSvc.getByOwner(oid).subscribe(d => this.hotels = d);
  }

  loadItems() {
    if (this.selectedHotelId) this.http.get<FoodItem[]>(`${API_URL}/food-items/hotel/${this.selectedHotelId}`).subscribe(d => this.items = d);
  }

  addItem() {
    this.http.post<FoodItem>(`${API_URL}/food-items`, { ...this.form, hotelId: Number(this.selectedHotelId) }).subscribe(() => {
      this.loadItems();
      this.form = { name: '', price: 0, category: '', description: '' };
    });
  }

  deleteItem(id: number) {
    if (confirm('Delete?')) this.http.delete(`${API_URL}/food-items/${id}`).subscribe(() => this.loadItems());
  }
}
