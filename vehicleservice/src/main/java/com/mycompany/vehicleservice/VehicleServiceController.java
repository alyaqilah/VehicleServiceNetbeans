package com.mycompany.vehicleservice;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import java.io.File;

public class VehicleServiceController {

    @FXML private TextField txtCustomerName;
    @FXML private TextField txtVehicleRegNo;
    @FXML private ComboBox<String> cmbVehicleType;
    @FXML private ComboBox<String> cmbServiceType;
    @FXML private DatePicker datePickerService;
    
    @FXML private CheckBox chkEngineOil;
    @FXML private CheckBox chkBrakeInspection;
    @FXML private CheckBox chkAircond;
    @FXML private CheckBox chkCarWash;
    
    // Live Invoice Summary Labels
    @FXML private Label lblSummaryCustomer;
    @FXML private Label lblSummaryVehicle;
    @FXML private Label lblSummaryDate;
    @FXML private Label lblSummaryService;
    @FXML private Label lblSummaryAddons;
    @FXML private Label lblSummaryServiceCharge;
    @FXML private Label lblSummaryDiscount;
    @FXML private Label lblSummaryFinal;

    @FXML
    public void initialize() {
        if (cmbVehicleType != null && cmbVehicleType.getItems().isEmpty()) {
            cmbVehicleType.setItems(FXCollections.observableArrayList("Motorcycle", "Car", "MPV", "SUV"));
        }
        if (cmbServiceType != null && cmbServiceType.getItems().isEmpty()) {
            cmbServiceType.setItems(FXCollections.observableArrayList("Basic Service", "Standard Service", "Full Service"));
        }
    }

    @FXML
    public void handleCalculate() {
        try {
            // Validate empty fields using try-catch & Alert (Refer to Part B)[cite: 10]
            if (txtCustomerName.getText().trim().isEmpty() || 
                txtVehicleRegNo.getText().trim().isEmpty() || 
                cmbVehicleType.getValue() == null || 
                cmbServiceType.getValue() == null || 
                datePickerService.getValue() == null) {
                
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Empty Field Error");
                alert.setHeaderText(null);
                alert.setContentText("Please complete all customer details, vehicle type, package, and date.");
                alert.showAndWait();
                return;
            }

            String customer = txtCustomerName.getText().trim();
            String regNo = txtVehicleRegNo.getText().trim().toUpperCase();
            String vehicleType = cmbVehicleType.getValue();
            String serviceType = cmbServiceType.getValue();
            LocalDate serviceDate = datePickerService.getValue();

            // Price rates according to the official Part B schedule[cite: 10]
            double baseCost = 0.0;
            String st = serviceType.toLowerCase();
            if (st.contains("basic")) {
                baseCost = 80.0; // Basic Service = RM80[cite: 10]
            } else if (st.contains("standard")) {
                baseCost = 150.0; // Standard Service = RM150[cite: 10]
            } else if (st.contains("full")) {
                baseCost = 250.0; // Full Service = RM250[cite: 10]
            }

            // Additional services calculation (Add-ons)[cite: 10]
            StringBuilder addonsBuilder = new StringBuilder();
            double addonsCost = 0.0;
            if (chkEngineOil != null && chkEngineOil.isSelected()) {
                addonsBuilder.append("Engine Oil Change, ");
                addonsCost += 50.0; // RM50[cite: 10]
            }
            if (chkBrakeInspection != null && chkBrakeInspection.isSelected()) {
                addonsBuilder.append("Brake Inspection, ");
                addonsCost += 40.0; // RM40[cite: 10]
            }
            if (chkAircond != null && chkAircond.isSelected()) {
                addonsBuilder.append("Air-Conditioning Service, ");
                addonsCost += 80.0; // RM80[cite: 10]
            }
            if (chkCarWash != null && chkCarWash.isSelected()) {
                addonsBuilder.append("Car Wash, ");
                addonsCost += 20.0; // RM20[cite: 10]
            }
            
            String addonsStr = addonsBuilder.length() > 0 ? addonsBuilder.substring(0, addonsBuilder.length() - 2) : "None";

            // Service charge and discount policy according to Part B[cite: 10]
            double serviceCharge = 30.0; // RM30 service charge[cite: 10]
            double subtotal = baseCost + addonsCost + serviceCharge;

            double discount = 0.0;
            if (subtotal > 300.0) {
                discount = subtotal * 0.10; // 10% discount if exceeding RM300[cite: 10]
            }
            double finalAmount = subtotal - discount; // Final amount formula[cite: 10]

            // Update Live Invoice Summary display[cite: 10]
            if (lblSummaryCustomer != null) lblSummaryCustomer.setText(customer);
            if (lblSummaryVehicle != null) lblSummaryVehicle.setText(regNo);
            if (lblSummaryDate != null) lblSummaryDate.setText(serviceDate.toString());
            if (lblSummaryService != null) lblSummaryService.setText(String.format("RM %.2f", baseCost));
            if (lblSummaryAddons != null) lblSummaryAddons.setText(String.format("RM %.2f", addonsCost));
            if (lblSummaryServiceCharge != null) lblSummaryServiceCharge.setText(String.format("RM %.2f", serviceCharge));
            if (lblSummaryDiscount != null) lblSummaryDiscount.setText(String.format("RM %.2f", discount));
            if (lblSummaryFinal != null) lblSummaryFinal.setText(String.format("RM %.2f", finalAmount));

            // Save transaction to MySQL database
            DatabaseHelper.insertTransaction(customer, regNo, vehicleType, serviceType, addonsStr, serviceDate.toString(), finalAmount);

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Calculation Error");
            alert.setHeaderText(null);
            alert.setContentText("An error occurred during calculation: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void handleReset() {
        txtCustomerName.clear();
        txtVehicleRegNo.clear();
        cmbVehicleType.setValue(null);
        cmbServiceType.setValue(null);
        datePickerService.setValue(null);
        if (chkEngineOil != null) chkEngineOil.setSelected(false);
        if (chkBrakeInspection != null) chkBrakeInspection.setSelected(false);
        if (chkAircond != null) chkAircond.setSelected(false);
        if (chkCarWash != null) chkCarWash.setSelected(false);
        
        if (lblSummaryCustomer != null) lblSummaryCustomer.setText("-");
        if (lblSummaryVehicle != null) lblSummaryVehicle.setText("-");
        if (lblSummaryDate != null) lblSummaryDate.setText("-");
        if (lblSummaryService != null) lblSummaryService.setText("RM 0.00");
        if (lblSummaryAddons != null) lblSummaryAddons.setText("RM 0.00");
        if (lblSummaryServiceCharge != null) lblSummaryServiceCharge.setText("RM 30.00");
        if (lblSummaryDiscount != null) lblSummaryDiscount.setText("RM 0.00");
        if (lblSummaryFinal != null) lblSummaryFinal.setText("RM 0.00");
    }

    @FXML
    public void handleViewHistory() {
        Stage historyStage = new Stage();
        historyStage.setTitle("Customer Transaction History");

        Label titleLabel = new Label("Saved Service Transactions");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #faf5ff;");

        TableView<TransactionRecord> table = new TableView<>();
        table.setItems(DatabaseHelper.getAllTransactions());

        TableColumn<TransactionRecord, String> colCust = new TableColumn<>("Customer Name");
        colCust.setCellValueFactory(new PropertyValueFactory<>("customerName"));

        TableColumn<TransactionRecord, String> colReg = new TableColumn<>("Vehicle Reg");
        colReg.setCellValueFactory(new PropertyValueFactory<>("vehicleReg"));

        TableColumn<TransactionRecord, String> colService = new TableColumn<>("Service Type");
        colService.setCellValueFactory(new PropertyValueFactory<>("serviceType"));

        TableColumn<TransactionRecord, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("serviceDate"));

        TableColumn<TransactionRecord, Double> colTotal = new TableColumn<>("Final Amount (RM)");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("finalAmount"));

        table.getColumns().addAll(colCust, colReg, colService, colDate, colTotal);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // --- NEW BUTTON TO PRINT ALL HISTORY PDF ---
        Button printHistoryBtn = new Button("Print History PDF");
        printHistoryBtn.setStyle("-fx-background-color: #7c3aed; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        printHistoryBtn.setOnAction(e -> handlePrintHistoryPdf());

        Button closeBtn = new Button("Close Window");
        closeBtn.getStyleClass().add("secondary-button");
        closeBtn.setOnAction(e -> historyStage.close());

        HBox buttonBar = new HBox(10, printHistoryBtn, closeBtn);
        buttonBar.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        VBox vbox = new VBox(15, titleLabel, table, buttonBar);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #09090b;");
        VBox.setVgrow(table, Priority.ALWAYS);

        Scene scene = new Scene(vbox, 780, 460);
        try {
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }

        historyStage.setScene(scene);
        historyStage.show();
    }
    
    @FXML
    private void handlePrintHistoryPdf() {
        javafx.collections.ObservableList<TransactionRecord> records = DatabaseHelper.getAllTransactions();
        if (records == null || records.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Records Found");
            alert.setHeaderText(null);
            alert.setContentText("There is no transaction history available to print.");
            alert.showAndWait();
            return;
        }

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            PDPageContentStream cs = new PDPageContentStream(document, page);

            // Header Banner
            cs.setNonStrokingColor(0.294f, 0.0f, 0.510f); // Purple
            cs.addRect(40, 715, 532, 50);
            cs.fill();

            cs.beginText();
            cs.setNonStrokingColor(1.0f, 1.0f, 1.0f);
            cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 13);
            cs.newLineAtOffset(50, 733);
            cs.showText("AUTOMOTIVE MANAGEMENT - TRANSACTION HISTORY REPORT");
            cs.endText();

            // Table Column Headers
            float yPosition = 660f;
            cs.beginText();
            cs.setNonStrokingColor(0.157f, 0.157f, 0.157f);
            cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 10);
            cs.newLineAtOffset(45, yPosition);
            cs.showText("Customer Name");
            cs.newLineAtOffset(160, 0);
            cs.showText("Vehicle Reg");
            cs.newLineAtOffset(110, 0);
            cs.showText("Service Type");
            cs.newLineAtOffset(120, 0);
            cs.showText("Date");
            cs.newLineAtOffset(80, 0);
            cs.showText("Amount (RM)");
            cs.endText();

            // Divider Line
            yPosition -= 8;
            cs.setStrokingColor(0.6f, 0.6f, 0.6f);
            cs.setLineWidth(1f);
            cs.moveTo(40, yPosition);
            cs.lineTo(572, yPosition);
            cs.stroke();

            // Rows Loop with precise coordinate placement
            yPosition -= 25;
            for (TransactionRecord record : records) {
                // Auto-create new page if it exceeds bottom margin
                if (yPosition < 50) {
                    cs.close();
                    page = new PDPage();
                    document.addPage(page);
                    cs = new PDPageContentStream(document, page);
                    yPosition = 730f;
                }

                cs.beginText();
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 9);
                cs.setNonStrokingColor(0.2f, 0.2f, 0.2f);
                
                // Column 1: Customer Name
                cs.newLineAtOffset(45, yPosition);
                cs.showText(truncate(record.getCustomerName(), 18));
                
                // Column 2: Vehicle Reg
                cs.newLineAtOffset(160, 0);
                cs.showText(truncate(record.getVehicleReg(), 12));
                
                // Column 3: Service Type
                cs.newLineAtOffset(110, 0);
                cs.showText(truncate(record.getServiceType(), 18));
                
                // Column 4: Date
                cs.newLineAtOffset(120, 0);
                cs.showText(truncate(record.getServiceDate(), 12));
                
                // Column 5: Amount
                cs.newLineAtOffset(80, 0);
                cs.showText(String.format("RM %.2f", record.getFinalAmount()));
                
                cs.endText();

                yPosition -= 20; // Move down for the next row
            }
            cs.close();

            String filename = "All_Transactions_Report.pdf";
            document.save(filename);

            // Instantly open the PDF file
            File pdfFile = new File(filename);
            if (pdfFile.exists() && java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(pdfFile);
            }

            //Alert alert = new Alert(Alert.AlertType.INFORMATION);
            //alert.setTitle("History PDF Generated");
            //alert.setHeaderText(null);
            //alert.setContentText("Full transaction history report successfully saved and opened as:\n" + filename);
            //alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("PDF Error");
            alert.setHeaderText("Failed to generate history report");
            alert.setContentText("Error details: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private String truncate(String value, int maxLength) {
        if (value == null) return "";
        return value.length() > maxLength ? value.substring(0, maxLength - 3) + "..." : value;
    }

    @FXML
    private void handlePrintPdf() {
        if (txtCustomerName.getText().trim().isEmpty() || txtVehicleRegNo.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Incomplete Information");
            alert.setHeaderText(null);
            alert.setContentText("Please perform a calculation before printing the PDF receipt.");
            alert.showAndWait();
            return;
        }

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                // Header Banner
                cs.setNonStrokingColor(0.294f, 0.0f, 0.510f);
                cs.addRect(40, 710, 532, 60);
                cs.fill();

                cs.beginText();
                cs.setNonStrokingColor(1.0f, 1.0f, 1.0f);
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
                cs.newLineAtOffset(55, 735);
                cs.showText("AUTOMOTIVE MANAGEMENT");
                
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 10);
                cs.newLineAtOffset(0, -16);
                cs.showText("Official Service Invoice & Receipt");
                cs.endText();

                // Customer details box border
                cs.setStrokingColor(0.784f, 0.784f, 0.784f);
                cs.setLineWidth(1);
                cs.addRect(40, 560, 532, 130);
                cs.stroke();

                cs.beginText();
                cs.setNonStrokingColor(0.157f, 0.157f, 0.157f);
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 12);
                cs.newLineAtOffset(55, 670);
                cs.showText("Customer & Vehicle Information");

                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 11);
                cs.newLineAtOffset(0, -22);
                cs.showText("Customer Name:   " + txtCustomerName.getText().trim());
                cs.newLineAtOffset(0, -18);
                cs.showText("Vehicle Reg No:    " + txtVehicleRegNo.getText().trim().toUpperCase());
                cs.newLineAtOffset(0, -18);
                cs.showText("Vehicle Type:        " + (cmbVehicleType.getValue() != null ? cmbVehicleType.getValue() : "-"));
                cs.newLineAtOffset(0, -18);
                cs.showText("Service Date:        " + (datePickerService.getValue() != null ? datePickerService.getValue().toString() : "-"));
                cs.endText();

                // Table Header Background
                cs.setNonStrokingColor(0.941f, 0.902f, 1.0f);
                cs.addRect(40, 500, 532, 30);
                cs.fill();

                cs.beginText();
                cs.setNonStrokingColor(0.294f, 0.0f, 0.510f);
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 11);
                cs.newLineAtOffset(55, 510);
                cs.showText("Description / Service Package");
                cs.newLineAtOffset(360, 0);
                cs.showText("Amount");
                cs.endText();

                // Table Content
                cs.beginText();
                cs.setNonStrokingColor(0.196f, 0.196f, 0.196f);
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 11);
                
                float startY = 470f;
                cs.newLineAtOffset(55f, startY);
                cs.showText("• " + (cmbServiceType.getValue() != null ? cmbServiceType.getValue() : "Standard Service"));
                cs.newLineAtOffset(360f, 0f);
                cs.showText(lblSummaryService.getText());
                
                cs.newLineAtOffset(-360f, -25f);
                cs.showText("• Add-ons & Extras Selected");
                cs.newLineAtOffset(360f, 0f);
                cs.showText(lblSummaryAddons.getText());

                cs.newLineAtOffset(-360f, -25f);
                cs.showText("• Standard Service & Handling Charge");
                cs.newLineAtOffset(360f, 0f);
                cs.showText(lblSummaryServiceCharge.getText());

                cs.newLineAtOffset(-360f, -25f);
                cs.showText("• Discount Applied (10% if > RM300)");
                cs.newLineAtOffset(360f, 0f);
                cs.showText("-" + lblSummaryDiscount.getText());
                
                cs.endText();

                // Total Divider & Amount
                cs.setStrokingColor(0.588f, 0.588f, 0.588f);
                cs.setLineWidth(1);
                cs.moveTo(40, 360);
                cs.lineTo(572, 360);
                cs.stroke();

                cs.beginText();
                cs.setNonStrokingColor(0.294f, 0.0f, 0.510f);
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 13);
                cs.newLineAtOffset(55, 330);
                cs.showText("TOTAL PAYABLE AMOUNT:");

                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 15);
                cs.newLineAtOffset(330, 0);
                cs.showText(lblSummaryFinal.getText());
                cs.endText();

                // Footer
                cs.beginText();
                cs.setNonStrokingColor(0.471f, 0.471f, 0.471f);
                cs.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE), 10);
                cs.newLineAtOffset(55, 80);
                cs.showText("Thank you for choosing Automotive Services!");
                cs.endText();
            }

            String filename = "Invoice_" + txtVehicleRegNo.getText().trim().toUpperCase().replaceAll("\\s+", "_") + ".pdf";
            document.save(filename);

            // --- INSTANTLY OPEN / POP UP THE PDF FILE ---
            File pdfFile = new File(filename);
            if (pdfFile.exists() && java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(pdfFile);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("PDF Error");
            alert.setHeaderText("Failed to generate or open PDF receipt");
            alert.setContentText("Error details: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    @FXML
    public void handleExit() {
        System.exit(0);
    }
}