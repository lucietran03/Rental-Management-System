package com.trustmejunior.repository;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.ConnectionManager;
import com.trustmejunior.model.Property.PropertyImage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PropertyImageRepository {
    private Connection connection;

    public PropertyImageRepository() {
        connection = ConnectionManager.getConnection();
    }

    // Method to get a PropertyImage by its image_id
    public PropertyImage getPropertyImageById(int id) {
        PropertyImage propertyImage = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to select a property image by its image_id
            String sql = "SELECT * FROM property_image WHERE image_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id); // Set the image_id parameter
            resultSet = statement.executeQuery();

            // If the query returns a result, create a PropertyImage object
            if (resultSet.next()) {
                int imageId = resultSet.getInt("image_id");
                String url = resultSet.getString("url");
                int propertyId = resultSet.getInt("property_id");

                // Create a new PropertyImage object with the result data
                propertyImage = new PropertyImage(imageId, url, propertyId);
            }
        } catch (SQLException e) {
            // Print any SQL exceptions that occur
            e.printStackTrace();
        } finally {
            // Close the result set and statement to free resources
            try {
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

        // Return the retrieved PropertyImage object
        return propertyImage;
    }

    // Method to get a PropertyImage by its property_id
    public PropertyImage getPropertyImageByPropertyId(int id) {
        PropertyImage propertyImage = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to select a property image by its property_id
            String sql = "SELECT * FROM property_image WHERE property_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id); // Set the property_id parameter
            resultSet = statement.executeQuery();

            // If the query returns a result, create a PropertyImage object
            if (resultSet.next()) {
                int imageId = resultSet.getInt("image_id");
                String url = resultSet.getString("url");
                int propertyId = resultSet.getInt("property_id");

                // Create a new PropertyImage object with the result data
                propertyImage = new PropertyImage(imageId, url, propertyId);
            }
        } catch (SQLException e) {
            // Print any SQL exceptions that occur
            e.printStackTrace();
        } finally {
            // Close the result set and statement to free resources
            try {
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

        // Return the retrieved PropertyImage object
        return propertyImage;
    }

    // Method to get all PropertyImages from the database
    public List<PropertyImage> getAllPropertyImages() {
        List<PropertyImage> propertyImages = new ArrayList<PropertyImage>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to select all property images
            String sql = "SELECT * FROM property_image";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            // Iterate through the results and add each PropertyImage to the list
            while (resultSet.next()) {
                int imageId = resultSet.getInt("image_id");
                String url = resultSet.getString("url");
                int propertyId = resultSet.getInt("property_id");

                // Create a new PropertyImage object and add it to the list
                PropertyImage propertyImage = new PropertyImage(imageId, url, propertyId);
                propertyImages.add(propertyImage);
            }
        } catch (SQLException e) {
            // Print any SQL exceptions that occur
            e.printStackTrace();
        } finally {
            // Close the result set and statement to free resources
            try {
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

        // Return the list of all PropertyImage objects
        return propertyImages;
    }

    public PropertyImage createPropertyImage(String url, int propertyId) {
        PropertyImage propertyImage = null;
        PreparedStatement statement = null;

        try {
            connection.setAutoCommit(false); // Start transaction

            // SQL query to insert a new property image
            String sql = "INSERT INTO property_image (url, property_id) VALUES (?, ?)";
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Set parameters for the insert query
            statement.setString(1, url);
            statement.setInt(2, propertyId);
            statement.executeUpdate(); // Execute insert

            // Retrieve the generated image ID
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int imageId = generatedKeys.getInt(1);
                propertyImage = getPropertyImageById(imageId); // Fetch created image
            }

            connection.commit(); // Commit transaction
        } catch (SQLException e) {
            System.err.println("Error creating property image: " + e.getMessage());
            try {
                connection.rollback(); // Rollback on error
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            try {
                if (statement != null)
                    statement.close(); // Close resources
                connection.setAutoCommit(true); // Restore auto-commit
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return propertyImage; // Return the created property image
    }

    public PropertyImage updatePropertyImage(int id, String url, int propertyId) {
        PropertyImage propertyImage = null;
        PreparedStatement statement = null;

        try {
            connection.setAutoCommit(false); // Start transaction

            // SQL query to update the property image
            String sql = "UPDATE property_image SET url = ?, property_id = ? WHERE image_id = ?";
            statement = connection.prepareStatement(sql);

            // Set parameters for the update query
            statement.setString(1, url);
            statement.setInt(2, propertyId);
            statement.setInt(3, id);
            statement.executeUpdate(); // Execute update

            propertyImage = getPropertyImageById(id); // Fetch updated image

            connection.commit(); // Commit transaction
        } catch (SQLException e) {
            System.err.println("Error updating property image: " + e.getMessage());
            try {
                connection.rollback(); // Rollback on error
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            try {
                if (statement != null)
                    statement.close(); // Close resources
                connection.setAutoCommit(true); // Restore auto-commit
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return propertyImage; // Return the updated property image
    }

    // Method to delete a property image by its image ID
    public void deletePropertyImageById(int id) {
        PreparedStatement statement = null;

        try {
            // Disable auto-commit to manage the transaction manually
            connection.setAutoCommit(false);

            // SQL query to delete the property image based on its image ID
            String sql = "DELETE FROM property_image WHERE image_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id); // Set the image ID to delete

            // Execute the delete query
            int rows = statement.executeUpdate();
            if (rows > 0) {
                // If the deletion is successful, print a success message
                System.out.println("Property image deleted successfully");
            }

            // Commit the transaction if everything is successful
            connection.commit();
        } catch (SQLException e) {
            // If an error occurs, print the error and attempt to roll back the transaction
            System.err.println("Error deleting property image: " + e.getMessage());

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
