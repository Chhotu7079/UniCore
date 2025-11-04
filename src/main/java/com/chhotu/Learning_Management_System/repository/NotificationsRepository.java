package com.chhotu.Learning_Management_System.repository;

import com.chhotu.Learning_Management_System.entity.Notifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationsRepository extends JpaRepository<Notifications, Integer> {
}
