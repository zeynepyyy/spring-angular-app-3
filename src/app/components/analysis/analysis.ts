import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AccountService } from '../../services/account';
import { RouterModule } from '@angular/router';

@Component({
    selector: 'app-analysis',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './analysis.html',
    styleUrl: './analysis.css'
})
export class AnalysisComponent implements OnInit {
    chartData = signal<{ category: string, amount: number, color: string, percentage: number }[]>([]);
    totalExpense = signal<number>(0);
    isLoading = signal<boolean>(true);

    // Renk paleti
    colors = ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40'];

    constructor(private accountService: AccountService) { }

    ngOnInit() {
        const customerId = localStorage.getItem('customerId');
        if (customerId) {
            this.accountService.getAccountsByCustomerId(+customerId).subscribe({
                next: (accounts) => {
                    if (accounts.length > 0) {
                        this.loadAllReports(accounts);
                    } else {
                        this.isLoading.set(false);
                    }
                }
            });
        }
    }

    loadAllReports(accounts: any[]) {
        let mergedData: { [key: string]: number } = {};
        let completedRequests = 0;

        accounts.forEach(acc => {
            this.accountService.getReport(acc.id).subscribe({
                next: (data) => {
                    // Verileri birleştir
                    Object.entries(data).forEach(([category, amount]) => {
                        mergedData[category] = (mergedData[category] || 0) + amount;
                    });
                },
                complete: () => {
                    completedRequests++;
                    // Hepsi bittiğinde grafiği çiz
                    if (completedRequests === accounts.length) {
                        this.processChartData(mergedData);
                    }
                }
            });
        });
    }

    processChartData(data: { [key: string]: number }) {
        let total = 0;
        const formattedData: any[] = [];
        const entries = Object.entries(data);

        entries.forEach(([_, amount]) => total += amount);
        this.totalExpense.set(total);

        entries.forEach(([category, amount], index) => {
            formattedData.push({
                category,
                amount,
                color: this.colors[index % this.colors.length],
                percentage: total > 0 ? (amount / total) * 100 : 0
            });
        });

        this.chartData.set(formattedData);
        this.isLoading.set(false);
    }

    getConicGradient(): string {
        let gradient = 'conic-gradient(';
        let currentDeg = 0;

        this.chartData().forEach((item, index) => {
            const deg = (item.percentage / 100) * 360;
            gradient += `${item.color} ${currentDeg}deg ${currentDeg + deg}deg`;
            currentDeg += deg;

            if (index < this.chartData().length - 1) {
                gradient += ', ';
            }
        });

        gradient += ')';
        return gradient;
    }
}
