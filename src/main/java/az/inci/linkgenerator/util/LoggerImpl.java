package az.inci.linkgenerator.util;


import az.inci.linkgenerator.controller.MainWindowController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggerImpl implements Logger {
    private final MainWindowController controller;

    public LoggerImpl(MainWindowController controller) {
        this.controller = controller;
    }


    @Override
    public void logInfo(String message) {
        controller.logMessage("Info >> " + message, "log-info");
        log.info(message);
    }

    @Override
    public void logWarning(String message) {
        controller.logMessage("Warning >> " + message, "log-warning");
        log.info(message);
        controller.disableControls(false);
    }

    @Override
    public void logError(String message) {
        controller.logMessage("Error >> " + message, "log-error");
        log.error(message);
        controller.disableControls(false);
    }
}
