import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TransactionLog } from '../models/transactionLog';

export interface Customer {
  firstName: string;
  lastName: string;
}

export interface Account {
  id: number;
  accountNumber: string;
  balance: number;
  accountType: string;
  goalAmount: number;
  status: string;
  customer?: Customer;
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

  transferMoney(fromAccountNumber: string, toAccountNumber: string, amount: number): Observable<string> {
    const url = `${this.apiUrl}/transfer?fromAccountNumber=${fromAccountNumber}&toAccountNumber=${toAccountNumber}&amount=${amount}`;
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

  repayLoan(loanAccountId: number, sourceAccountId: number, amount: number): Observable<string> {
    const url = `http://localhost:8082/api/loans/repay?loanAccountId=${loanAccountId}&sourceAccountId=${sourceAccountId}&amount=${amount}`;
    return this.http.post(url, {}, { responseType: 'text' });
  }

  processQR(accountId: number, amount: number, action: 'WITHDRAW' | 'DEPOSIT'): Observable<string> {
    const url = `http://localhost:8082/api/accounts/qr?accountId=${accountId}&amount=${amount}&action=${action}`;
    return this.http.post(url, {}, { responseType: 'text' });
  }

  createAccount(account: any): Observable<Account> {
    return this.http.post<Account>(this.apiUrl, account);
  }
}
