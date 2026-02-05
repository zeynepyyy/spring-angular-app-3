package com.zeynep.customerapp.service;

import com.zeynep.customerapp.model.Notification;
import com.zeynep.customerapp.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> getNotificationsForCustomer(Long customerId) {
        return notificationRepository.findByCustomerIdOrderByCreateDateDesc(customerId);
    }

    public void createNotification(Long customerId, String message) {
        Notification notification = new Notification(customerId, message);
        notificationRepository.save(notification);
    }

    // Uygulama başlatıldığında veya test için örnek veri oluşturan metot
    public void createSampleNotificationsIfEmpty(Long customerId) {
        List<Notification> existing = getNotificationsForCustomer(customerId);
        if (existing.isEmpty()) {
            createNotification(customerId, "Z-Bank'a hoş geldiniz! Hesaplarınız kullanıma hazırdır.");
            createNotification(customerId, "Kredi kartı başvurunuz onaylanmıştır.");
            createNotification(customerId, "Otomatik ödeme talimatınız başarıyla oluşturuldu.");
        }
    }
}
