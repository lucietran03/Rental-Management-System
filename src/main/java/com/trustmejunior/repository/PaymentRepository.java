package com.trustmejunior.repository;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.ConnectionManager;
import com.trustmejunior.model.Enum.PaymentMethod;
import com.trustmejunior.model.Enum.PaymentStatus;
import com.trustmejunior.model.RentalEntity.Payment;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PaymentRepository {
    private Connection connection;

    public PaymentRepository() {
        connection = ConnectionManager.getConnection();
    }

    // Method to retrieve a payment by its ID from the database
    public Payment getPaymentById(int id) {
        Payment payment = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to fetch payment details based on the payment ID
            String sql = "SELECT * FROM payment WHERE payment_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            // If a payment is found, retrieve its details
            if (resultSet.next()) {
                int paymentId = resultSet.getInt("payment_id");
                double amount = resultSet.getDouble("amount");
                Date dueDate = new Date(resultSet.getDate("due_date").getTime());

                // Retrieve the payment date, if it exists
                Date paymentDate = null;
                java.sql.Date sqlDate = resultSet.getDate("payment_date");
                if (sqlDate != null) {
                    paymentDate = new Date(sqlDate.getTime());
                }

                // Retrieve the payment method, if it exists
                PaymentMethod method = null;
                String paymentMethod = resultSet.getString("method");
                if (paymentMethod != null) {
                    method = PaymentMethod.valueOf(paymentMethod);
                }

                // Retrieve the payment status and other associated details
                PaymentStatus status = PaymentStatus.valueOf(resultSet.getString("status"));
                int rentalAgreementId = resultSet.getInt("ra_id");
                int tenantId = resultSet.getInt("tenant_id");

                // Create a Payment object using the fetched details
                payment = new Payment(paymentId, amount, dueDate, paymentDate, method, status, rentalAgreementId,
                        tenantId);
            }
        } catch (SQLException e) {
            // If there is an error, print the error message
            System.err.println("Error fetching payment: " + e.getMessage());
        } finally {
            // Close the result set and statement to avoid memory leaks
            try {
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

        // Return the Payment object, or null if not found
        return payment;
    }

    // Method to retrieve all payments from the database
    public List<Payment> getAllPayments() {
        List<Payment> payments = new ArrayList<Payment>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to fetch all payments
            String sql = "SELECT * FROM payment";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            // Iterate through each result and create a Payment object
            while (resultSet.next()) {
                int paymentId = resultSet.getInt("payment_id");
                double amount = resultSet.getDouble("amount");
                Date dueDate = new Date(resultSet.getDate("due_date").getTime());

                // Retrieve the payment date, if it exists
                Date paymentDate = null;
                java.sql.Date sqlDate = resultSet.getDate("payment_date");
                if (sqlDate != null) {
                    paymentDate = new Date(sqlDate.getTime());
                }

                // Retrieve the payment method, if it exists
                PaymentMethod method = null;
                String paymentMethod = resultSet.getString("method");
                if (paymentMethod != null) {
                    method = PaymentMethod.valueOf(paymentMethod);
                }

                // Retrieve the payment status and other associated details
                PaymentStatus status = PaymentStatus.valueOf(resultSet.getString("status"));
                int rentalAgreementId = resultSet.getInt("ra_id");
                int tenantId = resultSet.getInt("tenant_id");

                // Create a new Payment object and add it to the list
                Payment payment = new Payment(paymentId, amount, dueDate, paymentDate, method, status,
                        rentalAgreementId, tenantId);
                payments.add(payment);
            }
        } catch (SQLException e) {
            // If there is an error, print the error message
            System.err.println("Error fetching payments: " + e.getMessage());
        } finally {
            // Close the result set and statement to avoid memory leaks
            try {
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

        // Return the list of all payments
        return payments;
    }

    // Method to fetch all payments related to a specific rental agreement ID
    public List<Payment> getPaymentsByRentalAgreementId(int id) {
        List<Payment> payments = new ArrayList<Payment>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to fetch payments where the rental agreement ID matches the
            // provided ID
            String sql = "SELECT * FROM payment WHERE ra_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id); // Set the rental agreement ID parameter
            resultSet = statement.executeQuery(); // Execute the query

            // Iterate through the result set and map each row to a Payment object
            while (resultSet.next()) {
                // Retrieve payment details from the result set
                int paymentId = resultSet.getInt("payment_id");
                double amount = resultSet.getDouble("amount");
                Date dueDate = new Date(resultSet.getDate("due_date").getTime());
                Date paymentDate = null;
                java.sql.Date sqlDate = resultSet.getDate("payment_date");
                if (sqlDate != null) {
                    paymentDate = new Date(sqlDate.getTime()); // If payment date exists, convert it
                }
                PaymentMethod method = null;
                String paymentMethod = resultSet.getString("method");
                if (paymentMethod != null) {
                    method = PaymentMethod.valueOf(paymentMethod); // Convert string to enum PaymentMethod
                }
                PaymentStatus status = PaymentStatus.valueOf(resultSet.getString("status")); // Convert string to enum
                                                                                             // PaymentStatus
                int rentalAgreementId = resultSet.getInt("ra_id");
                int tenantId = resultSet.getInt("tenant_id");

                // Create a new Payment object using the retrieved data
                Payment payment = new Payment(paymentId, amount, dueDate, paymentDate, method, status,
                        rentalAgreementId, tenantId);
                payments.add(payment); // Add the payment to the list
            }
        } catch (SQLException e) {
            // Handle any SQL exceptions and print the error message
            System.err.println("Error fetching payments: " + e.getMessage());
        } finally {
            try {
                // Close the result set and statement to free up resources
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                // Print any exceptions that occur while closing resources
                e.printStackTrace();
            }
        }

        return payments; // Return the list of payments
    }

    // Method to fetch all payments related to a specific tenant ID
    public List<Payment> getPaymentsByTenantId(int id) {
        List<Payment> payments = new ArrayList<Payment>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to fetch payments where the tenant ID matches the provided ID
            String sql = "SELECT * FROM payment WHERE tenant_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id); // Set the tenant ID parameter
            resultSet = statement.executeQuery(); // Execute the query

            // Iterate through the result set and map each row to a Payment object
            while (resultSet.next()) {
                // Retrieve payment details from the result set
                int paymentId = resultSet.getInt("payment_id");
                double amount = resultSet.getDouble("amount");
                Date dueDate = new Date(resultSet.getDate("due_date").getTime());
                Date paymentDate = null;
                java.sql.Date sqlDate = resultSet.getDate("payment_date");
                if (sqlDate != null) {
                    paymentDate = new Date(sqlDate.getTime()); // If payment date exists, convert it
                }
                PaymentMethod method = null;
                String paymentMethod = resultSet.getString("method");
                if (paymentMethod != null) {
                    method = PaymentMethod.valueOf(paymentMethod); // Convert string to enum PaymentMethod
                }
                PaymentStatus status = PaymentStatus.valueOf(resultSet.getString("status")); // Convert string to enum
                                                                                             // PaymentStatus
                int rentalAgreementId = resultSet.getInt("ra_id");
                int tenantId = resultSet.getInt("tenant_id");

                // Create a new Payment object using the retrieved data
                Payment payment = new Payment(paymentId, amount, dueDate, paymentDate, method, status,
                        rentalAgreementId, tenantId);
                payments.add(payment); // Add the payment to the list
            }
        } catch (SQLException e) {
            // Handle any SQL exceptions and print the error message
            System.err.println("Error fetching payments: " + e.getMessage());
        } finally {
            try {
                // Close the result set and statement to free up resources
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                // Print any exceptions that occur while closing resources
                e.printStackTrace();
            }
        }

        return payments; // Return the list of payments
    }

    // Method to create a new payment and insert it into the database
    public Payment createPayment(double amount, Date dueDate, PaymentMethod method, PaymentStatus status,
            int rentalAgreementId, int tenantId) {
        Payment payment = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            // Start a transaction (set auto-commit to false)
            connection.setAutoCommit(false);

            // SQL query to insert a new payment into the 'payment' table
            String sql = "INSERT INTO payment (amount, due_date, method, status, ra_id, tenant_id) VALUES (?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Set the values for the prepared statement
            statement.setDouble(1, amount); // Set payment amount
            statement.setDate(2, new java.sql.Date(dueDate.getTime())); // Set payment due date
            statement.setObject(3, method == null ? null : method.name()); // Set payment method (convert enum to string
                                                                           // if not null)
            statement.setObject(4, status == null ? null : status.name()); // Set payment status (convert enum to string
                                                                           // if not null)
            statement.setInt(5, rentalAgreementId); // Set rental agreement ID
            statement.setInt(6, tenantId); // Set tenant ID

            // Execute the update and check if rows were affected
            int rows = statement.executeUpdate();
            if (rows > 0) {
                // If the insert was successful, get the generated keys (i.e., the new payment
                // ID)
                resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) {
                    int id = resultSet.getInt(1); // Get the generated payment ID
                    payment = getPaymentById(id); // Retrieve the newly created payment using its ID
                }
            }

            // Commit the transaction
            connection.commit();
        } catch (SQLException e) {
            // If any error occurs, print the error message
            System.err.println("Error creating payment: " + e.getMessage());

            try {
                // If an error occurs, roll back the transaction to maintain data integrity
                connection.rollback();
            } catch (SQLException ex) {
                // Print any error that occurs while rolling back the transaction
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            try {
                // Close the result set and statement to free up resources
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }

                // Set auto-commit back to true after the transaction is completed
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                // Print any error that occurs while closing resources
                e.printStackTrace();
            }
        }

        // Return the created payment object
        return payment;
    }

    public Payment updatePayment(int paymentId, double amount, Date dueDate, Date paymentDate, PaymentMethod method,
            PaymentStatus status, int rentalAgreementId, int tenantId) {
        Payment payment = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection.setAutoCommit(false); // Start transaction
            String sql = "UPDATE payment SET amount = ?, due_date = ?, payment_date = ?, method = ?, status = ?, ra_id = ?, tenant_id = ? WHERE payment_id = ?";
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Set parameters for the update query
            statement.setDouble(1, amount);
            statement.setDate(2, new java.sql.Date(dueDate.getTime()));
            statement.setObject(3, paymentDate == null ? null : new java.sql.Date(paymentDate.getTime()));
            statement.setObject(4, method == null ? null : method.name());
            statement.setString(5, status.name());
            statement.setInt(6, rentalAgreementId);
            statement.setInt(7, tenantId);
            statement.setInt(8, paymentId);

            int rows = statement.executeUpdate();

            if (rows > 0) {
                resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    payment = getPaymentById(id); // Fetch updated payment
                }
            }

            connection.commit(); // Commit transaction
        } catch (SQLException e) {
            System.err.println("Error updating payment: " + e.getMessage());
            try {
                connection.rollback(); // Rollback on error
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();
                if (statement != null)
                    statement.close();
                connection.setAutoCommit(true); // Restore auto-commit
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return payment; // Return the updated payment
    }

    // Method to delete a payment by its payment ID
    public void deletePaymentById(int id) {
        PreparedStatement statement = null;

        try {
            // Disable auto-commit to manage the transaction manually
            connection.setAutoCommit(false);

            // SQL query to delete a payment based on its payment ID
            String sql = "DELETE FROM payment WHERE payment_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id); // Set the payment ID to be deleted

            // Execute the delete query
            int rows = statement.executeUpdate();
            if (rows > 0) {
                // If the deletion is successful, print a success message
                System.out.println("Payment deleted successfully");
            }

            // Commit the transaction if everything is successful
            connection.commit();
        } catch (SQLException e) {
            // If an error occurs, print the error and attempt to roll back the transaction
            System.err.println("Error deleting payment: " + e.getMessage());

            try {
                connection.rollback(); // Rollback the transaction
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            try {
                // Close the statement to avoid memory leaks
                if (statement != null) {
                    statement.close();
                }

                // Re-enable auto-commit for future queries
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
