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
  fromId: number = 0;
  toId: number = 0;
  amount: number = 0;
  message = signal<string>('');

  constructor(private accountService: AccountService) {}

  onTransfer() {
    if (this.fromId && this.toId && this.amount > 0) {
      this.accountService.transferMoney(this.fromId, this.toId, this.amount).subscribe({
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
