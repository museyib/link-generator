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
    exports az.inci.linkgenerator.controller;
    opens az.inci.linkgenerator.controller to javafx.fxml;
    exports az.inci.linkgenerator.service;
    opens az.inci.linkgenerator.service to javafx.fxml;
    exports az.inci.linkgenerator.data;
    opens az.inci.linkgenerator.data to javafx.fxml;
    exports az.inci.linkgenerator.util;
    opens az.inci.linkgenerator.util to javafx.fxml;
    exports az.inci.linkgenerator.factory;
    opens az.inci.linkgenerator.factory to javafx.fxml;
}