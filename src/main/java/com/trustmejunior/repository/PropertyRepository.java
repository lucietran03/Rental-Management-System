package com.trustmejunior.repository;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.ConnectionManager;
import com.trustmejunior.model.Enum.PropertyStatus;
import com.trustmejunior.model.Enum.PropertyType;
import com.trustmejunior.model.Property.Property;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PropertyRepository {
    private Connection connection;

    public PropertyRepository() {
        connection = ConnectionManager.getConnection();
    }

    // Method to get a Property by its property_id
    public Property getPropertyById(int id) {
        Property property = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to select a property by its property_id
            String sql = "SELECT * FROM property WHERE property_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id); // Set the property_id parameter
            resultSet = statement.executeQuery();

            // If the query returns a result, create a Property object
            if (resultSet.next()) {
                int propertyId = resultSet.getInt("property_id");
                double price = resultSet.getDouble("price");
                String address = resultSet.getString("address");
                PropertyStatus status = PropertyStatus.valueOf(resultSet.getString("status"));
                PropertyType type = PropertyType.valueOf(resultSet.getString("type"));
                int ownerId = resultSet.getInt("owner_id");

                // Create a new Property object with the result data
                property = new Property(propertyId, price, address, status, type, ownerId);
            }
        } catch (SQLException e) {
            // Print an error message if there's an issue fetching the property
            System.err.println("Error fetching property: " + e.getMessage());
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

        // Return the retrieved Property object
        return property;
    }

    // Method to get all Properties from the database
    public List<Property> getAllProperties() {
        List<Property> properties = new ArrayList<Property>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to select all properties
            String sql = "SELECT * FROM property";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            // Iterate through the results and add each Property to the list
            while (resultSet.next()) {
                int propertyId = resultSet.getInt("property_id");
                double price = resultSet.getDouble("price");
                String address = resultSet.getString("address");
                PropertyStatus status = PropertyStatus.valueOf(resultSet.getString("status"));
                PropertyType type = PropertyType.valueOf(resultSet.getString("type"));
                int ownerId = resultSet.getInt("owner_id");

                // Create a new Property object and add it to the list
                Property property = new Property(propertyId, price, address, status, type, ownerId);
                properties.add(property);
            }
        } catch (SQLException e) {
            // Print an error message if there's an issue fetching the properties
            System.err.println("Error fetching properties: " + e.getMessage());
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

        // Return the list of all Property objects
        return properties;
    }

    // Method to get Properties associated with a particular host_id
    public List<Property> getPropertiesByHostId(int id) {
        List<Property> properties = new ArrayList<Property>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // SQL query to select properties by host_id by joining property and
            // host_property tables
            String sql = "SELECT p.*, hp.* FROM property p JOIN host_property hp ON p.property_id = hp.property_id WHERE hp.host_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id); // Set the host_id parameter
            resultSet = statement.executeQuery();

            // Iterate through the results and add each Property to the list
            while (resultSet.next()) {
                int propertyId = resultSet.getInt("property_id");
                double price = resultSet.getDouble("price");
                String address = resultSet.getString("address");
                PropertyStatus status = PropertyStatus.valueOf(resultSet.getString("status"));
                PropertyType type = PropertyType.valueOf(resultSet.getString("type"));
                int ownerId = resultSet.getInt("owner_id");

                // Create a new Property object and add it to the list
                Property property = new Property(propertyId, price, address, status, type, ownerId);
                properties.add(property);
            }
        } catch (SQLException e) {
            // Print an error message if there's an issue fetching the properties by host
            System.err.println("Error fetching properties: " + e.getMessage());
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

        // Return the list of properties associated with the given host_id
        return properties;
    }

    // Fetches a list of properties based on the owner's ID
    public List<Property> getPropertiesByOwnerId(int id) {
        List<Property> properties = new ArrayList<Property>(); // List to hold property objects
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            String sql = "SELECT * FROM property WHERE owner_id = ?"; // SQL query to select properties by owner
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id); // Set the owner ID parameter in the query
            resultSet = statement.executeQuery(); // Execute the query

            // Loop through the result set and create Property objects
            while (resultSet.next()) {
                int propertyId = resultSet.getInt("property_id");
                double price = resultSet.getDouble("price");
                String address = resultSet.getString("address");
                PropertyStatus status = PropertyStatus.valueOf(resultSet.getString("status"));
                PropertyType type = PropertyType.valueOf(resultSet.getString("type"));
                int ownerId = resultSet.getInt("owner_id");

                Property property = new Property(propertyId, price, address, status, type, ownerId);
                properties.add(property); // Add the property object to the list
            }
        } catch (SQLException e) {
            System.err.println("Error fetching properties: " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close(); // Close the result set
                }
                if (statement != null) {
                    statement.close(); // Close the statement
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return properties; // Return the list of properties
    }

    // Fetches a list of properties based on their status
    public List<Property> getPropertiesByStatus(PropertyStatus status) {
        List<Property> properties = new ArrayList<Property>(); // List to hold property objects
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            String sql = "SELECT * FROM property WHERE status = ?"; // SQL query to select properties by status
            statement = connection.prepareStatement(sql);
            statement.setString(1, status.toString()); // Set the status parameter in the query
            resultSet = statement.executeQuery(); // Execute the query

            // Loop through the result set and create Property objects
            while (resultSet.next()) {
                int propertyId = resultSet.getInt("property_id");
                double price = resultSet.getDouble("price");
                String address = resultSet.getString("address");
                PropertyStatus settedstatus = PropertyStatus.valueOf(resultSet.getString("status"));
                PropertyType type = PropertyType.valueOf(resultSet.getString("type"));
                int ownerId = resultSet.getInt("owner_id");

                Property property = new Property(propertyId, price, address, settedstatus, type, ownerId);
                properties.add(property); // Add the property object to the list
            }
        } catch (SQLException e) {
            System.err.println("Error fetching properties: " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close(); // Close the result set
                }
                if (statement != null) {
                    statement.close(); // Close the statement
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return properties; // Return the list of properties
    }

    // Creates a new property and returns the created Property object
    public Property createProperty(double price, String address, PropertyStatus status, PropertyType type,
            int ownerId) {
        Property property = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection.setAutoCommit(false); // Start transaction

            // SQL query to insert a new property
            String sql = "INSERT INTO property (price, address, status, type, owner_id) VALUES (?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setDouble(1, price);
            statement.setString(2, address);
            statement.setString(3, status.name());
            statement.setString(4, type.name());
            statement.setInt(5, ownerId);

            int rows = statement.executeUpdate(); // Execute the insert query
            if (rows > 0) {
                resultSet = statement.getGeneratedKeys(); // Get the generated key (property ID)
                if (resultSet.next()) {
                    int id = resultSet.getInt(1); // Get the property ID from the generated keys
                    property = getPropertyById(id); // Retrieve the created property by its ID
                }
            }

            connection.commit(); // Commit the transaction
        } catch (SQLException e) {
            System.err.println("Error creating property: " + e.getMessage());

            try {
                connection.rollback(); // Rollback the transaction in case of an error
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close(); // Close the result set
                }
                if (statement != null) {
                    statement.close(); // Close the statement
                }

                connection.setAutoCommit(true); // Restore the auto-commit mode
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return property; // Return the created property
    }

    // Method to update a Property in the database by its property_id
    public Property updateProperty(int propertyId, double price, String address, PropertyStatus status,
            PropertyType type, int ownerId) {
        Property property = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Disable auto-commit for transaction management
            connection.setAutoCommit(false);

            // SQL query to update a property by its property_id
            String sql = "UPDATE property SET price = ?, address = ?, status = ?, type = ?, owner_id = ? WHERE property_id = ?";
            statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Set the parameters for the SQL query
            statement.setDouble(1, price);
            statement.setString(2, address);
            statement.setString(3, status.name());
            statement.setString(4, type.name());
            statement.setInt(5, ownerId);
            statement.setInt(6, propertyId);

            // Execute the update query
            int rows = statement.executeUpdate();

            // If the update is successful, fetch the updated property
            if (rows > 0) {
                resultSet = statement.getGeneratedKeys();
                if (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    property = getPropertyById(id); // Retrieve the updated property
                }
            }

            // Commit the transaction if the update is successful
            connection.commit();
        } catch (SQLException e) {
            // Print error message if an exception occurs while updating the property
            System.err.println("Error updating property: " + e.getMessage());

            // Rollback the transaction in case of an error
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            // Close the ResultSet, Statement, and reset auto-commit to true
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }

                connection.setAutoCommit(true); // Re-enable auto-commit
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Return the updated property
        return property;
    }

    // Method to delete a Property from the database by its property_id
    public void deletePropertyById(int id) {
        PreparedStatement statement = null;

        try {
            // Disable auto-commit for transaction management
            connection.setAutoCommit(false);

            // SQL query to delete a property by its property_id
            String sql = "DELETE FROM property WHERE property_id = ?";
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id); // Set the property_id parameter

            // Execute the delete query
            int rows = statement.executeUpdate();
            if (rows > 0) {
                // Print a message if the deletion is successful
                System.out.println("Property deleted successfully");
            }

            // Commit the transaction if the deletion is successful
            connection.commit();
        } catch (SQLException e) {
            // Print error message if an exception occurs while deleting the property
            System.err.println("Error deleting property: " + e.getMessage());

            // Rollback the transaction in case of an error
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            // Close the Statement and reset auto-commit to true
            try {
                if (statement != null) {
                    statement.close();
                }

                connection.setAutoCommit(true); // Re-enable auto-commit
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to get the total number of properties for a given host
    public int getTotalProperties(Integer hostId) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int countProperty = 0;

        try {
            // SQL query to count properties associated with the host (handling null hostId)
            String sql = """
                       SELECT COUNT(property_id) AS property_count
                         FROM property p
                         WHERE (
                             ? IS NULL OR property_id IN (
                                 SELECT property_id
                                 FROM host_property
                                 WHERE host_id = ?
                             )
                         )
                    """;
            statement = connection.prepareStatement(sql);

            // Set the parameter for hostId if provided
            if (hostId != null) {
                statement.setInt(1, hostId); // ? IS NULL condition
                statement.setInt(2, hostId); // hostId filter condition
            } else {
                statement.setNull(1, java.sql.Types.INTEGER); // Handle when hostId is null
                statement.setNull(2, java.sql.Types.INTEGER); // Dummy value, not used in this case
            }

            resultSet = statement.executeQuery(); // Execute the query

            // Retrieve the result from the query
            if (resultSet.next()) {
                countProperty = resultSet.getInt("property_count"); // Get the property count
            }
        } catch (SQLException e) {
            System.err.println("Error fetching properties: " + e.getMessage());
        } finally {
            // Close the ResultSet explicitly
            try {
                if (resultSet != null)
                    resultSet.close();
            } catch (SQLException e) {
                System.err.println("Error closing ResultSet: " + e.getMessage());
            }
        }

        return countProperty; // Return the count of properties
    }

    // Method to get the top 5 most rented properties for a given host
    public Map<String, Integer> getTop5MostRentedProperties(Integer hostId) {
        Map<String, Integer> mostRentedProperties = new LinkedHashMap<>(); // Maintain insertion order

        String sql = """
                    SELECT
                        p.property_id AS property_id,
                        COUNT(ra.property_id) AS rental_count
                    FROM property p
                    JOIN rental_agreement ra ON p.property_id = ra.property_id
                    JOIN host_rental_agreement hra ON ra.ra_id = hra.ra_id
                    WHERE
                        ra.property_id IS NOT NULL
                        AND (hra.host_id = COALESCE(?, hra.host_id) OR ? IS NULL)
                    GROUP BY p.property_id
                    ORDER BY rental_count DESC
                    LIMIT 5;
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            // Set the hostId parameter in the preparedStatement
            if (hostId != null) {
                preparedStatement.setInt(1, hostId); // Host ID parameter
                preparedStatement.setInt(2, hostId);
            } else {
                preparedStatement.setNull(1, java.sql.Types.INTEGER); // Null for all hosts
                preparedStatement.setNull(2, java.sql.Types.INTEGER);
            }

            // Execute the query and process the result
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String propertyId = resultSet.getString("property_id"); // Get property ID
                    int rentalCount = resultSet.getInt("rental_count"); // Get rental count

                    // Format the property ID for the map (for display purposes)
                    String formattedPropertyId = "Property ID: " + propertyId;

                    // Add the formatted property ID and rental count to the map
                    mostRentedProperties.put(formattedPropertyId, rentalCount);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching top rented properties: " + e.getMessage());
        }

        return mostRentedProperties; // Return the map of most rented properties
    }

    // Method to get rented property types count, filtered by hostId if provided
    public Map<String, Integer> getRentedPropertyTypesCount(Integer hostId) {
        Map<String, Integer> rentedPropertiesByType = new LinkedHashMap<>();

        // SQL query to get property types and rented counts, filtered by hostId if
        // provided
        String sql = """
                SELECT p.type, COUNT(DISTINCT ra.property_id) AS rented_count
                FROM rental_agreement ra
                JOIN property p ON ra.property_id = p.property_id
                JOIN host_rental_agreement hra ON ra.ra_id = hra.ra_id
                WHERE ra.status IN ('ACTIVE', 'COMPLETED')
                    AND hra.host_id = COALESCE(?, hra.host_id)
                GROUP BY p.type
                ORDER BY rented_count DESC;
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if (hostId != null) {
                preparedStatement.setInt(1, hostId); // Set hostId if provided
            } else {
                preparedStatement.setNull(1, java.sql.Types.INTEGER); // Null for all hosts
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    rentedPropertiesByType.put(resultSet.getString("type"), resultSet.getInt("rented_count"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching rented properties by type: " + e.getMessage());
        }

        return rentedPropertiesByType;
    }
}
