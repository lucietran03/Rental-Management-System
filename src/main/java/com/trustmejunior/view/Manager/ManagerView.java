package com.trustmejunior.view.Manager;

import com.trustmejunior.controller.PropertyController;
import com.trustmejunior.controller.RentalAgreementController;
import com.trustmejunior.controller.TenantController;
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

public class ManagerView implements Initializable {

    @FXML
    private Label homeTotalProperties;

    @FXML
    private Label homeTotalRA;

    @FXML
    private Label homeTotalRevenue;

    @FXML
    private Label homeTotalTenants;

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

    private void updateMonthlyRevenueChart(String selectedYear) {
        // Clear existing chart data
        monthlyRevenueTrend.getData().clear();

        // Retrieve monthly revenue data for the selected year
        Map<String, Map<String, Double>> data = rentalAgreementController.getMonthlyRevenueTrend(selectedYear, null);

        // Exit if no data is available
        if (data == null || data.isEmpty()) {
            return;
        }

        // Create series for total and paid revenue
        XYChart.Series<String, Double> totalRevenueSeries = new XYChart.Series<>();
        XYChart.Series<String, Double> paidRevenueSeries = new XYChart.Series<>();

        // Add data to series for each month
        data.forEach((month, revenueData) -> {
            if (revenueData.get("total_revenue") != null) {
                totalRevenueSeries.getData().add(new XYChart.Data<>(month, revenueData.get("total_revenue")));
            }
            if (revenueData.get("total_revenue_paid") != null) {
                paidRevenueSeries.getData().add(new XYChart.Data<>(month, revenueData.get("total_revenue_paid")));
            }
        });

        // Set series names
        totalRevenueSeries.setName("Potential Revenue");
        paidRevenueSeries.setName("Paid Revenue");

        // Add series to chart if they contain data
        if (!totalRevenueSeries.getData().isEmpty()) {
            monthlyRevenueTrend.getData().add(totalRevenueSeries);
        }
        if (!paidRevenueSeries.getData().isEmpty()) {
            monthlyRevenueTrend.getData().add(paidRevenueSeries);
        }
    }

    private void populateYearSelector() {
        // Retrieve list of available years
        List<String> years = rentalAgreementController.getAvailableYears();
        if (years == null || years.isEmpty()) {
            years = new ArrayList<>();
        }

        // Ensure "All" option is included
        if (!years.contains("All")) {
            years.add(0, "All");
        }

        // Populate ComboBox with years
        selectYear.getItems().clear();
        selectYear.getItems().addAll(years);
        selectYear.setValue("All");

        // Update chart on selection
        updateMonthlyRevenueChart("All");

        // Event listener for year selection
        selectYear.setOnAction(event -> {
            String selectedYear = selectYear.getValue();
            updateMonthlyRevenueChart(selectedYear);
            System.out.println("Selected Year: " + selectedYear);
        });
    }

    private void updateYearlyRevenueChart() {
        // Clear existing chart data
        yearlyRevenueTrend.getData().clear();

        // Retrieve yearly revenue data
        Map<String, Map<String, Double>> data = rentalAgreementController.getYearlyRevenueTrend(null);

        // Exit if no data is available
        if (data == null || data.isEmpty()) {
            return;
        }

        // Create series for total and paid revenue
        XYChart.Series<String, Double> totalRevenueSeries = new XYChart.Series<>();
        XYChart.Series<String, Double> paidRevenueSeries = new XYChart.Series<>();

        // Add data to series for each year
        data.forEach((year, revenueData) -> {
            if (revenueData.get("total_revenue") != null) {
                totalRevenueSeries.getData().add(new XYChart.Data<>(year, revenueData.get("total_revenue")));
            }
            if (revenueData.get("total_revenue_paid") != null) {
                paidRevenueSeries.getData().add(new XYChart.Data<>(year, revenueData.get("total_revenue_paid")));
            }
        });

        // Set series names
        totalRevenueSeries.setName("Total Revenue");
        paidRevenueSeries.setName("Paid Revenue");

        // Add series to chart if they contain data
        if (!totalRevenueSeries.getData().isEmpty()) {
            yearlyRevenueTrend.getData().add(totalRevenueSeries);
        }
        if (!paidRevenueSeries.getData().isEmpty()) {
            yearlyRevenueTrend.getData().add(paidRevenueSeries);
        }
    }

    // Load and display the top 5 most rented properties in a bar chart
    public void loadTop5MostRentedProperties() {
        // Retrieve the data
        Map<String, Integer> mostRentedProperties = propertyController.getTop5MostRentedProperties(null);

        // Create an observable list to hold the bar chart data
        ObservableList<XYChart.Series<String, Number>> chartData = FXCollections.observableArrayList();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Top 10 Most Rented Properties");

        // Add data to the series
        for (Map.Entry<String, Integer> entry : mostRentedProperties.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        // Add the series to the chart's data
        chartData.add(series);

        // Set the chart data
        mostRentProperties.setData(chartData);
    }

    // Load and display rented property types in a pie chart
    public void loadRentedPropertyTypesChart() {
        // Get the rented property counts
        Map<String, Integer> rentedPropertyCounts = propertyController.getRentedPropertyTypesCount(null);

        // Create PieChart.Data items from the map
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : rentedPropertyCounts.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()));
        }

        // Set the data to the PieChart
        rentedPropertyByType.setData(pieChartData);
    }

    // Load and display the top 5 properties by revenue in a bar chart
    public void loadTop5PropertiesByRevenue() {
        // Retrieve the data
        Map<String, Double> PaymentByProperty = rentalAgreementController.getTop5PropertiesByRevenue(null);

        // Clear existing data from the chart to avoid duplication
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

    // Load and display the top 10 overdue payments by rental agreements in a bar
    // chart
    public void loadTop10OverduePaymentsByRA() {
        // Get the data from the method
        Map<Integer, Integer> overduePayments = rentalAgreementController.getTop10OverduePaymentsByRA(null);

        // Create an ObservableList to hold the data
        ObservableList<XYChart.Data<String, Number>> chartData = FXCollections.observableArrayList();

        // Populate the chart data with RA ID and overdue count
        for (Map.Entry<Integer, Integer> entry : overduePayments.entrySet()) {
            String formattedRaId = "RA ID: " + entry.getKey(); // Format RA ID as "RA ID: id"
            int count = entry.getValue(); // Overdue payment count

            // Add a data point for each RA ID and its overdue count
            chartData.add(new XYChart.Data<>(formattedRaId, count));
        }

        // Create a BarChart with a CategoryAxis (for RA IDs) and NumberAxis (for
        // overdue count)
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Rental Agreement ID");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Overdue Payment Count");

        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Overdue Payment Count by RA ID");

        // Add the chart data to the bar chart
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Overdue Payments");

        // Add all chart data points to the series
        series.getData().addAll(chartData);

        // Add the series to the chart
        chart.getData().add(series);

        // Display the chart
        overduePaymentByMonth.getData().clear();
        overduePaymentByMonth.getData().add(series);
    }

    // Populate the vacant vs rented properties pie chart
    public void populateVacantRentedPieChart() {
        // Get the vacant and rented property counts from the controller
        Map<String, Integer> vacantRentedPropertyCounts = rentalAgreementController.getVacantVsRentedProperties(null);

        // Create PieChart.Data items from the map
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : vacantRentedPropertyCounts.entrySet()) {
            // Set label as "Status (Count)"
            PieChart.Data data = new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue());
            pieChartData.add(data);
        }

        // Set the data to the PieChart
        vacantRentedProperty.setData(pieChartData);
    }

    // Set and display the available years in a ComboBox, update chart on selection
    private void setSelectYear1() {
        List<String> years = rentalAgreementController.getYears();

        if (years == null || years.isEmpty()) {
            years = new ArrayList<>();
        }

        if (!years.contains("All")) {
            years.add(0, "All");
        }

        selectYear1.getItems().clear();
        selectYear1.getItems().addAll(years);
        selectYear1.setValue("All"); // Default selection

        // Trigger chart update immediately after setting the default value
        loadPeakTimeChart("All");

        // Event listener to update chart on ComboBox selection
        selectYear1.setOnAction(event -> {
            String selectedYear = selectYear1.getValue();
            loadPeakTimeChart(selectedYear);
        });
    }

    private void loadPeakTimeChart(String selectedYear) {
        peakTimeOfTheYear.getData().clear(); // Clear previous data

        // Retrieve rental counts based on the selected year
        Map<Integer, Integer> rentalCountsByMonth;
        if (selectedYear.equals("All")) {
            rentalCountsByMonth = rentalAgreementController.getRentalCountsForAllYears(null);
        } else {
            int year = Integer.parseInt(selectedYear); // Convert selected year to integer
            rentalCountsByMonth = rentalAgreementController.getRentalCountsByMonth(year, null);
        }

        // Exit if no data available
        if (rentalCountsByMonth == null || rentalCountsByMonth.isEmpty()) {
            System.out.println("No rental count data available for year: " + selectedYear);
            return;
        }

        // Create and populate the series with rental count data
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        series.setName("Peak Times " + (selectedYear.equals("All") ? "(All)" : selectedYear));

        for (int month = 1; month <= 12; month++) {
            int rentalCount = rentalCountsByMonth.getOrDefault(month, 0);
            String monthName = getMonthName(month); // Get month name (e.g., "January")
            series.getData().add(new XYChart.Data<>(monthName, rentalCount));
        }

        // Add populated series to chart and set chart title
        peakTimeOfTheYear.getData().add(series);
        peakTimeOfTheYear
                .setTitle("Peak Rental Times" + (selectedYear.equals("All") ? " (All Years)" : " for " + selectedYear));
    }

    // Helper method to get month name
    private String getMonthName(int month) {
        String[] months = { "January", "February", "March", "April", "May", "June", "July", "August", "September",
                "October", "November", "December" };
        return months[month - 1]; // Adjust for 1-based index
    }

    private String formatNumber(double number) {
        if (number >= 1000000) {
            return new DecimalFormat("#,##0.0M").format(number / 1000000);
        } else if (number >= 1000) {
            return new DecimalFormat("#,##0.0K").format(number / 1000);
        } else {
            return new DecimalFormat("#,##0").format(number);
        }
    }

    public void initData() {
        int totalProperties = propertyController.getTotalProperties(null);
        homeTotalProperties.setText(String.valueOf(totalProperties));

        int totalTenants = tenantController.getTotalTenants();
        homeTotalTenants.setText(String.valueOf(totalTenants));

        int totalRA = rentalAgreementController.getTotalRentalAgreements(null);
        homeTotalRA.setText(String.valueOf(totalRA));

        double totalRevenue = rentalAgreementController.getTotalRevenue(null);
        homeTotalRevenue.setText("$" + formatNumber(totalRevenue));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initData();
        populateYearSelector();
        setSelectYear1();

        // Event listener to refresh chart when year changes
        selectYear.setOnAction(event -> {
            String selectedYear = selectYear.getValue();
            updateMonthlyRevenueChart(selectedYear);
        });

        updateYearlyRevenueChart();
        loadTop5MostRentedProperties();
        loadRentedPropertyTypesChart();
        loadTop5PropertiesByRevenue();
        loadTop10OverduePaymentsByRA();
        populateVacantRentedPieChart();
    }
}
