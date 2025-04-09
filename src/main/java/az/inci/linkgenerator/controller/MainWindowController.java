package az.inci.linkgenerator.controller;


import az.inci.linkgenerator.AppContext;
import az.inci.linkgenerator.service.*;
import az.inci.linkgenerator.util.Logger;
import az.inci.linkgenerator.util.LoggerImpl;
import az.inci.linkgenerator.util.UIInteraction;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.io.IOException;

public class MainWindowController implements UIInteraction {
    private final Logger logger = new LoggerImpl(this);
    private final AppContext appContext = new AppContext(this, logger);
    @FXML
    private WebView logView;
    @FXML
    private TextArea invCodeList;
    @FXML
    private Button generateFromList;
    @FXML
    private Button generateForAll;

    @FXML
    public void initialize() {
        logView.getEngine().loadContent("""
                <html>
                <head>
                    <style>
                        body { background-color: #ddf; }
                        .log-info { color: blue }
                        .log-warning { color: orange }
                        .log-error { color: red }
                    </style>
                </head>
                <body id='log-container'>
                </body>
                </html>""");

        logView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) logView.getEngine().executeScript("window");
                window.setMember("javaApp", this);
                logView.getEngine().executeScript("window.openFile = function(filePath) {javaApp.openFile(filePath); }");
                logView.getEngine().executeScript("window.openFolder = function(folderPath) {javaApp.openFolder(folderPath); }");
            }
        });
    }

    @Override
    public void logMessage(String message, String styleClass) {
        String escapeMessage = escapeJavaScript(message);
        Platform.runLater(() -> {
            String js = """
                    var logContainer = document.getElementById('log-container');
                    var isAtBottom = logContainer.scrollHeight - logContainer.scrollTop <= logContainer.clientHeight + 5;;
                    var div = document.createElement('div');
                    div.className = '""" + styleClass + """
                    ';div.innerHTML = '""" + escapeMessage + """
                    ';logContainer.appendChild(div);
                    if (isAtBottom) window.scrollTo(0, document.body.scrollHeight);""";
            logView.getEngine().executeScript(js);
        });
    }

    @Override
    public void disableControls(boolean disable) {
        Platform.runLater(() -> {
            generateForAll.setDisable(disable);
            generateFromList.setDisable(disable);
            invCodeList.setDisable(disable);
        });
    }

    @Override
    public void focusOnInvCodeList() {
        Platform.runLater(() -> invCodeList.requestFocus());
    }

    private String escapeJavaScript(String message) {
        return message.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("'", "\\'")
                .replace("\n", "\\n")
                .replace("\r", "");
    }

    @FXML
    public void onGenerateForAllClick() {
        disableControls(true);
        new Thread(() -> {
            LinkGenerator linkGenerator = appContext.getLinkGeneratorFactory().create();
            linkGenerator.generateForAll();
            disableControls(false);
        }).start();
    }

    @FXML
    public void onGenerateFromListClick() {
        CodeListProcessor codeListProcessor = appContext.getCodeListProcessor(() -> invCodeList.getText());
        codeListProcessor.handleGenerateFromListClick();
    }

    @SuppressWarnings("unused")
    public void openFolder(String filePath) {
        if (filePath != null) {
            try {
                Runtime.getRuntime().exec("explorer /select, \"" + filePath + "\"");
            }
            catch (IOException e) {
                logger.logError(e.toString());
            }
        }
    }

    @SuppressWarnings("unused")
    public void openFile(String filePath) {
        if (filePath != null) {
            try {
                Runtime.getRuntime().exec("explorer \"" + filePath + "\"");
            }
            catch (IOException e) {
                logger.logError(e.toString());
            }
        }
    }
}
