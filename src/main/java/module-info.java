module az.inci.invoiceconverter {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.apache.poi.ooxml;
    requires static lombok;
    requires java.desktop;
    requires org.slf4j;
    requires org.apache.logging.log4j;
    requires java.sql;
    requires org.apache.commons.net;
    requires jdk.jsobject;

    exports az.inci.linkgenerator;
    opens az.inci.linkgenerator to javafx.fxml;
}