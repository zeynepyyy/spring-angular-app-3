import { Component, OnInit, signal } from '@angular/core';

import { CommonModule } from '@angular/common';

import { AccountService, Account } from '../../services/account';

import { TransactionLog } from '../../models/transactionLog';

import { RouterModule } from '@angular/router';

import { TransferComponent } from '../transfer/transfer';

import { Router } from '@angular/router';



@Component({

  selector: 'app-dashboard',

  standalone: true,

  imports: [CommonModule, RouterModule, TransferComponent],

  templateUrl: './dashboard.html',

  styleUrl: './dashboard.css'

})

export class DashboardComponent implements OnInit {

  accounts = signal<Account[]>([]);
  isLoading = signal<boolean>(true);
  transactionLogs = signal<TransactionLog[]>([]);

  // 1. ADIM: Eksik olan değişkeni ekledik
  currentAccountId: number | null = null;

  constructor(
    private accountService: AccountService,
    private router: Router
  ) { }

  ngOnInit() {
    this.loadAccounts();
  }

  loadAccounts() {
    this.isLoading.set(true);
    const savedId = localStorage.getItem('customerId');

    if (savedId) {
      this.accountService.getAccountsByCustomerId(+savedId).subscribe({
        next: (data) => {
          this.accounts.set(data);
          this.isLoading.set(false);

          // 2. ADIM: Kullanıcı girdiği gibi ilk hesabın loglarını otomatik çekelim
          if (data.length > 0) {
            this.loadTransactions(data[0].id);
          }
        },
        error: (err) => {
          console.error('Hesaplar yüklenirken hata oluştu:', err);
          this.isLoading.set(false);
        }
      });
    } else {
      this.router.navigate(['/login']);
    }
  }

  getAccountClass(type: string): string {
    const t = type.toUpperCase();
    if (t.includes('VADELI')) return 'card-gold';
    if (t.includes('KREDI')) return 'card-red';
    if (t.includes('HEDEF')) return 'card-green';
    return 'card-blue';
  }

  loadTransactions(accountId: number) {
    // Gelen ID'nin sayı olduğundan emin olalım
    this.currentAccountId = Number(accountId);

    this.accountService.getTransactionsByAccountId(this.currentAccountId).subscribe({
      next: (data) => {
        console.log('Backendden Gelen Veri:', data);
        this.transactionLogs.set(data);
      },
      error: (err) => console.error('Hata:', err)
    });
  }

  selectAccount(accountId: number) {
    this.loadTransactions(accountId);
  }
}
