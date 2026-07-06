import { ReportType } from './enums.model';

export interface ReportRequest {
  totalBookings: number;
  income: number;
  occupancyRate: number;
  type: ReportType;
  hotelId: number;
}

export interface ReportResponse {
  id: number;
  totalBookings: number;
  income: number;
  occupancyRate: number;
  type: ReportType;
  hotelName: string;
  generatedAt: string;
  updatedAt: string;
}
