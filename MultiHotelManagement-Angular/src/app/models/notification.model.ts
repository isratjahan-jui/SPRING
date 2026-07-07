export interface Notification {
  id: number;
  message: string;
  readStatus: boolean;
  type: string;
  channel: string;
  userId: number;
}
