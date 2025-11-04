package com.chhotu.Learning_Management_System.service;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

public interface NotificationsService {

    // Fetch all notifications (read and unread) for a user
    List<String> getAllNotifications(int userId, HttpServletRequest request);

    // Fetch only unread notifications for a user
    List<String> getAllUnreadNotifications(int userId, HttpServletRequest request);

    // Send a new notification to a user
    void sendNotification(String message, int id);
}
