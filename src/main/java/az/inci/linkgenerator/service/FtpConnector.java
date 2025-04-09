package az.inci.linkgenerator.service;

import az.inci.linkgenerator.util.Logger;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FtpConnector {
    private final FTPClient ftpClient;
    private final Logger logger;

    public FtpConnector(Logger logger) {
        this.logger = logger;
        ftpClient = new FTPClient();
    }

    public FTPClient connect() {
        Properties props = new Properties();
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            props.load(stream);
        } catch (IOException e) {
            logger.logError(e.toString());
            throw new IllegalStateException(e.toString());
        }

        String host = props.getProperty("ftp.host");
        int port = Integer.parseInt(props.getProperty("ftp.port"));
        String user = props.getProperty("ftp.user");
        String password = props.getProperty("ftp.password");
        try {
            logger.logInfo("FTP serverə qoşulur...");
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.connect(host, port);
            boolean loginSuccess = ftpClient.login(user, password);
            logger.logInfo("Qoşulma statusu: " + (loginSuccess ? "Uğurlu" : "Uğursuz"));

            if (loginSuccess)
                return ftpClient;
            else
                throw new RuntimeException("FTP serverə qoşulma uğursuz oldu.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
