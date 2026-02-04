import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TransactionLog } from '../models/transactionLog';

export interface Account {
  id: number;
  accountNumber: string;
  balance: number;
  accountType: string;
  goalAmount: number;
  status: string;
}

@Injectable({
  providedIn: 'root'
})
export class AccountService {

  private apiUrl = 'http://localhost:8082/api/accounts';

  constructor(private http: HttpClient) { }

  getAccounts(): Observable<Account[]> {
    return this.http.get<Account[]>(this.apiUrl);
  }

  getGoalStatus(id: number): Observable<string> {
    return this.http.get(`${this.apiUrl}/${id}/goal-status`, { responseType: 'text' });
  }

  transferMoney(fromId: number, toId: number, amount: number): Observable<string> {
    const url = `${this.apiUrl}/transfer?fromId=${fromId}&toId=${toId}&amount=${amount}`;
    return this.http.post(url, {}, { responseType: 'text' });
  }

  getAccountsByCustomerId(customerId: number): Observable<Account[]> {
    return this.http.get<Account[]>(`${this.apiUrl}/customer/${customerId}`);
  }
  getTransactionsByAccountId(accountId: number): Observable<TransactionLog[]> {
    return this.http.get<TransactionLog[]>(`${this.apiUrl}/${accountId}/transactions`);
  }

  // Yeni Login Metodu
  login(email: string, password: string): Observable<any> {
    return this.http.post('http://localhost:8082/api/customers/login', { email, password });
  }

  getReport(accountId: number): Observable<{ [key: string]: number }> {
    return this.http.get<{ [key: string]: number }>(`${this.apiUrl}/${accountId}/report`);
  }

  applyLoan(customerId: number, amount: number, months: number): Observable<string> {
    const url = `http://localhost:8082/api/loans/apply?customerId=${customerId}&amount=${amount}&months=${months}`;
    return this.http.post(url, {}, { responseType: 'text' });
  }

  getLoanLimit(customerId: number): Observable<number> {
    return this.http.get<number>(`http://localhost:8082/api/loans/limit/${customerId}`);
  }
}
