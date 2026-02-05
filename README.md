🚀 Z-BANK: Full-Stack Banking Application Z-BANK, modern bankacılık ihtiyaçlarını simüle eden, kullanıcıların hesaplarını yönetebildiği, para transferi yapabildiği ve işlem geçmişini anlık olarak takip edebildiği kapsamlı bir web uygulamasıdır.

🛠️ Teknolojiler
Backend (Spring Boot)
Java 17+ & Spring Boot 3.x
Spring Data JPA: Veritabanı yönetimi ve ORM.
H2 Database: Hızlı geliştirme için in-memory veritabanı.
REST API: Clean ve yönetilebilir endpoint yapısı.
Transaction Management: Güvenli para transferi süreçleri.
Frontend (Angular)
Angular 17+ (Standalone Components)
Angular Signals: Modern state yönetimi.
RxJS: Reaktif programlama ve API iletişimi.
Bootstrap / SCSS: Şık ve responsive kullanıcı arayüzü.

✨ Özellikler
👤 Müşteri Bazlı Hesap Yönetimi: Müşteriye özel birden fazla hesap (Vadeli, Kredi, Hedef vb.) görüntüleme.
💸 Güvenli Para Transferi: Hesaplar arası anlık para gönderimi.
📜 İşlem Geçmişi (Audit Logs): Yapılan her işlemin (gelen/giden) detaylı ve renkli takibi.
📊 Dinamik Dashboard: Hesap bakiyelerinin ve hareketlerinin anlık güncellenmesi.
🛡️ Hata Yönetimi: Yetersiz bakiye veya geçersiz işlem kontrolleri.

🏗️ Proje Yapısı
Backend API Endpointleri
MetotEndpointAçıklama
GET/api/accounts/customer/{id}Müşteriye ait hesapları getirir.
POST/api/accounts/transferPara transferi gerçekleştirir.
GET/api/accounts/{id}/transactionsHesabın işlem geçmişini döner.

🚀 Kurulum ve Çalıştırma1.
Backend Hazırlığı
Bash cd backend
mvn clean install
mvn spring-boot:run
API varsayılan olarak http://localhost:8082 portunda çalışacaktır.

2. Frontend HazırlığıBashcd frontend
npm install
ng serve
Uygulama http://localhost:4200 adresinde hazır olacaktır.

👨‍💻 Geliştirici
İsim: [Zeynep YILDIZ]

Bölüm: Bilgisayar Mühendisliği Öğrencisi

LinkedIn: [www.linkedin.com/in/zeynep-yıldız-154a0a309]

