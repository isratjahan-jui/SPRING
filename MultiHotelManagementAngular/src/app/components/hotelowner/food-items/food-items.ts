import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FoodItem, FoodItemRequest } from '../../../models/food-item.model';
import { Hotel } from '../../../models/hotel.model';
import { FoodItemService } from '../../../services/food-item.service';
import { HotelService } from '../../../services/hotel.service';

@Component({
  selector: 'app-owner-food-items',
  imports: [CommonModule, FormsModule],
  templateUrl: './food-items.html',
  styleUrl: './food-items.css',
})
export class OwnerFoodItems implements OnInit {
  hotels: Hotel[] = [];
  foodItems: FoodItem[] = [];
  selectedHotelId = 0;
  showForm = false;
  editingId: number | null = null;
  form: FoodItemRequest = this.emptyForm();
  loading = false;

  constructor(
    private foodItemService: FoodItemService,
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

  emptyForm(): FoodItemRequest {
    return { itemName: '', description: '', foodPrice: 0, category: '', hotelId: 0 };
  }

  onHotelChange() {
    this.foodItems = [];
    this.showForm = false;
    if (this.selectedHotelId) {
      this.foodItemService.getByHotel(this.selectedHotelId).subscribe({
        next: (data) => (this.foodItems = data),
        error: () => alert('Failed to load food items'),
      });
    }
  }

  addNew() {
    this.editingId = null;
    this.form = { ...this.emptyForm(), hotelId: this.selectedHotelId };
    this.showForm = true;
  }

  edit(item: FoodItem) {
    this.editingId = item.id;
    this.form = {
      itemName: item.itemName,
      description: item.description,
      foodPrice: item.foodPrice,
      category: item.category,
      hotelId: this.selectedHotelId,
    };
    this.showForm = true;
  }

  cancelForm() {
    this.showForm = false;
    this.editingId = null;
  }

  save() {
    this.loading = true;
    const request = this.editingId
      ? this.foodItemService.update(this.editingId, this.form)
      : this.foodItemService.create(this.form);

    request.subscribe({
      next: () => {
        this.loading = false;
        this.cancelForm();
        this.onHotelChange();
      },
      error: (err) => {
        this.loading = false;
        alert(err.error?.message || 'Failed to save food item');
      },
    });
  }

  deleteItem(id: number) {
    if (!confirm('Delete this food item?')) return;
    this.foodItemService.delete(id).subscribe({
      next: () => this.onHotelChange(),
      error: () => alert('Failed to delete food item'),
    });
  }
}
