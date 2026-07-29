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
  createdAt: string;   // LocalDateTime → ISO string
  updatedAt: string;   // LocalDateTime → ISO string
}

// Enum for NotificationType (backend এর সাথে match করবে)
export enum NotificationType {
  BOOKING_CONFIRMED = 'BOOKING_CONFIRMED',
  BOOKING_CANCELLED = 'BOOKING_CANCELLED',
  BOOKING_REMINDER = 'BOOKING_REMINDER',

  PAYMENT_SUCCESSFUL = 'PAYMENT_SUCCESSFUL',
  PAYMENT_FAILED = 'PAYMENT_FAILED',
  PAYMENT_REFUNDED = 'PAYMENT_REFUNDED',

  HOTEL_APPROVED = 'HOTEL_APPROVED',
  HOTEL_REJECTED = 'HOTEL_REJECTED',

  REVIEW_RECEIVED = 'REVIEW_RECEIVED',
  SUPPORT_REPLIED = 'SUPPORT_REPLIED',

  GENERAL = 'GENERAL',
  PROMOTIONAL = 'PROMOTIONAL'
}

// Enum for NotificationChannel (backend এর সাথে match করবে)
export enum NotificationChannel {
  EMAIL = 'EMAIL',
  SMS = 'SMS',
  PUSH = 'PUSH',
  WEB = 'WEB',
  SYSTEM = 'SYSTEM'
}
