import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environments';

export interface SupportTicketRequest {
  customerId: number;
  subject: string;
  description: string;
  priority: string;
  category?: string;
  hotelId?: number;
  agentId?: number;
  status?: string;
}

export interface SupportTicketResponse {
  id: number;
  subject: string;
  description: string;
  status: string;
  priority: string;
  category?: string;
  customerName: string;
  customerId: number;
  hotelName?: string;
  hotelId?: number;
  agentName?: string;
  agentId?: number;
  replyCount: number;
  escalated: boolean;
  firstResponseAt?: string;
  resolvedAt?: string;
  responseTimeMinutes?: number;
  resolutionTimeMinutes?: number;
  createdAt: string;
  updatedAt: string;
}

export interface SupportReplyRequest {
  message: string;
  ticketId: number;
  replierId: number;
  isInternal?: boolean;
}

export interface SupportReplyResponse {
  id: number;
  message: string;
  ticketId: number;
  replierName: string;
  replierRole: string;
  isInternal: boolean;
  createdAt: string;
}

export interface SupportStats {
  totalTickets: number;
  pendingCount: number;
  inProgressCount: number;
  escalatedCount: number;
  resolvedCount: number;
  closedCount: number;
  unassignedCount: number;
  avgResponseTimeHours: number;
  avgResolutionTimeHours: number;
}

export interface EscalationRequest {
  reason?: string;
  escalatedByUserId?: number;
}

@Injectable({ providedIn: 'root' })
export class CustomerSupportService {
  private apiUrl = environment.apiUrl + 'support';
  private replyUrl = environment.apiUrl + 'support-replies';

  constructor(private http: HttpClient) {}

  create(data: SupportTicketRequest): Observable<SupportTicketResponse> {
    return this.http.post<SupportTicketResponse>(this.apiUrl, data);
  }

  update(id: number, data: Partial<SupportTicketRequest>): Observable<SupportTicketResponse> {
    return this.http.put<SupportTicketResponse>(`${this.apiUrl}/${id}`, data);
  }

  close(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/close`, {});
  }

  getById(id: number): Observable<SupportTicketResponse> {
    return this.http.get<SupportTicketResponse>(`${this.apiUrl}/${id}`);
  }

  getAll(): Observable<SupportTicketResponse[]> {
    return this.http.get<SupportTicketResponse[]>(this.apiUrl);
  }

  getByCustomer(customerId: number): Observable<SupportTicketResponse[]> {
    return this.http.get<SupportTicketResponse[]>(`${this.apiUrl}/customer/${customerId}`);
  }

  getByAgent(agentId: number): Observable<SupportTicketResponse[]> {
    return this.http.get<SupportTicketResponse[]>(`${this.apiUrl}/agent/${agentId}`);
  }

  getByHotel(hotelId: number): Observable<SupportTicketResponse[]> {
    return this.http.get<SupportTicketResponse[]>(`${this.apiUrl}/hotel/${hotelId}`);
  }

  assignAgent(ticketId: number, agentId: number): Observable<SupportTicketResponse> {
    return this.http.put<SupportTicketResponse>(`${this.apiUrl}/${ticketId}/assign/${agentId}`, {});
  }

  escalate(ticketId: number, data?: EscalationRequest): Observable<SupportTicketResponse> {
    return this.http.put<SupportTicketResponse>(`${this.apiUrl}/${ticketId}/escalate`, data || {});
  }

  getStats(): Observable<SupportStats> {
    return this.http.get<SupportStats>(`${this.apiUrl}/stats`);
  }

  getStatsByHotel(hotelId: number): Observable<SupportStats> {
    return this.http.get<SupportStats>(`${this.apiUrl}/stats/hotel/${hotelId}`);
  }

  addReply(data: SupportReplyRequest): Observable<SupportReplyResponse> {
    return this.http.post<SupportReplyResponse>(this.replyUrl, data);
  }

  getReplies(ticketId: number): Observable<SupportReplyResponse[]> {
    return this.http.get<SupportReplyResponse[]>(`${this.replyUrl}/ticket/${ticketId}`);
  }
}
