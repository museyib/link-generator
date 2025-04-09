package az.inci.linkgenerator.service;

import az.inci.linkgenerator.util.Logger;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FtpServiceImpl implements FtpService {
    private final Logger logger;

    public FtpServiceImpl(Logger logger) {
        this.logger = logger;
    }

    @Override
    public List<String> listImageCodes() {
        List<String> codeList = new ArrayList<>();
        FTPClient client = new FtpConnector(logger).connect();
        FTPFileFilter fileFilter = ftpFile -> ftpFile.getName().endsWith(".jpg");
        try {
            FTPFile[] ftpFiles = client.listFiles("/Inventory Images", fileFilter);

            logger.logInfo("Mövcud şəkil faylları siyahılanır...");
            for (FTPFile ftpFile : ftpFiles) {
                codeList.add(ftpFile.getName().substring(0, ftpFile.getName().indexOf(".")));
            }
        } catch (IOException e) {
            logger.logError(e.toString());
        } finally {
            try {
                client.disconnect();
            } catch (IOException e) {
                logger.logError(e.toString());
            }
        }

        return codeList;
    }
}
