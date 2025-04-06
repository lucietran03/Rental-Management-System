package com.trustmejunior.repository;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.ConnectionManager;
import com.trustmejunior.model.Property.CommercialProperty;
import com.trustmejunior.model.Enum.PropertyStatus;
import com.trustmejunior.model.Enum.PropertyBusinessType;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommercialPropertyRepository {
    private Connection connection;

    public CommercialPropertyRepository() {
        connection = ConnectionManager.getConnection();
    }

    // Method to fetch a commercial property by its ID
    public CommercialProperty getCommercialPropertyById(int propertyId) {
        CommercialProperty commercialProperty = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to fetch property details based on the property ID
            String sql = "SELECT p.*, cp.* FROM property p JOIN commercial_property cp ON p.property_id = cp.cproperty_id WHERE p.property_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, propertyId); // Setting the property ID parameter in the query
            resultSet = statement.executeQuery();

            if (resultSet.next()) { // If the property is found in the result set
                // Extract property details from the result set
                int cPropertyId = resultSet.getInt("cproperty_id");
                double price = resultSet.getDouble("price");
                String address = resultSet.getString("address");
                PropertyStatus status = PropertyStatus.valueOf(resultSet.getString("status"));
                int ownerId = resultSet.getInt("owner_id");
                PropertyBusinessType businessType = PropertyBusinessType.valueOf(resultSet.getString("business_type"));
                double area = resultSet.getDouble("area");
                boolean hasParking = resultSet.getBoolean("has_parking");

                // Create a new CommercialProperty object using the retrieved details
                commercialProperty = new CommercialProperty(cPropertyId, price, address, status, ownerId, businessType,
                        area, hasParking);
            }
        } catch (SQLException e) {
            // Handle any SQL errors during the query execution
            System.err.println("Error fetching commercial property: " + e.getMessage());
        } finally {
            // Close the result set and statement to prevent memory leaks
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

        return commercialProperty; // Return the fetched commercial property
    }

    // Method to fetch all commercial properties
    public List<CommercialProperty> getAllCommercialProperties() {
        List<CommercialProperty> commercialProperties = new ArrayList<CommercialProperty>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to fetch all commercial properties
            String sql = "SELECT p.*, cp.* FROM property p JOIN commercial_property cp ON p.property_id = cp.cproperty_id";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) { // Loop through the result set to retrieve each property
                // Extract property details
                int cPropertyId = resultSet.getInt("cproperty_id");
                double price = resultSet.getDouble("price");
                String address = resultSet.getString("address");
                PropertyStatus status = PropertyStatus.valueOf(resultSet.getString("status"));
                int ownerId = resultSet.getInt("owner_id");
                PropertyBusinessType businessType = PropertyBusinessType.valueOf(resultSet.getString("business_type"));
                double area = resultSet.getDouble("area");
                boolean hasParking = resultSet.getBoolean("has_parking");

                // Create a new CommercialProperty object for each result
                CommercialProperty commercialProperty = new CommercialProperty(cPropertyId, price, address, status,
                        ownerId, businessType, area, hasParking);
                commercialProperties.add(commercialProperty); // Add the property to the list
            }
        } catch (SQLException e) {
            // Handle any SQL errors during the query execution
            System.err.println("Error fetching commercial properties: " + e.getMessage());
        } finally {
            // Close the result set and statement to prevent memory leaks
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

        return commercialProperties; // Return the list of all commercial properties
    }

    // Method to fetch commercial properties by host ID
    public List<CommercialProperty> getCommercialPropertiesByHostId(int id) {
        List<CommercialProperty> commercialProperties = new ArrayList<CommercialProperty>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to fetch commercial properties based on the host ID
            String sql = "SELECT p.*, cp.* FROM property p JOIN commercial_property cp ON p.property_id = cp.cproperty_id JOIN host_property hp ON p.property_id = hp.property_id WHERE hp.host_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id); // Setting the host ID parameter in the query
            resultSet = statement.executeQuery();

            while (resultSet.next()) { // Loop through the result set to retrieve each property
                // Extract property details
                int cPropertyId = resultSet.getInt("cproperty_id");
                double price = resultSet.getDouble("price");
                String address = resultSet.getString("address");
                PropertyStatus status = PropertyStatus.valueOf(resultSet.getString("status"));
                int ownerId = resultSet.getInt("owner_id");
                PropertyBusinessType businessType = PropertyBusinessType.valueOf(resultSet.getString("business_type"));
                double area = resultSet.getDouble("area");
                boolean hasParking = resultSet.getBoolean("has_parking");

                // Create a new CommercialProperty object for each result
                CommercialProperty commercialProperty = new CommercialProperty(cPropertyId, price, address, status,
                        ownerId, businessType, area, hasParking);
                commercialProperties.add(commercialProperty); // Add the property to the list
            }
        } catch (SQLException e) {
            // Handle any SQL errors during the query execution
            System.err.println("Error fetching commercial properties: " + e.getMessage());
        } finally {
            // Close the result set and statement to prevent memory leaks
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

        return commercialProperties; // Return the list of commercial properties for the specified host
    }

    // Method to fetch commercial properties by owner ID
    public List<CommercialProperty> getCommercialPropertiesByOwnerId(int id) {
        List<CommercialProperty> commercialProperties = new ArrayList<CommercialProperty>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to fetch commercial properties based on the owner ID
            String sql = "SELECT p.*, cp.* FROM property p JOIN commercial_property cp ON p.property_id = cp.cproperty_id WHERE p.owner_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id); // Setting the owner ID parameter in the query
            resultSet = statement.executeQuery();

            while (resultSet.next()) { // Loop through the result set to retrieve each property
                // Extract property details
                int cPropertyId = resultSet.getInt("cproperty_id");
                double price = resultSet.getDouble("price");
                String address = resultSet.getString("address");
                PropertyStatus status = PropertyStatus.valueOf(resultSet.getString("status"));
                int ownerId = resultSet.getInt("owner_id");
                PropertyBusinessType businessType = PropertyBusinessType.valueOf(resultSet.getString("business_type"));
                double area = resultSet.getDouble("area");
                boolean hasParking = resultSet.getBoolean("has_parking");

                // Create a new CommercialProperty object for each result
                CommercialProperty commercialProperty = new CommercialProperty(cPropertyId, price, address, status,
                        ownerId, businessType, area, hasParking);
                commercialProperties.add(commercialProperty); // Add the property to the list
            }
        } catch (SQLException e) {
            // Handle any SQL errors during the query execution
            System.err.println("Error fetching commercial properties: " + e.getMessage());
        } finally {
            // Close the result set and statement to prevent memory leaks
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

        return commercialProperties; // Return the list of commercial properties for the specified owner
    }

    // Method to create a new commercial property in the database
    public CommercialProperty createCommercialProperty(int cPropertyId, PropertyBusinessType businessType, double area,
            boolean hasParking) {
        CommercialProperty commercialProperty = null;
        PreparedStatement statement = null;

        try {
            // Disable auto-commit to handle transaction manually
            connection.setAutoCommit(false);

            // SQL query to insert a new record into the commercial_property table
            String sql = "INSERT INTO commercial_property (cproperty_id, business_type, area, has_parking) VALUES (?, ?, ?, ?)";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, cPropertyId); // Set the commercial property ID
            statement.setString(2, businessType.name()); // Set the business type as a string
            statement.setDouble(3, area); // Set the area of the property
            statement.setBoolean(4, hasParking); // Set whether the property has parking

            // Execute the query and check if rows were affected
            int rows = statement.executeUpdate();
            if (rows > 0) {
                // If successful, retrieve the created commercial property by ID
                commercialProperty = getCommercialPropertyById(cPropertyId);
            }

            // Commit the transaction if successful
            connection.commit();
        } catch (SQLException e) {
            // Print the error message if an exception occurs
            System.err.println("Error creating commercial property: " + e.getMessage());

            try {
                // Rollback the transaction in case of an error
                connection.rollback();
            } catch (SQLException ex) {
                // Print an error message if rollback fails
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            try {
                // Close the PreparedStatement if it is not null
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                // Print stack trace if closing the statement fails
                e.printStackTrace();
            }
        }

        return commercialProperty; // Return the created commercial property (or null if failed)
    }

    // Method to update an existing commercial property in the database
    public CommercialProperty updateCommercialProperty(int cPropertyId, PropertyBusinessType businessType, double area,
            boolean hasParking) {
        CommercialProperty commercialProperty = null;
        PreparedStatement statement = null;

        try {
            // Disable auto-commit to handle transaction manually
            connection.setAutoCommit(false);

            // SQL query to update the commercial_property table
            String sql = "UPDATE commercial_property SET business_type = ?, area = ?, has_parking = ? WHERE cproperty_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, businessType.name()); // Set the business type
            statement.setDouble(2, area); // Set the area
            statement.setBoolean(3, hasParking); // Set the parking availability
            statement.setInt(4, cPropertyId); // Set the property ID for the update

            // Execute the update query and check if rows were affected
            int rows = statement.executeUpdate();
            if (rows > 0) {
                // If successful, retrieve the updated commercial property by ID
                commercialProperty = getCommercialPropertyById(cPropertyId);
            }

            // Commit the transaction if successful
            connection.commit();
        } catch (SQLException e) {
            // Print the error message if an exception occurs
            System.err.println("Error updating commercial property: " + e.getMessage());

            try {
                // Rollback the transaction in case of an error
                connection.rollback();
            } catch (SQLException ex) {
                // Print an error message if rollback fails
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            try {
                // Close the PreparedStatement if it is not null
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                // Print stack trace if closing the statement fails
                e.printStackTrace();
            }
        }

        return commercialProperty; // Return the updated commercial property (or null if failed)
    }

    // Method to delete a commercial property by its ID from the database
    public void deleteCommercialPropertyById(int propertyId) {
        PreparedStatement statement = null;

        try {
            // Disable auto-commit to handle transaction manually
            connection.setAutoCommit(false);

            // SQL query to delete a commercial property by its ID
            String sql = "DELETE FROM commercial_property WHERE cproperty_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, propertyId); // Set the property ID to delete
            statement.executeUpdate(); // Execute the deletion query

            // Commit the transaction if successful
            connection.commit();
        } catch (SQLException e) {
            // Print the error message if an exception occurs
            System.err.println("Error deleting commercial property: " + e.getMessage());

            try {
                // Rollback the transaction in case of an error
                connection.rollback();
            } catch (SQLException ex) {
                // Print an error message if rollback fails
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            try {
                // Close the PreparedStatement if it is not null
                if (statement != null) {
                    statement.close();
                }

                // Re-enable auto-commit mode for future operations
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                // Print stack trace if closing the statement or re-enabling auto-commit fails
                e.printStackTrace();
            }
        }
    }
}
