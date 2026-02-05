import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AccountService } from '../../services/account';

@Component({
    selector: 'app-exchange',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './exchange.html',
    styleUrl: './exchange.css'
})
export class ExchangeComponent {
    rates = signal<any>({});
    accounts = signal<any[]>([]);

    fromCurrency = signal<string>('TRY');
    toCurrency = signal<string>('USD');
    amount = signal<number>(0);
    estimatedAmount = signal<number>(0);

    constructor(private http: HttpClient, private accountService: AccountService) { }

    ngOnInit() {
        this.refreshRates();
        this.loadAccounts();

        // Her 30 saniyede bir kuru güncelle
        setInterval(() => this.refreshRates(), 30000);
    }

    refreshRates() {
        this.http.get('http://localhost:8082/api/exchange/rates').subscribe(data => {
            this.rates.set(data);
            this.calculate();
        });
    }

    loadAccounts() {
        const customerId = localStorage.getItem('customerId');
        if (customerId) {
            this.accountService.getAccountsByCustomerId(+customerId).subscribe(data => {
                this.accounts.set(data.filter(acc => acc.accountType === 'VADESIZ'));
            });
        }
    }

    calculate() {
        if (this.amount() <= 0) return;

        const fromRate = this.rates()[this.fromCurrency()] || 1;
        const toRate = this.rates()[this.toCurrency()] || 1;

        // Formül: Miktar * (GirişKuru / ÇıkışKuru)
        const result = this.amount() * (fromRate / toRate);
        this.estimatedAmount.set(result);
    }

    onTrade() {
        const customerId = localStorage.getItem('customerId');
        if (!customerId) return;

        if (!confirm(`${this.amount()} ${this.fromCurrency()} karşılığında ${this.toCurrency()} almak istiyor musunuz?`)) return;

        const url = `http://localhost:8082/api/exchange/trade?customerId=${customerId}&fromCurrency=${this.fromCurrency()}&toCurrency=${this.toCurrency()}&amount=${this.amount()}`;

        this.http.post(url, {}, { responseType: 'text' }).subscribe({
            next: (res) => {
                alert(res);
                this.loadAccounts(); // Hesapları güncelle
                this.amount.set(0);
            },
            error: (err) => alert("İşlem Başarısız: " + err.error)
        });
    }
}
