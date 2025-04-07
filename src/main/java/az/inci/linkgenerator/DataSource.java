package az.inci.linkgenerator;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DataSource {
    private final String connectionString;
    private final MainWindowController controller;

    public DataSource(MainWindowController controller) {
        this.controller = controller;
        Properties props = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            props.load(inputStream);
        } catch (IOException e) {
            controller.logError(e.toString());
            throw new IllegalStateException(e.toString());
        }
        String url = props.getProperty("datasource.url");
        String username = props.getProperty("datasource.username");
        String password = props.getProperty("datasource.password");

        connectionString = url.concat(";User=").concat(username).concat(";Password=").concat(password);
    }

    public Connection connection() {
        Connection conn = null;
        controller.logInfo("Bazaya qoşulur...");
        try {
            conn = DriverManager.getConnection(connectionString);
            controller.logInfo("Qoşulma uğurla baş verdi.");
        } catch (SQLException e) {
            controller.logError(e.toString());
        }
        return conn;
    }
}
