package com.trustmejunior.repository;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.ConnectionManager;
import com.trustmejunior.model.Enum.PropertyStatus;
import com.trustmejunior.model.Property.ResidentialProperty;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResidentialPropertyRepository {
    private Connection connection;

    public ResidentialPropertyRepository() {
        connection = ConnectionManager.getConnection();
    }

    // Method to fetch a residential property by its ID
    public ResidentialProperty getResidentialPropertyById(int propertyId) {
        ResidentialProperty residentialProperty = null; // Initialize the property object to null
        PreparedStatement statement = null; // Statement for executing SQL query
        ResultSet resultSet = null; // Result set to hold query results

        try {
            // SQL query to join property and residential_property tables based on
            // property_id
            String sql = "SELECT p.*, rp.* FROM property p JOIN residential_property rp ON p.property_id = rp.rproperty_id WHERE p.property_id = ?";
            statement = connection.prepareStatement(sql); // Prepare the SQL statement
            statement.setInt(1, propertyId); // Set the property ID parameter
            resultSet = statement.executeQuery(); // Execute the query

            // If a result is found, populate the ResidentialProperty object
            if (resultSet.next()) {
                int rPropertyId = resultSet.getInt("rproperty_id");
                double price = resultSet.getDouble("price");
                String address = resultSet.getString("address");
                PropertyStatus status = PropertyStatus.valueOf(resultSet.getString("status"));
                int ownerId = resultSet.getInt("owner_id");
                int numberOfBedrooms = resultSet.getInt("number_of_bedrooms");
                boolean hasGarden = resultSet.getBoolean("has_garden");
                boolean isPetFriendly = resultSet.getBoolean("pet_friendly");

                // Create a new ResidentialProperty object with the fetched data
                residentialProperty = new ResidentialProperty(rPropertyId, price, address, status, ownerId,
                        numberOfBedrooms, hasGarden, isPetFriendly);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching residential property: " + e.getMessage()); // Print error message if an
                                                                                          // exception occurs
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

        return residentialProperty; // Return the ResidentialProperty object (or null if not found)
    }

    // Method to fetch all residential properties
    public List<ResidentialProperty> getAllResidentialProperties() {
        List<ResidentialProperty> residentialProperties = new ArrayList<ResidentialProperty>(); // Initialize a list to
                                                                                                // store all properties
        PreparedStatement statement = null; // Statement for executing SQL query
        ResultSet resultSet = null; // Result set to hold query results

        try {
            // SQL query to join property and residential_property tables
            String sql = "SELECT p.*, rp.* FROM property p JOIN residential_property rp ON p.property_id = rp.rproperty_id";
            statement = connection.prepareStatement(sql); // Prepare the SQL statement
            resultSet = statement.executeQuery(); // Execute the query

            // Iterate over the result set to populate the list of residential properties
            while (resultSet.next()) {
                int rPropertyId = resultSet.getInt("rproperty_id");
                double price = resultSet.getDouble("price");
                String address = resultSet.getString("address");
                PropertyStatus status = PropertyStatus.valueOf(resultSet.getString("status"));
                int ownerId = resultSet.getInt("owner_id");
                int numberOfBedrooms = resultSet.getInt("number_of_bedrooms");
                boolean hasGarden = resultSet.getBoolean("has_garden");
                boolean isPetFriendly = resultSet.getBoolean("pet_friendly");

                // Create a new ResidentialProperty object and add it to the list
                ResidentialProperty residentialProperty = new ResidentialProperty(rPropertyId, price, address, status,
                        ownerId, numberOfBedrooms, hasGarden, isPetFriendly);
                residentialProperties.add(residentialProperty);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching residential properties: " + e.getMessage()); // Print error message if an
                                                                                            // exception occurs
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

        return residentialProperties; // Return the list of residential properties
    }

    // Method to fetch residential properties by host ID
    public List<ResidentialProperty> getResidentialPropertiesByHostId(int id) {
        List<ResidentialProperty> residentialProperties = new ArrayList<ResidentialProperty>(); // Initialize a list to
                                                                                                // store residential
                                                                                                // properties for the
                                                                                                // host
        PreparedStatement statement = null; // Statement for executing SQL query
        ResultSet resultSet = null; // Result set to hold query results

        try {
            // SQL query to join property, residential_property, and host_property tables
            String sql = "SELECT p.*, rp.* FROM property p JOIN residential_property rp ON p.property_id = rp.rproperty_id JOIN host_property hp ON p.property_id = hp.property_id WHERE hp.host_id = ?";
            statement = connection.prepareStatement(sql); // Prepare the SQL statement
            statement.setInt(1, id); // Set the host ID parameter
            resultSet = statement.executeQuery(); // Execute the query

            // Iterate over the result set to populate the list of residential properties
            // for the specified host
            while (resultSet.next()) {
                int rPropertyId = resultSet.getInt("rproperty_id");
                double price = resultSet.getDouble("price");
                String address = resultSet.getString("address");
                PropertyStatus status = PropertyStatus.valueOf(resultSet.getString("status"));
                int ownerId = resultSet.getInt("owner_id");
                int numberOfBedrooms = resultSet.getInt("number_of_bedrooms");
                boolean hasGarden = resultSet.getBoolean("has_garden");
                boolean isPetFriendly = resultSet.getBoolean("pet_friendly");

                // Create a new ResidentialProperty object and add it to the list
                ResidentialProperty residentialProperty = new ResidentialProperty(rPropertyId, price, address, status,
                        ownerId, numberOfBedrooms, hasGarden, isPetFriendly);
                residentialProperties.add(residentialProperty);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching residential properties: " + e.getMessage()); // Print error message if an
                                                                                            // exception occurs
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

        return residentialProperties; // Return the list of residential properties for the host
    }

    // Method to get a list of residential properties by the owner's ID
    public List<ResidentialProperty> getResidentialPropertiesByOwnerId(int id) {
        List<ResidentialProperty> residentialProperties = new ArrayList<ResidentialProperty>(); // List to store the
                                                                                                // fetched properties
        PreparedStatement statement = null; // Statement object for executing SQL query
        ResultSet resultSet = null; // ResultSet object to hold the query results

        try {
            // SQL query to join the property table with the residential_property table and
            // filter by owner_id
            String sql = "SELECT p.*, rp.* FROM property p JOIN residential_property rp ON p.property_id = rp.rproperty_id WHERE p.owner_id = ?";
            statement = connection.prepareStatement(sql); // Prepare the SQL statement
            statement.setInt(1, id); // Set the owner_id parameter in the query
            resultSet = statement.executeQuery(); // Execute the query and get the results

            // Loop through the result set and create ResidentialProperty objects for each
            // record
            while (resultSet.next()) {
                // Retrieve the data from the result set
                int rPropertyId = resultSet.getInt("rproperty_id");
                double price = resultSet.getDouble("price");
                String address = resultSet.getString("address");
                PropertyStatus status = PropertyStatus.valueOf(resultSet.getString("status"));
                int ownerId = resultSet.getInt("owner_id");
                int numberOfBedrooms = resultSet.getInt("number_of_bedrooms");
                boolean hasGarden = resultSet.getBoolean("has_garden");
                boolean isPetFriendly = resultSet.getBoolean("pet_friendly");

                // Create a new ResidentialProperty object and add it to the list
                ResidentialProperty residentialProperty = new ResidentialProperty(rPropertyId, price, address, status,
                        ownerId, numberOfBedrooms, hasGarden, isPetFriendly);
                residentialProperties.add(residentialProperty);
            }
        } catch (SQLException e) {
            // Handle SQL exceptions
            System.err.println("Error fetching residential properties: " + e.getMessage());
        } finally {
            // Close resources to avoid memory leaks
            try {
                if (resultSet != null) {
                    resultSet.close(); // Close the result set
                }
                if (statement != null) {
                    statement.close(); // Close the statement
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Handle exception during closing
            }
        }

        // Return the list of residential properties
        return residentialProperties;
    }

    // Method to create a new residential property
    public ResidentialProperty createResidentialProperty(int rPropertyId, int numberOfBedrooms, boolean hasGarden,
            boolean isPetFriendly) {
        ResidentialProperty residentialProperty = null; // Variable to hold the created residential property
        PreparedStatement statement = null; // Statement object for executing SQL query

        try {
            connection.setAutoCommit(false); // Disable auto-commit for transaction management

            // SQL query to insert a new residential property record
            String sql = "INSERT INTO residential_property (rproperty_id, number_of_bedrooms, has_garden, pet_friendly) VALUES (?, ?, ?, ?)";
            statement = connection.prepareStatement(sql); // Prepare the SQL statement
            statement.setInt(1, rPropertyId); // Set rproperty_id parameter
            statement.setInt(2, numberOfBedrooms); // Set number_of_bedrooms parameter
            statement.setBoolean(3, hasGarden); // Set has_garden parameter
            statement.setBoolean(4, isPetFriendly); // Set pet_friendly parameter

            int rows = statement.executeUpdate(); // Execute the insert query
            if (rows > 0) {
                // If the insert is successful, retrieve the created residential property by its
                // ID
                residentialProperty = getResidentialPropertyById(rPropertyId);
            }

            connection.commit(); // Commit the transaction
        } catch (SQLException e) {
            // Handle SQL exceptions
            System.err.println("Error creating residential property: " + e.getMessage());

            try {
                connection.rollback(); // Rollback the transaction if an error occurs
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            // Close resources to avoid memory leaks
            try {
                if (statement != null) {
                    statement.close(); // Close the statement
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Handle exception during closing
            }
        }

        // Return the created residential property
        return residentialProperty;
    }

    // Method to update an existing residential property
    public ResidentialProperty updateResidentialProperty(int rPropertyId, int numberOfBedrooms, boolean hasGarden,
            boolean isPetFriendly) {
        ResidentialProperty residentialProperty = null; // Variable to store the updated residential property
        PreparedStatement statement = null; // PreparedStatement for executing SQL query

        try {
            connection.setAutoCommit(false); // Disable auto-commit for transaction management

            // SQL query to update the residential property record
            String sql = "UPDATE residential_property SET number_of_bedrooms = ?, has_garden = ?, pet_friendly = ? WHERE rproperty_id = ?";
            statement = connection.prepareStatement(sql); // Prepare the SQL statement
            statement.setInt(1, numberOfBedrooms); // Set number_of_bedrooms parameter
            statement.setBoolean(2, hasGarden); // Set has_garden parameter
            statement.setBoolean(3, isPetFriendly); // Set pet_friendly parameter
            statement.setInt(4, rPropertyId); // Set rproperty_id parameter for the record to be updated

            int rows = statement.executeUpdate(); // Execute the update query
            if (rows > 0) {
                // If the update is successful, retrieve the updated residential property by its
                // ID
                residentialProperty = getResidentialPropertyById(rPropertyId);
            }

            connection.commit(); // Commit the transaction
        } catch (SQLException e) {
            // Handle SQL exceptions
            System.err.println("Error updating residential property: " + e.getMessage());

            try {
                connection.rollback(); // Rollback the transaction if an error occurs
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            // Close resources to avoid memory leaks
            try {
                if (statement != null) {
                    statement.close(); // Close the statement
                }
            } catch (SQLException e) {
                e.printStackTrace(); // Handle exception during closing
            }
        }

        // Return the updated residential property
        return residentialProperty;
    }

    // Method to delete a residential property by its ID
    public void deleteResidentialPropertyById(int propertyId) {
        PreparedStatement statement = null; // PreparedStatement for executing SQL query

        try {
            connection.setAutoCommit(false); // Disable auto-commit for transaction management

            // SQL query to delete a residential property record by rproperty_id
            String sql = "DELETE FROM residential_property WHERE rproperty_id = ?";
            statement = connection.prepareStatement(sql); // Prepare the SQL statement
            statement.setInt(1, propertyId); // Set the rproperty_id parameter for the record to be deleted
            statement.executeUpdate(); // Execute the delete query

            connection.commit(); // Commit the transaction
        } catch (SQLException e) {
            // Handle SQL exceptions
            System.err.println("Error deleting residential property: " + e.getMessage());

            try {
                connection.rollback(); // Rollback the transaction if an error occurs
            } catch (SQLException ex) {
                System.err.println("Error rolling back transaction: " + ex.getMessage());
            }
        } finally {
            // Close resources and restore auto-commit state
            try {
                if (statement != null) {
                    statement.close(); // Close the statement
                }

                connection.setAutoCommit(true); // Restore auto-commit mode to true
            } catch (SQLException e) {
                e.printStackTrace(); // Handle exception during closing
            }
        }
    }
}
