import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AccountService } from '../../services/account';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class LoginComponent implements OnInit {

  username = '';
  password = '';

  constructor(
    private router: Router,
    private accountService: AccountService
  ) { }

  ngOnInit() {
    // Varsa eski oturumu temizleyelim
    localStorage.removeItem('customerId');
  }

  onLogin() {
    if (this.username && this.password) {
      this.accountService.login(this.username, this.password).subscribe({
        next: (response: any) => {
          console.log('Giriş Başarılı:', response);

          // Backend'den dönen Customer objesinden ID'yi alıp kaydediyoruz
          if (response && response.id) {
            localStorage.setItem('customerId', response.id.toString());
            // Opsiyonel: İsmi de kaydedelim
            localStorage.setItem('customerName', response.firstName + ' ' + response.lastName);

            this.router.navigate(['/dashboard']);
          }
        },
        error: (err) => {
          console.error("Giriş hatası:", err);
          alert('Giriş başarısız! E-posta veya şifre hatalı.');
        }
      });
    } else {
      alert('Lütfen tüm alanları doldurun!');
    }
  }
}
