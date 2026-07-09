export interface CustomerSupportRequest {
  subject: string;
  description: string;
  status: string;
  priority: string;
  customerId: number;
  agentId?: number;
}

export interface CustomerSupportResponse {
  id: number;
  subject: string;
  description: string;
  status: string;
  priority: string;
  customerName: string;
  agentName: string;
  createdAt: string;
  updatedAt: string;
}
