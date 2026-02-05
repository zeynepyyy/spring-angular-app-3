package com.zeynep.customerapp.repository;

import com.zeynep.customerapp.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByCustomerIdOrderByCreateDateDesc(Long customerId);
}
