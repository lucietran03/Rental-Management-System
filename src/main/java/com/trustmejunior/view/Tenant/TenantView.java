package com.trustmejunior.view.Tenant;

import javafx.fxml.FXML;

/**
 * @author TrustMeJunior
 */

import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import com.trustmejunior.controller.PaymentController;
import com.trustmejunior.controller.RentalAgreementController;
import com.trustmejunior.model.Enum.PaymentStatus;
import com.trustmejunior.model.RentalEntity.Payment;
import com.trustmejunior.model.RentalEntity.RentalAgreement;
import com.trustmejunior.model.User.Account;
import com.trustmejunior.utils.SessionManager;

public class TenantView implements Initializable {
    @FXML
    private ImageView ImageViewTenantLogo;

    @FXML
    private Label homeTotalPayment;

    @FXML
    private Label homeTotalRA;

    @FXML
    private Label nameField;

    @FXML
    private Label homeTotalUnpaidPayment;

    private Account account = SessionManager.getCurrentAccount();
    private RentalAgreementController rentalAgreementController = new RentalAgreementController();
    private PaymentController paymentController = new PaymentController();

    public int loadTotalRA() {
        Set<RentalAgreement> rentalAgreements = new HashSet<>();

        // Add agreements for both main and sub tenants
        rentalAgreements.addAll(rentalAgreementController.getRentalAgreementsByMainTenantId(account.getAccountId()));
        rentalAgreements.addAll(rentalAgreementController.getRentalAgreementsBySubTenantId(account.getAccountId()));

        return rentalAgreements.size(); // Return the total number of rental agreements
    }

    public double loadTotalUnpaidPayment() {
        // Calculate the total of unpaid payments (status = PENDING)
        return paymentController.getPaymentsByTenantId(account.getAccountId()).stream()
                .filter(p -> p.getStatus() == PaymentStatus.PENDING)
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    public double loadTotalPayment() {
        // Calculate the total amount of all payments made
        return paymentController.getPaymentsByTenantId(account.getAccountId()).stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    // Format number with commas for display
    public static String formatNumberWithComma(double number) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.US);
        return formatter.format(number); // Return formatted number as string
    }

    public void initData() {
        // Initialize data on the UI
        homeTotalRA.setText(String.valueOf(loadTotalRA())); // Set total rental agreements
        homeTotalPayment.setText("$" + formatNumberWithComma(loadTotalPayment())); // Set total payment amount

        // Display unpaid payments, or a message if none
        if (loadTotalUnpaidPayment() != 0) {
            homeTotalUnpaidPayment.setText("Unpaid: $" + formatNumberWithComma(loadTotalUnpaidPayment()));
        } else {
            homeTotalUnpaidPayment.setText("None unpaid bill!"); // No unpaid bills
        }

        nameField.setText(account.getFullName()); // Set user's full name
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initData(); // Initialize the data when the view is loaded
    }
}
