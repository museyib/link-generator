package az.inci.linkgenerator.controller;


import az.inci.linkgenerator.service.LinkGenerator;
import az.inci.linkgenerator.util.Logger;
import az.inci.linkgenerator.util.LoggerImpl;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MainWindowController {
    private final Logger logger = new LoggerImpl(this);
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
                logView.getEngine().executeScript("window.openFile = function(filePath) {javaApp.openFile(filePath); }");
                logView.getEngine().executeScript("window.openFolder = function(folderPath) {javaApp.openFolder(folderPath); }");
            }
        });

        logView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) logView.getEngine().executeScript("window");
                window.setMember("javaApp", this);
            }
        });
    }

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

    private String escapeJavaScript(String message) {
        return message.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("'", "\\'")
                .replace("\n", "\\n")
                .replace("\r", "");
    }

    public void disableControls(boolean disable) {
        Platform.runLater(() -> {
            generateForAll.setDisable(disable);
            generateFromList.setDisable(disable);
            invCodeList.setDisable(disable);
        });
    }

    public void focusOnInvCodeList() {
        invCodeList.requestFocus();
    }

    @FXML
    public void onGenerateForAllClick() {
        disableControls(true);
        new Thread(() -> {
            LinkGenerator linkGenerator = new LinkGenerator(this, logger);
            linkGenerator.generateForAll();
            disableControls(false);
        }).start();
    }

    @FXML
    public void onGenerateFromListClick() {
        disableControls(true);
        List<String> codeList = Arrays.stream(invCodeList.getText()
                        .replaceAll("\n", " ")
                        .toLowerCase()
                        .split(" ")).filter(s -> !s.isEmpty()).toList();
        if (codeList.isEmpty()) {
            logger.logWarning("Kod siyahısı boşdur. Ən azı 1 kod əlavə edin.");
            invCodeList.requestFocus();
        }
        else {
            new Thread(() -> {
                LinkGenerator linkGenerator = new LinkGenerator(this, logger);
                linkGenerator.generateForSelected(codeList);
                disableControls(false);
            }).start();
        }
    }

    @SuppressWarnings("unused")
    public void openFolder(String filePath) {
        if (filePath != null) {
            try {
                Runtime.getRuntime().exec("explorer /select, " + filePath);
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
                Runtime.getRuntime().exec("explorer /open, " + filePath);
            }
            catch (IOException e) {
                logger.logError(e.toString());
            }
        }
    }
}
