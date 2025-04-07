package az.inci.linkgenerator;


import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebView;
import lombok.extern.slf4j.Slf4j;
import netscape.javascript.JSObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class MainWindowController {

    @FXML
    public WebView logView;
    @FXML
    public TextArea invCodeList;
    @FXML
    public Button generateFromList;
    @FXML
    public Button generateForAll;

    @FXML
    public void initialize() {
        logView.getEngine().loadContent("""
                <html>
                <head>
                    <style>
                        .log-info { color: blue }
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

    private void logMessage(String message, String styleClass) {
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


    public void logInfo(String info) {
        logMessage("Info >> " + info, "log-info");
        log.info(info);
    }

    public void logError(String error) {
        logMessage("Error >> " + error, "log-error");
        log.error(error);
        disableControls(false);
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
        });
    }

    @FXML
    public void onGenerateForAllClick() {
        new Thread(() -> {
            LinkGenerator linkGenerator = new LinkGenerator(this, new DataSource(this));
            linkGenerator.generateForAll();
        }).start();
    }

    @FXML
    public void onGenerateFromListClick() {
        List<String> codeList = Arrays.asList(
                invCodeList.getText()
                        .replaceAll("\n", " ")
                        .toLowerCase()
                        .split(" "));
        new Thread(() -> {
            LinkGenerator linkGenerator = new LinkGenerator(this, new DataSource(this));
            linkGenerator.generateForSelected(codeList);
        }).start();
    }

    public void openFolder(String filePath) {
        if (filePath != null) {
            try {
                Runtime.getRuntime().exec("explorer /select, " + filePath);
            }
            catch (IOException e) {
                logError(e.toString());
            }
        }
    }

    public void openFile(String filePath) {
        if (filePath != null) {
            try {
                Runtime.getRuntime().exec("explorer /open, " + filePath);
            }
            catch (IOException e) {
                logError(e.toString());
            }
        }
    }
}
