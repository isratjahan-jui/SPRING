import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environments';

export interface SupportTicketRequest {
  customerId: number;
  subject: string;
  description: string;
  priority: string;
}

export interface SupportTicketResponse {
  id: number;
  subject: string;
  description: string;
  status: string;
  priority: string;
  customerName: string;
  agentName?: string;
  createdAt: string;
  updatedAt: string;
}

@Injectable({ providedIn: 'root' })
export class CustomerSupportService {
  private apiUrl = environment.apiUrl + 'support';

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

  getByCustomer(customerId: number): Observable<SupportTicketResponse[]> {
    return this.http.get<SupportTicketResponse[]>(`${this.apiUrl}/customer/${customerId}`);
  }

  getByAgent(agentId: number): Observable<SupportTicketResponse[]> {
    return this.http.get<SupportTicketResponse[]>(`${this.apiUrl}/agent/${agentId}`);
  }
}
