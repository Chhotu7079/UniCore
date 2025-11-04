package com.chhotu.Learning_Management_System.service.impl;

import com.chhotu.Learning_Management_System.entity.Notifications;
import com.chhotu.Learning_Management_System.entity.Users;
import com.chhotu.Learning_Management_System.repository.NotificationsRepository;
import com.chhotu.Learning_Management_System.repository.UsersRepository;
import com.chhotu.Learning_Management_System.service.NotificationsService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class NotificationsServiceImpl implements NotificationsService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationsServiceImpl.class);

    private final NotificationsRepository notificationsRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public NotificationsServiceImpl(NotificationsRepository notificationsRepository,
                                    UsersRepository usersRepository) {
        this.notificationsRepository = notificationsRepository;
        this.usersRepository = usersRepository;
    }

    /**
     * Retrieves all notifications for a user and marks them as read.
     * @param userId the ID of the user
     * @param request the HTTP request used to verify the logged-in user
     * @return list of notification messages
     */
    @Override
    public List<String> getAllNotifications(int userId, HttpServletRequest request) {
        logger.info("Fetching all notifications for user ID: {}", userId);
        checkUserAccess(userId, request);

        List<Notifications> notificationsList = notificationsRepository.findAll();
        List<String> notificationMessages = new ArrayList<>();

        for (Notifications notification : notificationsList) {
            if (notification.getUserId().getUserId() == userId) {
                notification.setRead(true);
                notificationsRepository.save(notification);
                notificationMessages.add(notification.getMessage());
            }
        }

        logger.info("Total notifications retrieved for user ID {}: {}", userId, notificationMessages.size());
        return notificationMessages;
    }

    /**
     * Retrieves all unread notifications for a user and marks them as read.
     * @param userId the ID of the user
     * @param request the HTTP request used to verify the logged-in user
     * @return list of unread notification messages
     */
    @Override
    public List<String> getAllUnreadNotifications(int userId, HttpServletRequest request) {
        logger.info("Fetching unread notifications for user ID: {}", userId);
        checkUserAccess(userId, request);

        List<Notifications> notificationsList = notificationsRepository.findAll();
        List<String> notificationMessages = new ArrayList<>();

        for (Notifications notification : notificationsList) {
            if (notification.getUserId().getUserId() == userId && !notification.isRead()) {
                notification.setRead(true);
                notificationsRepository.save(notification);
                notificationMessages.add(notification.getMessage());
            }
        }

        logger.info("Unread notifications retrieved for user ID {}: {}", userId, notificationMessages.size());
        return notificationMessages;
    }

    /**
     * Sends a notification message to a user.
     * @param message the notification message
     * @param id the ID of the recipient user
     */
    @Override
    public void sendNotification(String message, int id) {
        logger.info("Sending notification to user ID: {} | Message: {}", id, message);
        Users user = usersRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        Notifications notification = new Notifications();
        notification.setUserId(user);
        notification.setRead(false);
        notification.setCreatedTime(new Date());
        notification.setMessage(message);

        notificationsRepository.save(notification);
        logger.info("Notification sent successfully to user ID: {}", id);
    }

    /**
     * Checks if the logged-in user is authorized to access the specified user’s notifications.
     * @param id the user ID being accessed
     * @param request the HTTP request used to get the logged-in user
     */
    private void checkUserAccess(int id, HttpServletRequest request) {
        Users loggedInUser = (Users) request.getSession().getAttribute("user");

        if (loggedInUser == null) {
            logger.error("Unauthorized access: no user is logged in.");
            throw new IllegalArgumentException("No user is logged in.");
        }

        if (loggedInUser.getUserId() != id) {
            logger.error("Access denied for user ID: {}. Logged in user ID: {}", id, loggedInUser.getUserId());
            throw new IllegalArgumentException("ID mismatch. Please provide the correct ID.");
        }

        logger.debug("User access verified for user ID: {}", id);
    }
}
