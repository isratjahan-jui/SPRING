import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environments';
import { Wallet, WalletTransaction } from '../models/wallet.model';

@Injectable({ providedIn: 'root' })
export class WalletService {
  private apiUrl = environment.apiUrl + 'wallets';

  constructor(private http: HttpClient) {}

  getByUser(userId: number): Observable<Wallet> {
    return this.http.get<Wallet>(`${this.apiUrl}/user/${userId}`);
  }

  getTransactions(walletId: number): Observable<WalletTransaction[]> {
    return this.http.get<WalletTransaction[]>(`${this.apiUrl}/${walletId}/transactions`);
  }
}
