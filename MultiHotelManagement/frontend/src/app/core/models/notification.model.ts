import { NotificationType, NotificationChannel } from './enums.model';

export interface NotificationRequest {
  message: string;
  type: NotificationType;
  channel: NotificationChannel;
  userId: number;
}

export interface NotificationResponse {
  id: number;
  message: string;
  type: NotificationType;
  channel: NotificationChannel;
  readStatus: boolean;
  userName: string;
  createdAt: string;
  updatedAt: string;
}
