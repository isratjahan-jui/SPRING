import { CustomerSupportTicketStatus, CustomerSupportTicketPriority } from './enums.model';

export interface CustomerSupportRequest {
  subject: string;
  description: string;
  status: CustomerSupportTicketStatus;
  priority: CustomerSupportTicketPriority;
  customerId: number;
  agentId?: number;
}

export interface CustomerSupportResponse {
  id: number;
  subject: string;
  description: string;
  status: CustomerSupportTicketStatus;
  priority: CustomerSupportTicketPriority;
  customerName: string;
  agentName: string;
  createdAt: string;
  updatedAt: string;
}
