export interface FoodItem {
  id: number;
  itemName: string;
  description: string;
  foodPrice: number;
  category: string;
  hotelName?: string;
}

export interface FoodItemRequest {
  itemName: string;
  description: string;
  foodPrice: number;
  category: string;
  hotelId: number;
}
