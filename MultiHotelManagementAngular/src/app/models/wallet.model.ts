export interface Wallet {
  id: number;
  userId: number;
  userName: string;
  balance: number;
  totalEarned: number;
  totalWithdrawn: number;
  createdAt: string;
}

export interface WalletTransaction {
  id: number;
  amount: number;
  type: 'CREDIT' | 'DEBIT';
  description: string;
  referenceId: number | null;
  createdAt: string;
}
