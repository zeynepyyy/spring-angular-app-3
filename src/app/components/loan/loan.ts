import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AccountService } from '../../services/account';
import { Router, RouterModule } from '@angular/router';

@Component({
    selector: 'app-loan',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule],
    templateUrl: './loan.html',
    styleUrl: './loan.css'
})
export class LoanComponent {
    amount = signal<number>(10000);
    months = signal<number>(12);
    monthlyPayment = signal<number>(0);
    totalRepayment = signal<number>(0);
    isLoading = signal<boolean>(false);

    constructor(private accountService: AccountService, private router: Router) {
        this.calculate();
    }

    calculate() {
        const principal = this.amount();
        const rate = 1.25 / 100; // %1.25 Aylık Faiz
        const time = this.months();

        // Basit Faiz Hesabı: Ana Para + (Ana Para * Faiz * Ay)
        const interest = principal * rate * time;
        const total = principal + interest;

        this.totalRepayment.set(total);
        this.monthlyPayment.set(total / time);
    }

    onAmountChange() {
        this.calculate();
    }

    onMonthChange() {
        this.calculate();
    }

    apply() {
        const customerId = localStorage.getItem('customerId');
        if (!customerId) {
            alert("Lütfen önce giriş yapın!");
            return;
        }

        this.isLoading.set(true);
        this.accountService.applyLoan(+customerId, this.amount(), this.months()).subscribe({
            next: (res) => {
                alert(res);
                this.router.navigate(['/dashboard']);
            },
            error: (err) => {
                console.error(err);
                alert("Kredi başvurusu sırasında bir hata oluştu.");
                this.isLoading.set(false);
            }
        });
    }
}
