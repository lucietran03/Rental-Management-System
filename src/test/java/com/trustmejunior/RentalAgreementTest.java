package com.trustmejunior;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.RentalAgreementController;
import com.trustmejunior.model.Enum.RentalPeriod;
import com.trustmejunior.model.Enum.RentalStatus;
import com.trustmejunior.model.RentalEntity.RentalAgreement;
import com.trustmejunior.service.RentalAgreementService;
import com.trustmejunior.service.TenantService;
import com.trustmejunior.request.CreateRentalAgreementRequest;
import com.trustmejunior.request.UpdateRentalAgreementRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RentalAgreementTest {

        @Mock
        private RentalAgreementService rentalAgreementService;

        @Mock
        private TenantService tenantService;

        @InjectMocks
        private RentalAgreementController rentalAgreementController;

        private RentalAgreement testAgreement;

        @BeforeEach
        void setUp() {
                testAgreement = new RentalAgreement(1, 10000.00, Date.valueOf("2023-09-17"),
                        Date.valueOf("2022-03-17"), RentalPeriod.MONTHLY, RentalStatus.COMPLETED, 3, 1);
        }

        @Test
        void getRentalAgreementByIdTest() {
                // Arrange
                int agreementId = 1;
                when(rentalAgreementService.getRentalAgreementById(agreementId)).thenReturn(testAgreement);

                // Act
                RentalAgreement result = rentalAgreementController.getRentalAgreementById(agreementId);

                // Assert
                assertNotNull(result, "Retrieved rental agreement should not be null");
                assertEquals(testAgreement.getRentalAgreementId(), result.getRentalAgreementId(),
                        "Rental agreement ID should match");
                assertEquals(testAgreement.getPropertyId(), result.getPropertyId(),
                        "Property ID should match");
                verify(rentalAgreementService).getRentalAgreementById(agreementId);
        }

        @Test
        void getAllRentalAgreementsTest() {
                // Arrange
                RentalAgreement secondAgreement = new RentalAgreement(2, 15000.00, Date.valueOf("2024-05-01"),
                        Date.valueOf("2023-05-01"), RentalPeriod.MONTHLY, RentalStatus.COMPLETED, 3, 7);
                List<RentalAgreement> mockAgreements = Arrays.asList(testAgreement, secondAgreement);
                when(rentalAgreementService.getAllRentalAgreements()).thenReturn(mockAgreements);

                // Act
                List<RentalAgreement> results = rentalAgreementController.getAllRentalAgreements();

                // Assert
                assertNotNull(results, "List of rental agreements should not be null");
                assertEquals(2, results.size(), "Should return exactly 2 rental agreements");
                assertEquals(1, results.get(0).getRentalAgreementId(),
                        "First agreement should have ID 1");
                assertEquals(3, results.get(0).getOwnerId(),
                        "First agreement should have owner ID 3");
                verify(rentalAgreementService).getAllRentalAgreements();
        }

        @Test
        void getRentalAgreementsByHostIdTest() {
                // Arrange
                int hostId = 2;
                List<RentalAgreement> mockAgreements = Arrays.asList(testAgreement);
                when(rentalAgreementService.getRentalAgreementsByHostId(hostId)).thenReturn(mockAgreements);

                // Act
                List<RentalAgreement> results = rentalAgreementController.getRentalAgreementsByHostId(hostId);

                // Assert
                assertNotNull(results, "List of rental agreements for host should not be null");
                assertEquals(1, results.size(), "Should return exactly 1 rental agreement");
                assertEquals(testAgreement.getRentalAgreementId(), results.get(0).getRentalAgreementId(),
                        "Rental agreement ID should match test agreement");
                verify(rentalAgreementService).getRentalAgreementsByHostId(hostId);
        }

        @Test
        void getRentalAgreementsByOwnerIdTest() {
                // Arrange
                int ownerId = 3;
                List<RentalAgreement> mockAgreements = Arrays.asList(testAgreement);
                when(rentalAgreementService.getRentalAgreementsByOwnerId(ownerId)).thenReturn(mockAgreements);

                // Act
                List<RentalAgreement> results = rentalAgreementController.getRentalAgreementsByOwnerId(ownerId);

                // Assert
                assertNotNull(results, "List of rental agreements for owner should not be null");
                assertEquals(1, results.size(), "Should return exactly 1 rental agreement");
                assertEquals(testAgreement.getRentalAgreementId(), results.get(0).getRentalAgreementId(),
                        "Rental agreement ID should match test agreement");
                verify(rentalAgreementService).getRentalAgreementsByOwnerId(ownerId);
        }

        @Test
        void getRentalAgreementsByPropertyIdTest() {
                // Arrange
                int propertyId = 1;
                List<RentalAgreement> mockAgreements = Arrays.asList(testAgreement);
                when(rentalAgreementService.getRentalAgreementsByPropertyId(propertyId)).thenReturn(mockAgreements);

                // Act
                List<RentalAgreement> results = rentalAgreementController.getRentalAgreementsByPropertyId(propertyId);

                // Assert
                assertNotNull(results, "List of rental agreements for property should not be null");
                assertEquals(1, results.size(), "Should return exactly 1 rental agreement");
                assertEquals(testAgreement.getRentalAgreementId(), results.get(0).getRentalAgreementId(),
                        "Rental agreement ID should match test agreement");
                verify(rentalAgreementService).getRentalAgreementsByPropertyId(propertyId);
        }

        @Test
        void getRentalAgreementsBySubTenantTest() {
                // Arrange
                int subTenantId = 10;
                List<RentalAgreement> mockAgreements = Arrays.asList(testAgreement);

                when(rentalAgreementService.getRentalAgreementsBySubTenantId(subTenantId)).thenReturn(mockAgreements);

                // Act
                List<RentalAgreement> results = rentalAgreementController.getRentalAgreementsBySubTenantId(subTenantId);

                // Assert
                assertNotNull(results, "List of rental agreements for sub-tenant should not be null");
                assertEquals(1, results.size(), "Should return exactly 1 rental agreement");
                assertEquals(testAgreement.getRentalAgreementId(), results.get(0).getRentalAgreementId(),
                        "Rental agreement ID should match test agreement");
        }

        @Test
        void getRentalAgreementsByMainTenantTest() {
                // Arrange
                int mainTenantId = 2;
                List<RentalAgreement> mockAgreements = Arrays.asList(testAgreement);

                when(rentalAgreementService.getRentalAgreementsByMainTenantId(mainTenantId)).thenReturn(mockAgreements);

                // Act
                List<RentalAgreement> results = rentalAgreementController
                        .getRentalAgreementsByMainTenantId(mainTenantId);

                // Assert
                assertNotNull(results, "List of rental agreements for main tenant should not be null");
                assertEquals(1, results.size(), "Should return exactly 1 rental agreement");
                assertEquals(testAgreement.getRentalAgreementId(), results.get(0).getRentalAgreementId(),
                        "Rental agreement ID should match test agreement");
                verify(rentalAgreementService).getRentalAgreementsByMainTenantId(mainTenantId);
        }

        @Test
        void deleteRentalAgreementTest() {
                // Arrange
                int agreementId = 1;
                doNothing().when(rentalAgreementService).deleteRentalAgreementById(agreementId);
                when(rentalAgreementService.getRentalAgreementById(agreementId)).thenReturn(null);

                // Act
                rentalAgreementController.deleteRentalAgreementById(agreementId);
                RentalAgreement deletedAgreement = rentalAgreementController.getRentalAgreementById(agreementId);

                // Assert
                assertNull(deletedAgreement, "Rental agreement should be null after deletion");
                verify(rentalAgreementService).deleteRentalAgreementById(agreementId);
                verify(rentalAgreementService).getRentalAgreementById(agreementId);
        }

        @Test
        void createRentalAgreementTest() throws Exception {
                // Arrange
                CreateRentalAgreementRequest request = new CreateRentalAgreementRequest(
                                10000.00,
                                Date.valueOf("2023-09-17"),
                                Date.valueOf("2022-03-17"),
                                RentalPeriod.MONTHLY,
                                RentalStatus.COMPLETED,
                                3,
                                1);
                when(rentalAgreementService.createRentalAgreement(request)).thenReturn(testAgreement);

                // Act
                RentalAgreement result = rentalAgreementController.createRentalAgreement(request);

                // Assert
                assertNotNull(result, "Created rental agreement should not be null");
                assertEquals(testAgreement.getRentalAgreementId(), result.getRentalAgreementId(),
                                "Rental agreement ID should match");
                verify(rentalAgreementService).createRentalAgreement(request);
        }

        @Test
        void updateRentalAgreementTest() throws Exception {
                // Arrange
                int agreementId = 1;
                UpdateRentalAgreementRequest request = new UpdateRentalAgreementRequest(
                                15000.00,
                                Date.valueOf("2024-09-17"),
                                Date.valueOf("2023-09-17"),
                                RentalPeriod.YEARLY,
                                RentalStatus.ACTIVE,
                                4,
                                2);
                RentalAgreement updatedAgreement = new RentalAgreement(
                                agreementId,
                                request.getFee(),
                                request.getStartDate(),
                                request.getEndDate(),
                                request.getPeriod(),
                                request.getStatus(),
                                request.getOwnerId(),
                                request.getPropertyId());
                when(rentalAgreementService.updateRentalAgreement(agreementId, request)).thenReturn(updatedAgreement);

                // Act
                RentalAgreement result = rentalAgreementController.updateRentalAgreement(agreementId, request);

                // Assert
                assertNotNull(result, "Updated rental agreement should not be null");
                assertEquals(updatedAgreement.getFee(), result.getFee(), "Fee should be updated");
                assertEquals(updatedAgreement.getStatus(), result.getStatus(), "Status should be updated");
                verify(rentalAgreementService).updateRentalAgreement(agreementId, request);
        }

        @Test
        void setHostIdsTest() {
                // Arrange
                int agreementId = 1;
                List<Integer> hostIds = Arrays.asList(1, 2, 3);
                doNothing().when(rentalAgreementService).setHostIds(agreementId, hostIds);

                // Act & Assert
                assertDoesNotThrow(() -> rentalAgreementController.setHostIds(agreementId, hostIds),
                                "Setting host IDs should not throw an exception");
                verify(rentalAgreementService).setHostIds(agreementId, hostIds);
        }

        @Test
        void setMainTenantIdTest() {
                // Arrange
                int agreementId = 1;
                int tenantId = 5;
                doNothing().when(rentalAgreementService).setMainTenantId(agreementId, tenantId);

                // Act & Assert
                assertDoesNotThrow(() -> rentalAgreementController.setMainTenantId(agreementId, tenantId),
                                "Setting main tenant ID should not throw an exception");
                verify(rentalAgreementService).setMainTenantId(agreementId, tenantId);
        }

        @Test
        void setSubTenantIdsTest() {
                // Arrange
                int agreementId = 1;
                List<Integer> tenantIds = Arrays.asList(6, 7, 8);
                doNothing().when(rentalAgreementService).setSubTenantIds(agreementId, tenantIds);

                // Act & Assert
                assertDoesNotThrow(() -> rentalAgreementController.setSubTenantIds(agreementId, tenantIds),
                                "Setting sub tenant IDs should not throw an exception");
                verify(rentalAgreementService).setSubTenantIds(agreementId, tenantIds);
        }

        @Test
        void getTotalRentalAgreementsTest() {
                // Arrange
                Integer hostId = 1;
                when(rentalAgreementService.getTotalRentalAgreements(hostId)).thenReturn(5);

                // Act
                int result = rentalAgreementController.getTotalRentalAgreements(hostId);

                // Assert
                assertEquals(5, result, "Total number of rental agreements should be 5");
                verify(rentalAgreementService).getTotalRentalAgreements(hostId);
        }

        @Test
        void getTotalRevenueTest() {
                // Arrange
                Integer hostId = 1;
                double expectedRevenue = 50000.00;
                when(rentalAgreementService.getTotalRevenue(hostId)).thenReturn(expectedRevenue);

                // Act
                double result = rentalAgreementController.getTotalRevenue(hostId);

                // Assert
                assertEquals(expectedRevenue, result, 0.01, "Total revenue should match expected value");
                verify(rentalAgreementService).getTotalRevenue(hostId);
        }

        @Test
        void getMonthlyRevenueTrendTest() {
                // Arrange
                String selectedYear = "2023";
                Integer hostId = 1;
                Map<String, Map<String, Double>> expectedTrend = new HashMap<>();
                Map<String, Double> januaryData = new HashMap<>();
                januaryData.put("revenue", 5000.00);
                expectedTrend.put("January", januaryData);

                when(rentalAgreementService.getMonthlyRevenueTrend(selectedYear, hostId)).thenReturn(expectedTrend);

                // Act
                Map<String, Map<String, Double>> result = rentalAgreementController.getMonthlyRevenueTrend(selectedYear,
                                hostId);

                // Assert
                assertNotNull(result, "Monthly revenue trend should not be null");
                assertEquals(expectedTrend.size(), result.size(), "Should have same number of months");
                assertEquals(5000.00, result.get("January").get("revenue"), 0.01, "January revenue should match");
                verify(rentalAgreementService).getMonthlyRevenueTrend(selectedYear, hostId);
        }

        @Test
        void getAvailableYearsTest() {
                // Arrange
                List<String> expectedYears = Arrays.asList("2021", "2022", "2023");
                when(rentalAgreementService.getAvailableYears()).thenReturn(expectedYears);

                // Act
                List<String> result = rentalAgreementController.getAvailableYears();

                // Assert
                assertNotNull(result, "Available years list should not be null");
                assertEquals(expectedYears.size(), result.size(), "Should have same number of years");
                assertTrue(result.containsAll(expectedYears), "Should contain all expected years");
                verify(rentalAgreementService).getAvailableYears();
        }

        @Test
        void getYearlyRevenueTrendTest() {
                // Arrange
                Integer hostId = 1;
                Map<String, Map<String, Double>> expectedTrend = new HashMap<>();
                Map<String, Double> year2023Data = new HashMap<>();
                year2023Data.put("revenue", 60000.00);
                expectedTrend.put("2023", year2023Data);

                when(rentalAgreementService.getYearlyRevenueTrend(hostId)).thenReturn(expectedTrend);

                // Act
                Map<String, Map<String, Double>> result = rentalAgreementController.getYearlyRevenueTrend(hostId);

                // Assert
                assertNotNull(result, "Yearly revenue trend should not be null");
                assertEquals(expectedTrend.size(), result.size(), "Should have same number of years");
                assertEquals(60000.00, result.get("2023").get("revenue"), 0.01, "2023 revenue should match");
                verify(rentalAgreementService).getYearlyRevenueTrend(hostId);
        }

        @Test
        void getTop5PropertiesByRevenueTest() {
                // Arrange
                Integer hostId = 1;
                Map<String, Double> expectedTop5 = new HashMap<>();
                expectedTop5.put("Property A", 30000.00);
                expectedTop5.put("Property B", 25000.00);

                when(rentalAgreementService.getTop5PropertiesByRevenue(hostId)).thenReturn(expectedTop5);

                // Act
                Map<String, Double> result = rentalAgreementController.getTop5PropertiesByRevenue(hostId);

                // Assert
                assertNotNull(result, "Top 5 properties should not be null");
                assertEquals(expectedTop5.size(), result.size(), "Should have same number of properties");
                assertEquals(30000.00, result.get("Property A"), 0.01, "Property A revenue should match");
                verify(rentalAgreementService).getTop5PropertiesByRevenue(hostId);
        }

        @Test
        void getTop10OverduePaymentsByRATest() {
                // Arrange
                Integer hostId = 1;
                Map<Integer, Integer> expectedOverdue = new HashMap<>();
                expectedOverdue.put(1, 3); // RentalAgreement ID 1 has 3 overdue payments
                expectedOverdue.put(2, 2); // RentalAgreement ID 2 has 2 overdue payments

                when(rentalAgreementService.getTop10OverduePaymentsByRA(hostId)).thenReturn(expectedOverdue);

                // Act
                Map<Integer, Integer> result = rentalAgreementController.getTop10OverduePaymentsByRA(hostId);

                // Assert
                assertNotNull(result, "Top 10 overdue payments should not be null");
                assertEquals(expectedOverdue.size(), result.size(), "Should have same number of entries");
                assertEquals(3, result.get(1), "RentalAgreement 1 should have 3 overdue payments");
                verify(rentalAgreementService).getTop10OverduePaymentsByRA(hostId);
        }

        @Test
        void getVacantVsRentedPropertiesTest() {
                // Arrange
                Integer hostId = 1;
                Map<String, Integer> expectedStats = new HashMap<>();
                expectedStats.put("Vacant", 3);
                expectedStats.put("Rented", 7);

                when(rentalAgreementService.getVacantVsRentedProperties(hostId)).thenReturn(expectedStats);

                // Act
                Map<String, Integer> result = rentalAgreementController.getVacantVsRentedProperties(hostId);

                // Assert
                assertNotNull(result, "Vacant vs Rented stats should not be null");
                assertEquals(expectedStats.size(), result.size(), "Should have same number of categories");
                assertEquals(3, result.get("Vacant"), "Should have 3 vacant properties");
                assertEquals(7, result.get("Rented"), "Should have 7 rented properties");
                verify(rentalAgreementService).getVacantVsRentedProperties(hostId);
        }

        @Test
        void getYearsTest() {
                // Arrange
                List<String> expectedYears = Arrays.asList("2021", "2022", "2023");
                when(rentalAgreementService.getYears()).thenReturn(expectedYears);

                // Act
                List<String> result = rentalAgreementController.getYears();

                // Assert
                assertNotNull(result, "Years list should not be null");
                assertEquals(expectedYears.size(), result.size(), "Should have same number of years");
                assertTrue(result.containsAll(expectedYears), "Should contain all expected years");
                verify(rentalAgreementService).getYears();
        }

        @Test
        void getRentalCountsForAllYearsTest() {
                // Arrange
                Integer hostId = 1;
                Map<Integer, Integer> expectedCounts = new HashMap<>();
                expectedCounts.put(2021, 10);
                expectedCounts.put(2022, 15);
                expectedCounts.put(2023, 20);

                when(rentalAgreementService.getRentalCountsForAllYears(hostId)).thenReturn(expectedCounts);

                // Act
                Map<Integer, Integer> result = rentalAgreementController.getRentalCountsForAllYears(hostId);

                // Assert
                assertNotNull(result, "Rental counts should not be null");
                assertEquals(expectedCounts.size(), result.size(), "Should have same number of years");
                assertEquals(10, result.get(2021), "2021 should have 10 rentals");
                assertEquals(15, result.get(2022), "2022 should have 15 rentals");
                assertEquals(20, result.get(2023), "2023 should have 20 rentals");
                verify(rentalAgreementService).getRentalCountsForAllYears(hostId);
        }

        @Test
        void getRentalCountsByMonthTest() {
                // Arrange
                int year = 2023;
                Integer hostId = 1;
                Map<Integer, Integer> expectedCounts = new HashMap<>();
                expectedCounts.put(1, 5); // January has 5 rentals
                expectedCounts.put(2, 3); // February has 3 rentals

                when(rentalAgreementService.getRentalCountsByMonth(year, hostId)).thenReturn(expectedCounts);

                // Act
                Map<Integer, Integer> result = rentalAgreementController.getRentalCountsByMonth(year, hostId);

                // Assert
                assertNotNull(result, "Monthly rental counts should not be null");
                assertEquals(expectedCounts.size(), result.size(), "Should have same number of months");
                assertEquals(5, result.get(1), "January should have 5 rentals");
                assertEquals(3, result.get(2), "February should have 3 rentals");
                verify(rentalAgreementService).getRentalCountsByMonth(year, hostId);
        }
}
