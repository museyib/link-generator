package az.inci.linkgenerator;

import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FTPConnection {
    private final FTPClient ftpClient;
    private final MainWindowController controller;

    public FTPConnection(MainWindowController controller) {
        this.controller = controller;
        ftpClient = new FTPClient();
    }

    public FTPClient connect() {
        Properties props = new Properties();
        try (InputStream stream = controller.getClass().getClassLoader().getResourceAsStream("application.properties")) {
            props.load(stream);
        } catch (IOException e) {
            controller.logError(e.toString());
            throw new IllegalStateException(e.toString());
        }

        String host = props.getProperty("ftp.host");
        int port = Integer.parseInt(props.getProperty("ftp.port"));
        String user = props.getProperty("ftp.user");
        String password = props.getProperty("ftp.password");
        try {
            controller.logInfo("FTP serverə qoşulur...");
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.connect(host, port);
            boolean loginSuccess = ftpClient.login(user, password);
            controller.logInfo("Qoşulma statusu: " + (loginSuccess ? "Uğurlu" : "Uğursuz"));

            if (loginSuccess)
                return ftpClient;
            else
                throw new RuntimeException("FTP serverə qoşulma uğursuz oldu.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
