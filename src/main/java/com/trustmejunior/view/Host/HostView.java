package com.trustmejunior.view.Host;

/**
 * @author TrustMeJunior
 */

import com.trustmejunior.controller.PropertyController;
import com.trustmejunior.controller.RentalAgreementController;
import com.trustmejunior.controller.TenantController;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.utils.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class HostView implements Initializable {
    @FXML
    private Label homeTotalProperties;

    @FXML
    private Label homeTotalRA;

    @FXML
    private Label homeTotalRevenue;

    @FXML
    private AreaChart<String, Double> monthlyRevenueTrend;

    @FXML
    private ComboBox<String> selectYear;

    @FXML
    private ComboBox<String> selectYear1;

    @FXML
    private AreaChart<String, Double> yearlyRevenueTrend;

    @FXML
    private BarChart<String, Number> mostRentProperties;

    @FXML
    private PieChart rentedPropertyByType;

    @FXML
    private BarChart<String, Double> totalPaymentByProperty;

    @FXML
    private BarChart<String, Number> overduePaymentByMonth;

    @FXML
    private PieChart vacantRentedProperty;

    @FXML
    private BarChart<String, Integer> peakTimeOfTheYear;

    private PropertyController propertyController = new PropertyController();
    private TenantController tenantController = new TenantController();
    private RentalAgreementController rentalAgreementController = new RentalAgreementController();

    private Account account = SessionManager.getCurrentAccount();

    private void updateMonthlyRevenueChart(String selectedYear) {
        // Clear any existing data from the chart
        monthlyRevenueTrend.getData().clear();

        // Retrieve monthly revenue data for the selected year
        Map<String, Map<String, Double>> data = rentalAgreementController.getMonthlyRevenueTrend(selectedYear,
                account.getAccountId());

        // Exit if no data is available
        if (data == null || data.isEmpty()) {
            return;
        }

        // Create series for total revenue and paid revenue
        XYChart.Series<String, Double> totalRevenueSeries = new XYChart.Series<>();
        XYChart.Series<String, Double> paidRevenueSeries = new XYChart.Series<>();

        // Loop through each month and add data to the series
        data.forEach((month, revenueData) -> {
            Double totalRevenue = revenueData.get("total_revenue");
            Double totalPaidRevenue = revenueData.get("total_revenue_paid");

            // Add data to total revenue series
            if (totalRevenue != null) {
                totalRevenueSeries.getData().add(new XYChart.Data<>(month, totalRevenue));
            }

            // Add data to paid revenue series
            if (totalPaidRevenue != null) {
                paidRevenueSeries.getData().add(new XYChart.Data<>(month, totalPaidRevenue));
            }
        });

        // Set names for the revenue series
        totalRevenueSeries.setName("Potential Revenue");
        paidRevenueSeries.setName("Paid Revenue");

        // Add series to the chart if data is available
        if (!totalRevenueSeries.getData().isEmpty()) {
            monthlyRevenueTrend.getData().add(totalRevenueSeries);
        }

        if (!paidRevenueSeries.getData().isEmpty()) {
            monthlyRevenueTrend.getData().add(paidRevenueSeries);
        }
    }

    private void populateYearSelector() {
        // Retrieve available years from the controller
        List<String> years = rentalAgreementController.getAvailableYears();

        // Initialize as empty list if years are null or empty
        if (years == null || years.isEmpty()) {
            years = new ArrayList<>();
        }

        // Add "All" option at the start if it's not already included
        if (!years.contains("All")) {
            years.add(0, "All");
        }

        // Clear existing ComboBox items and populate with updated years list
        selectYear.getItems().clear();
        selectYear.getItems().addAll(years);

        // Set default value as "All"
        selectYear.setValue("All");

        // Update chart immediately with the default value
        updateMonthlyRevenueChart("All");

        // Add event listener for ComboBox selection
        selectYear.setOnAction(event -> {
            String selectedYear = selectYear.getValue(); // Get selected year
            updateMonthlyRevenueChart(selectedYear); // Update chart based on selection
        });
    }

    private void updateYearlyRevenueChart() {
        // Clear any existing data from the chart
        yearlyRevenueTrend.getData().clear();

        // Retrieve yearly revenue data
        Map<String, Map<String, Double>> data = rentalAgreementController.getYearlyRevenueTrend(account.getAccountId());

        // Exit if no data is available
        if (data == null || data.isEmpty()) {
            return;
        }

        // Create series for total revenue and paid revenue
        XYChart.Series<String, Double> totalRevenueSeries = new XYChart.Series<>();
        XYChart.Series<String, Double> paidRevenueSeries = new XYChart.Series<>();

        // Loop through each year and add data to the series
        data.forEach((year, revenueData) -> {
            Double totalRevenue = revenueData.get("total_revenue");
            Double totalPaidRevenue = revenueData.get("total_revenue_paid");

            // Add data to total revenue series
            if (totalRevenue != null) {
                totalRevenueSeries.getData().add(new XYChart.Data<>(year, totalRevenue));
            }

            // Add data to paid revenue series
            if (totalPaidRevenue != null) {
                paidRevenueSeries.getData().add(new XYChart.Data<>(year, totalPaidRevenue));
            }
        });

        // Set names for the revenue series
        totalRevenueSeries.setName("Total Revenue");
        paidRevenueSeries.setName("Paid Revenue");

        // Add series to the chart if data is available
        if (!totalRevenueSeries.getData().isEmpty()) {
            yearlyRevenueTrend.getData().add(totalRevenueSeries);
        }

        if (!paidRevenueSeries.getData().isEmpty()) {
            yearlyRevenueTrend.getData().add(paidRevenueSeries);
        }
    }

    private void setSelectYear1() {
        // Retrieve the list of years from rental agreements
        List<String> years = rentalAgreementController.getYears();

        // If no years are available, initialize an empty list
        if (years == null || years.isEmpty()) {
            years = new ArrayList<>();
        }

        // Add "All" option to the list if not present
        if (!years.contains("All")) {
            years.add(0, "All");
        }

        // Set the available years in the ComboBox and set default value to "All"
        selectYear1.getItems().clear();
        selectYear1.getItems().addAll(years);
        selectYear1.setValue("All");

        // Load the chart immediately for the default selection ("All")
        loadPeakTimeChart("All");

        // Event listener to update chart when a year is selected from the ComboBox
        selectYear1.setOnAction(event -> {
            String selectedYear = selectYear1.getValue();
            loadPeakTimeChart(selectedYear);
        });
    }

    private void loadPeakTimeChart(String selectedYear) {
        // Clear existing chart data
        peakTimeOfTheYear.getData().clear();

        // Retrieve rental counts based on the selected year
        Map<Integer, Integer> rentalCountsByMonth;
        if (selectedYear.equals("All")) {
            rentalCountsByMonth = rentalAgreementController.getRentalCountsForAllYears(account.getAccountId());
        } else {
            int year = Integer.parseInt(selectedYear); // Convert selected year to integer
            rentalCountsByMonth = rentalAgreementController.getRentalCountsByMonth(year, account.getAccountId());
        }

        // If no data is available, exit the method
        if (rentalCountsByMonth == null || rentalCountsByMonth.isEmpty()) {
            System.out.println("No rental count data available for year: " + selectedYear);
            return;
        }

        // Create a new series for the chart
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        series.setName("Peak Times " + (selectedYear.equals("All") ? "(All)" : selectedYear));

        // Populate the series with monthly rental counts
        for (int month = 1; month <= 12; month++) {
            int rentalCount = rentalCountsByMonth.getOrDefault(month, 0);
            String monthName = getMonthName(month); // Convert month number to name
            series.getData().add(new XYChart.Data<>(monthName, rentalCount));
        }

        // Add the series to the chart and set the title
        peakTimeOfTheYear.getData().add(series);
        peakTimeOfTheYear
                .setTitle("Peak Rental Times" + (selectedYear.equals("All") ? " (All Years)" : " for " + selectedYear));
    }

    // Helper method to convert month number to name
    private String getMonthName(int month) {
        String[] months = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        return months[month - 1]; // Adjust for 1-based index
    }

    public void loadTop5MostRentedProperties() {
        // Retrieve data for the top 5 most rented properties
        Map<String, Integer> mostRentedProperties = propertyController
                .getTop5MostRentedProperties(account.getAccountId());

        // Create a new series for the bar chart
        ObservableList<XYChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Top 5 Most Rented Properties");

        // Add data to the series
        for (Map.Entry<String, Integer> entry : mostRentedProperties.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        // Set the chart data
        chartData.add(series);
        mostRentProperties.setData(chartData);
    }

    public void loadTop5PropertiesByRevenue() {
        // Retrieve data for the top 5 properties by revenue
        Map<String, Double> PaymentByProperty = rentalAgreementController
                .getTop5PropertiesByRevenue(account.getAccountId());

        // Clear existing chart data to avoid duplication
        totalPaymentByProperty.getData().clear();

        // Create a new series for the chart
        XYChart.Series<String, Double> series = new XYChart.Series<>();
        series.setName("Total Revenue Received by Property");

        // Add data to the series
        for (Map.Entry<String, Double> entry : PaymentByProperty.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        // Add the series to the chart
        totalPaymentByProperty.getData().add(series);
    }

    public void loadRentedPropertyTypesChart() {
        // Get counts of rented property types
        Map<String, Integer> rentedPropertyCounts = propertyController
                .getRentedPropertyTypesCount(account.getAccountId());

        // Create PieChart.Data items from the map
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : rentedPropertyCounts.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()));
        }

        // Set the data to the PieChart
        rentedPropertyByType.setData(pieChartData);
    }

    public void loadTop10OverduePaymentsByRA() {
        // Get overdue payments data
        Map<Integer, Integer> overduePayments = rentalAgreementController
                .getTop10OverduePaymentsByRA(account.getAccountId());

        // Create an ObservableList to hold the data
        ObservableList<XYChart.Data<String, Number>> chartData = FXCollections.observableArrayList();

        // Populate the chart data with RA ID and overdue count
        for (Map.Entry<Integer, Integer> entry : overduePayments.entrySet()) {
            String formattedRaId = "RA ID: " + entry.getKey(); // Format RA ID
            int count = entry.getValue(); // Overdue payment count

            // Add data point to chart
            chartData.add(new XYChart.Data<>(formattedRaId, count));
        }

        // Create a BarChart with category and number axes
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Rental Agreement ID");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Overdue Payment Count");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Overdue Payment Count by RA ID");

        // Add the data series to the chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Overdue Payments");
        series.getData().addAll(chartData);
        chart.getData().add(series);

        // Display the chart
        overduePaymentByMonth.getData().clear();
        overduePaymentByMonth.getData().add(series);
    }

    public void populateVacantRentedPieChart() {
        // Get vacant vs rented property counts
        Map<String, Integer> vacantRentedPropertyCounts = rentalAgreementController
                .getVacantVsRentedProperties(account.getAccountId());

        // Create PieChart.Data items from the map
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : vacantRentedPropertyCounts.entrySet()) {
            // Add data to the pie chart
            PieChart.Data data = new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue());
            pieChartData.add(data);
        }

        // Set the data to the PieChart
        vacantRentedProperty.setData(pieChartData);
    }

    private String formatNumber(double number) {
        // Format number for display (in millions or thousands)
        if (number >= 1000000) {
            return new DecimalFormat("#,##0.0M").format(number / 1000000);
        } else if (number >= 1000) {
            return new DecimalFormat("#,##0.0K").format(number / 1000);
        } else {
            return new DecimalFormat("#,##0").format(number);
        }
    }

    public void initData() {
        // Retrieve and display total properties, rental agreements, and revenue
        int totalProperties = propertyController.getTotalProperties(account.getAccountId());
        homeTotalProperties.setText(String.valueOf(totalProperties));

        int totalRA = rentalAgreementController.getTotalRentalAgreements(account.getAccountId());
        homeTotalRA.setText(String.valueOf(totalRA));

        double totalRevenue = rentalAgreementController.getTotalRevenue(account.getAccountId());
        homeTotalRevenue.setText("$" + formatNumber(totalRevenue));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize data and charts
        initData();
        populateYearSelector();
        setSelectYear1();

        // Event listener to refresh chart when year changes
        selectYear.setOnAction(event -> {
            String selectedYear = selectYear.getValue();
            updateMonthlyRevenueChart(selectedYear);
        });

        // Load charts for various metrics
        updateYearlyRevenueChart();
        loadTop5MostRentedProperties();
        loadRentedPropertyTypesChart();
        loadTop5PropertiesByRevenue();
        loadTop10OverduePaymentsByRA();
        populateVacantRentedPieChart();
    }
}
