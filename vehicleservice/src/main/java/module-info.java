module com.mycompany.vehicleservice {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    opens com.mycompany.vehicleservice to javafx.fxml;
    exports com.mycompany.vehicleservice;
    requires org.apache.pdfbox;
}
