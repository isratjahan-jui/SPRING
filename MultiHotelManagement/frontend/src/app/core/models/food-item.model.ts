export interface FoodItemRequest {
  itemName: string;
  description: string;
  foodPrice: number;
  category: string;
  hotelId: number;
}

export interface FoodItemResponse {
  id: number;
  itemName: string;
  description: string;
  foodPrice: number;
  category: string;
  hotelName: string;
}
