package com.trustmejunior.repository;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.ConnectionManager;
import com.trustmejunior.model.Notification.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationRepository {
    private Connection connection;

    public NotificationRepository() {
        connection = ConnectionManager.getConnection();
    }

    // Method to fetch a specific notification by its ID
    public Notification getNotificationById(int id) {
        Notification notification = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to select notification details by its ID
            String sql = "SELECT * FROM notification WHERE id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id); // Set the notification ID in the query
            resultSet = statement.executeQuery(); // Execute the query

            // If a notification with the given ID is found
            if (resultSet.next()) {
                // Extract values from the result set
                int notiId = resultSet.getInt("id");
                String content = resultSet.getString("content");
                int paymentId = resultSet.getInt("payment_id");
                int receiverId = resultSet.getInt("receiver_id");
                int senderId = resultSet.getInt("sender_id");
                String sendtime = resultSet.getString("created_at");

                // Create a new notification object
                notification = new Notification(notiId, receiverId, senderId, paymentId, content);
                // Convert sendtime to LocalDateTime and set it to the notification
                notification.setTimestamp(Notification.convertToLocalDateTime(sendtime));
            }
        } catch (SQLException e) {
            // Handle SQL exceptions by printing the error message
            System.err.println("Error fetching notification: " + e.getMessage());
        } finally {
            try {
                // Close the result set and statement in the finally block
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                // Print any errors that occur while closing resources
                e.printStackTrace();
            }
        }

        return notification; // Return the notification object (or null if not found)
    }

    // Method to fetch all notifications from the database
    public List<Notification> getAllNotification() {
        List<Notification> notifications = new ArrayList<Notification>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to select all notifications
            String sql = "SELECT * FROM notification";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery(); // Execute the query

            // Loop through all the result set rows
            while (resultSet.next()) {
                // Extract values from the result set
                int notiId = resultSet.getInt("id");
                String content = resultSet.getString("content");
                int paymentId = resultSet.getInt("payment_id");
                int senderId = resultSet.getInt("sender_id");
                int receiverId = resultSet.getInt("receiver_id");
                String sendtime = resultSet.getString("created_at");

                // Create a new notification object
                Notification notification = new Notification(notiId, receiverId, senderId, paymentId, content);
                // Convert sendtime to LocalDateTime and set it to the notification
                notification.setTimestamp(Notification.convertToLocalDateTime(sendtime));

                // Add the notification to the list
                notifications.add(notification);
            }
        } catch (SQLException e) {
            // Handle SQL exceptions by printing the error message
            System.err.println("Error fetching notification: " + e.getMessage());
        } finally {
            try {
                // Close the result set and statement in the finally block
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                // Print any errors that occur while closing resources
                e.printStackTrace();
            }
        }

        return notifications; // Return the list of notifications
    }

    // Method to fetch notifications by the receiver's ID
    public List<Notification> getNotificationByReceiverId(int id) {
        List<Notification> notifications = new ArrayList<Notification>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to select notifications for a specific receiver
            String sql = "SELECT * FROM notification WHERE receiver_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id); // Set the receiver ID in the query
            resultSet = statement.executeQuery(); // Execute the query

            // Loop through all the result set rows
            while (resultSet.next()) {
                // Extract values from the result set
                int notiId = resultSet.getInt("id");
                String content = resultSet.getString("content");
                int paymentId = resultSet.getInt("payment_id");
                int senderId = resultSet.getInt("sender_id");
                int receiverId = resultSet.getInt("receiver_id");
                String sendtime = resultSet.getString("created_at");

                // Create a new notification object
                Notification notification = new Notification(notiId, receiverId, senderId, paymentId, content);
                // Convert sendtime to LocalDateTime and set it to the notification
                notification.setTimestamp(Notification.convertToLocalDateTime(sendtime));

                // Add the notification to the list
                notifications.add(notification);
            }
        } catch (SQLException e) {
            // Handle SQL exceptions by printing the error message
            System.err.println("Error fetching notification: " + e.getMessage());
        } finally {
            try {
                // Close the result set and statement in the finally block
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                // Print any errors that occur while closing resources
                e.printStackTrace();
            }
        }

        return notifications; // Return the list of notifications for the specified receiver
    }

    // Method to get notifications by senderId
    public List<Notification> getNotificationBySenderId(int id) {
        List<Notification> notifications = new ArrayList<Notification>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to fetch notifications by sender_id
            String sql = "SELECT * FROM notification WHERE sender_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id); // Set senderId parameter in the query
            resultSet = statement.executeQuery(); // Execute the query

            // If notifications are found, create Notification objects
            if (resultSet.next()) {
                int notiId = resultSet.getInt("id");
                String content = resultSet.getString("content");
                int paymentId = resultSet.getInt("payment_id");
                int senderId = resultSet.getInt("sender_id");
                int receiverId = resultSet.getInt("receiver_id");
                String sendtime = resultSet.getString("created_at");

                // Initialize Notification object with the result data
                Notification notification = new Notification(notiId, receiverId, senderId, paymentId, content);
                notification.setTimestamp(Notification.convertToLocalDateTime(sendtime)); // Set timestamp from string
                                                                                          // to LocalDateTime

                notifications.add(notification); // Add the notification to the list
            }
        } catch (SQLException e) {
            // Print error message if an exception occurs
            System.err.println("Error fetching notification: " + e.getMessage());
        } finally {
            try {
                // Close the resultSet and statement to release resources
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Return the list of notifications
        return notifications;
    }

    public Notification sendNotification(String content, int paymentId, int senderId, int receiverId) {
        Notification notification = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection.setAutoCommit(false); // Disable auto-commit for manual transaction management
            String sql = "INSERT INTO notification (content, payment_id, sender_id, receiver_id) VALUES (?, ?, ?, ?)";
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, content);
            statement.setInt(2, paymentId);
            statement.setInt(3, senderId);
            statement.setInt(4, receiverId);

            int rows = statement.executeUpdate(); // Execute the insertion query

            if (rows > 0) {
                resultSet = statement.getGeneratedKeys(); // Retrieve the generated keys (notification ID)
                if (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    notification = getNotificationById(id); // Get the notification by its ID
                }
            }

            connection.commit(); // Commit the transaction if successful
        } catch (SQLException e) {
            System.err.println("Error creating notification: " + e.getMessage());
            try {
                connection.rollback(); // Rollback transaction in case of an error
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            try {
                if (resultSet != null)
                    resultSet.close(); // Close resources
                if (statement != null)
                    statement.close();
                connection.setAutoCommit(true); // Restore auto-commit mode
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return notification; // Return the created notification
    }

    // Method to get notifications by paymentId
    public List<Notification> getNotificationByPaymentId(int id) {
        List<Notification> notifications = new ArrayList<Notification>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to fetch notifications by payment_id
            String sql = "SELECT * FROM notification WHERE payment_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id); // Set paymentId parameter in the query
            resultSet = statement.executeQuery(); // Execute the query

            // If notifications are found, create Notification objects
            if (resultSet.next()) {
                int notiId = resultSet.getInt("id");
                String content = resultSet.getString("content");
                int paymentId = resultSet.getInt("payment_id");
                int senderId = resultSet.getInt("sender_id");
                int receiverId = resultSet.getInt("receiver_id");
                String sendtime = resultSet.getString("created_at");

                // Initialize Notification object with the result data
                Notification notification = new Notification(notiId, receiverId, senderId, paymentId, content);
                notification.setTimestamp(Notification.convertToLocalDateTime(sendtime)); // Set timestamp from string
                                                                                          // to LocalDateTime

                notifications.add(notification); // Add the notification to the list
            }
        } catch (SQLException e) {
            // Print error message if an exception occurs
            System.err.println("Error fetching notification: " + e.getMessage());
        } finally {
            try {
                // Close the resultSet and statement to release resources
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Return the list of notifications
        return notifications;
    }
}
