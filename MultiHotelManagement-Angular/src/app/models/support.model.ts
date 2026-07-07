export interface SupportTicket {
  id: number;
  subject: string;
  description: string;
  status: string;
  customerId: number;
  agentId?: number;
}
