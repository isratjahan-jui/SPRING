export interface Gallery {
  id: number;
  imageUrl: string;
  caption: string;
  category: string;
  hotelId: number;
  hotelName?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface GalleryRequest {
  caption: string;
  category: string;
  hotelId: number;
}
