export interface Report {
  id: number;
  totalBookings: number;
  income: number;
  occupancyRate: number;
  type: string;
  hotelName: string;
  generatedAt: string;
}
