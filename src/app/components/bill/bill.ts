import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { AccountService } from '../../services/account';

interface Bill {
    id: number;
    billType: string;
    amount: number;
    dueDate: string;
    status: string;
}

@Component({
    selector: 'app-bill',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './bill.html',
    styleUrl: './bill.css'
})
export class BillComponent {
    bills = signal<Bill[]>([]);
    accounts = signal<any[]>([]);
    selectedAccountId = signal<number | null>(null);

    constructor(private http: HttpClient, private accountService: AccountService) { }

    ngOnInit() {
        this.loadData();
    }

    loadData() {
        const customerId = localStorage.getItem('customerId');
        if (customerId) {
            // Faturaları Çek
            this.http.get<Bill[]>(`http://localhost:8082/api/bills/${customerId}`).subscribe(data => {
                this.bills.set(data);
            });

            // Hesapları Çek (Ödeme için)
            this.accountService.getAccountsByCustomerId(+customerId).subscribe(data => {
                console.log("Sunucudan gelen hesaplar:", data); // Debug log

                this.accounts.set(data.filter(acc =>
                    acc.accountType && acc.accountType.toUpperCase() === 'VADESIZ'
                ));

                console.log("Filtrelenmiş hesaplar:", this.accounts()); // Debug log

                if (this.accounts().length > 0) {
                    this.selectedAccountId.set(this.accounts()[0].id);
                }
            });
        }
    }

    payBill(bill: Bill) {
        if (!this.selectedAccountId()) {
            alert("Lütfen ödeme yapılacak hesabı seçin!");
            return;
        }

        if (!confirm(`${bill.billType} faturasını (${bill.amount} TL) ödemek istiyor musunuz?`)) return;

        this.http.post(`http://localhost:8082/api/bills/pay/${bill.id}?accountId=${this.selectedAccountId()}`, {}, { responseType: 'text' })
            .subscribe({
                next: (msg) => {
                    alert(msg);
                    this.loadData(); // Listeyi güncelle
                },
                error: (err) => {
                    console.error(err);
                    alert("Ödeme başarısız! Bakiye yetersiz olabilir.");
                }
            });
    }

    onAccountSelect(event: any) {
        this.selectedAccountId.set(+event.target.value); // Convert string to number
    }
}
