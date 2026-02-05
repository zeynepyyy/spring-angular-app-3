import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AccountService } from '../../services/account';

@Component({
  selector: 'app-transfer',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './transfer.html',
  styleUrl: './transfer.css'
})
export class TransferComponent {
  fromAccountNumber: string = '';
  toAccountNumber: string = '';
  amount: number = 0;
  message = signal<string>('');

  constructor(private accountService: AccountService) { }

  onTransfer() {
    if (this.fromAccountNumber && this.toAccountNumber && this.amount > 0) {
      // Remove all spaces from account numbers
      const cleanFrom = this.fromAccountNumber.replace(/\s/g, "");
      const cleanTo = this.toAccountNumber.replace(/\s/g, "");

      this.accountService.transferMoney(cleanFrom, cleanTo, this.amount).subscribe({
        next: (res) => {
          this.message.set("✅ " + res);

          setTimeout(() => location.reload(), 2000);
        },
        error: (err) => {
          this.message.set("❌ Hata: Transfer gerçekleşemedi!");
          console.error(err);
        }
      });
    }
  }
}
