package az.inci.linkgenerator.util;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggerImpl implements Logger {
    private final UIInteraction uiInteraction;

    public LoggerImpl(UIInteraction uiInteraction) {
        this.uiInteraction = uiInteraction;
    }

    @Override
    public void logInfo(String message) {
        uiInteraction.logMessage("Info >> " + message, "log-info");
        log.info(message);
    }

    @Override
    public void logWarning(String message) {
        uiInteraction.logMessage("Warning >> " + message, "log-warning");
        log.info(message);
        uiInteraction.disableControls(false);
    }

    @Override
    public void logError(String message) {
        uiInteraction.logMessage("Error >> " + message, "log-error");
        log.error(message);
        uiInteraction.disableControls(false);
    }
}
