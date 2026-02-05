import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AccountService, Account } from '../../services/account';
import { TransactionLog } from '../../models/transactionLog';
import { RouterModule, Router } from '@angular/router';
import { TransferComponent } from '../transfer/transfer';
import { NotificationService } from '../../services/notification.service';
import { Notification } from '../../models/notification';
import { FormsModule } from '@angular/forms';


import { ThemeService } from '../../services/theme.service';

@Component({

  selector: 'app-dashboard',

  standalone: true,

  imports: [CommonModule, RouterModule, TransferComponent, FormsModule],

  templateUrl: './dashboard.html',

  styleUrls: ['./dashboard.css']

})

export class DashboardComponent implements OnInit {

  private themeService = inject(ThemeService);


  isSidebarOpen = signal<boolean>(true);
  isDarkMode = this.themeService.isDarkMode;


  accounts = signal<Account[]>([]);
  transactionLogs = signal<TransactionLog[]>([]);
  currencyRates = signal<any>({ usd: 34.20, eur: 37.50, gold: 2450 });

  isLoading = signal<boolean>(true);
  currentAccountId: number | null = null;
  showTransfer = signal<boolean>(false);


  notifications = signal<Notification[]>([]);
  showNotifications = signal<boolean>(false);
  showProfileSettings = signal<boolean>(false);


  showPaymentModal = signal<boolean>(false);
  selectedLoanAccount: Account | null = null;
  paymentAmount: number = 0;
  paymentSourceId: number | null = null;

  activeView = signal<'dashboard' | 'accounts'>('dashboard');

  constructor(
    private accountService: AccountService,
    private notificationService: NotificationService,
    private router: Router
  ) { }

  toggleNotifications() {
    this.showNotifications.update(v => !v);
  }

  toggleProfileSettings() {
    this.showProfileSettings.update(v => !v);
  }


  openPaymentModal(account: Account) {
    this.selectedLoanAccount = account;
    this.paymentAmount = Math.abs(account.balance);
    this.showPaymentModal.set(true);
  }

  closePaymentModal() {
    this.showPaymentModal.set(false);
    this.selectedLoanAccount = null;
    this.paymentAmount = 0;
    this.paymentSourceId = null;
  }

  submitPayment() {
    if (!this.selectedLoanAccount || !this.paymentSourceId || this.paymentAmount <= 0) return;

    this.accountService.repayLoan(this.selectedLoanAccount.id, this.paymentSourceId, this.paymentAmount)
      .subscribe({
        next: (res) => {
          alert(res);
          this.closePaymentModal();
          this.ngOnInit(); 
        },
        error: (err) => alert('Ödeme başarısız: ' + err.message)
      });
  }

  createVirtualCard() {
    const customerId = localStorage.getItem('customerId');
    if (!customerId) return;

    if (!confirm('Limitini kendiniz belirleyebileceğiniz güvenli bir Sanal Kart oluşturulsun mu?')) return;

    const newAccount = {
      accountNumber: 'SANAL-' + Math.floor(100000 + Math.random() * 900000),
      accountType: 'SANAL',
      balance: 1000,
      customer: { id: +customerId },
      status: 'ACTIVE'
    };

    this.accountService.createAccount(newAccount).subscribe({
      next: () => {
        alert('Sanal Kartınız başarıyla oluşturuldu! 🎉');
        this.ngOnInit();
      },
      error: (err) => alert('Hata: ' + err.message)
    });
  }

  setView(view: 'dashboard' | 'accounts') {
    this.activeView.set(view);

    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  get unreadCount() {

    return this.notifications().filter(n => !n.isRead).length;
  }

  toggleTransfer() {
    this.showTransfer.update(v => !v);
  }

  toggleTheme() {
    this.themeService.toggle();
  }


  ngOnInit() {
    this.startCurrencySimulation();

    const savedId = localStorage.getItem('customerId');
    if (savedId) {
      const customerId = +savedId;

      this.accountService.getAccountsByCustomerId(customerId).subscribe({
        next: (data) => {
          this.accounts.set(data);
          this.isLoading.set(false);

          if (data.length > 0) {
            this.loadTransactions(data[0].id);
          }
        },
        error: (err) => {
          console.error('Hesaplar yüklenirken hata:', err);
          this.isLoading.set(false);
        }
      });


      this.notificationService.getNotifications(customerId).subscribe({
        next: (data) => {
          this.notifications.set(data);
        },
        error: (err) => console.error('Bildirimler yüklenemedi:', err)
      });
    } else {
      this.isLoading.set(false);
      this.router.navigate(['/login']);
    }
  }

  startCurrencySimulation() {
    setInterval(() => {
      this.currencyRates.update(rates => ({
        usd: rates.usd + (Math.random() - 0.5) * 0.1,
        eur: rates.eur + (Math.random() - 0.5) * 0.1,
        gold: rates.gold + (Math.random() - 0.5) * 5
      }));
    }, 3000);
  }

  getAccountClass(type: string): string {
    const t = type.toUpperCase();
    if (t.includes('VADELI')) return 'card-gold';
    if (t.includes('KREDI')) return 'card-red';
    if (t.includes('HEDEF')) return 'card-green';
    if (t.includes('SANAL')) return 'card-purple';
    return 'card-blue';
  }

  loadTransactions(accountId: number) {

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

  getCustomerName(): string {
    const accounts = this.accounts();
    if (accounts.length > 0 && accounts[0].customer) {
      return `${accounts[0].customer.firstName} ${accounts[0].customer.lastName} `;
    }
    return 'Sayın Müşteri';
  }
}
