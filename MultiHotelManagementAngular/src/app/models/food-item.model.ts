export interface FoodItem {
  id: number;
  itemName: string;
  imageUrl?: string;
  description: string;
  foodPrice: number;
  category: string;
  hotelId?: number;
  hotelName?: string;
}

export interface FoodItemRequest {
  itemName: string;
  description: string;
  foodPrice: number;
  category: string;
  hotelId: number;
}
