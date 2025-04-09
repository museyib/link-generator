package az.inci.linkgenerator.util;

public interface UIInteraction {
    void focusOnInvCodeList();
    void disableControls(boolean disable);
    void logMessage(String message, String styleClass);
}