package com.zeynep.customerapp.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zeynep.customerapp.model.Notification;
import com.zeynep.customerapp.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:4200")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/{customerId}")
    public List<Notification> getNotifications(@PathVariable Long customerId) {

        notificationService.createSampleNotificationsIfEmpty(customerId);
        return notificationService.getNotificationsForCustomer(customerId);
    }

    @org.springframework.web.bind.annotation.PostMapping
    public String createNotification(@org.springframework.web.bind.annotation.RequestBody Notification notification) {
        notificationService.createNotification(notification.getCustomerId(), notification.getMessage());
        return "Bildirim başarıyla oluşturuldu.";
    }
}
