module com.kitchenreceiptprint {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;
    requires java.desktop;
    requires org.apache.commons.net;
    requires org.apache.pdfbox;

    opens com.kitchenreceiptprint to javafx.fxml;
    opens com.kitchenreceiptprint.controller to javafx.fxml;

    exports com.kitchenreceiptprint;
    opens com.kitchenreceiptprint.util to javafx.fxml;
}
