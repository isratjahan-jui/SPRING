export interface GalleryRequest {
  hotelId: number;
  caption: string;
  category: string;
}

export interface GalleryResponse {
  id: number;
  imageUrl: string;
  caption: string;
  category: string;
  hotelId: number;
  hotelName: string;
  createdAt: string;
  updatedAt: string;
}
