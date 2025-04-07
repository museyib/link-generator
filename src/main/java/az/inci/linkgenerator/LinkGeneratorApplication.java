package az.inci.linkgenerator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LinkGeneratorApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(LinkGeneratorApplication.class.getResource("main-window.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        ((MainWindowController) fxmlLoader.getController()).setHostServices(getHostServices());
        stage.setTitle("Şəkillər üçün link generator");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
