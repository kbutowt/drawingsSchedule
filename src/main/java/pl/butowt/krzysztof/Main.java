package pl.butowt.krzysztof;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.butowt.krzysztof.controllers.ApiController;


import java.util.ResourceBundle;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }
 
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("DrawingScheduleGenerator");

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Main.class.getResource("/pl/butowt/krzysztof/controllers/select_project.fxml"));
        loader.setResources(ResourceBundle.getBundle("pl.butowt.krzysztof.controllers.messages.selectproject"));
        Parent root = loader.load();
        ApiController apiController = loader.getController();
        apiController.setStage(stage);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}


