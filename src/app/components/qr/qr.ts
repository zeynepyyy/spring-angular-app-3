import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AccountService, Account } from '../../services/account';
import { Router } from '@angular/router';

@Component({
  selector: 'app-qr',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './qr.html',
  styleUrl: './qr.css'
})
export class QrComponent implements OnInit {
  isScanning = signal<boolean>(false);
  scanSuccess = signal<boolean>(false);
  cameraPermission = signal<boolean>(false);

  accounts = signal<Account[]>([]);
  selectedAccountId = signal<number | null>(null);
  amount = signal<number>(0);
  action = signal<'WITHDRAW' | 'DEPOSIT'>('WITHDRAW');

  constructor(private accountService: AccountService, private router: Router) { }

  ngOnInit() {
    this.loadAccounts();
  }

  loadAccounts() {
    const customerId = localStorage.getItem('customerId');
    if (customerId) {
      this.accountService.getAccountsByCustomerId(+customerId).subscribe(data => {
        this.accounts.set(data.filter(acc => acc.accountType === 'VADESIZ'));
        if (this.accounts().length > 0) {
          this.selectedAccountId.set(this.accounts()[0].id);
        }
      });
    }
  }

  startScan() {
    this.cameraPermission.set(true);
    this.isScanning.set(true);
    this.scanSuccess.set(false);

    // Simulate scanning delay
    setTimeout(() => {
      this.isScanning.set(false);
      this.scanSuccess.set(true);
    }, 2000);
  }

  confirmTransaction() {
    if (!this.selectedAccountId() || this.amount() <= 0) return;

    this.accountService.processQR(this.selectedAccountId()!, this.amount(), this.action())
      .subscribe({
        next: (res) => {
          alert(res);
          this.router.navigate(['/dashboard']);
        },
        error: (err) => alert('İşlem Başarısız: ' + err.message)
      });
  }
}
