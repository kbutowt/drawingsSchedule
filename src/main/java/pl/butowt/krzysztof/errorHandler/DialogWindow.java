package pl.butowt.krzysztof.errorHandler;

import javafx.scene.control.Alert;
import pl.butowt.krzysztof.errorHandler.interfaces.Dialogs;

public class DialogWindow implements Dialogs {

    public void showErrorDialog (String tittle, String message){
        Alert confirm = new Alert(Alert.AlertType.ERROR);
        confirm.setTitle("Something goes wrong!");
        confirm.setHeaderText(tittle);
        confirm.setContentText(message);
        confirm.showAndWait();
    }
    public void showWarningDialog (String tittle, String message){
        Alert confirm = new Alert(Alert.AlertType.WARNING);
        confirm.setTitle("Something goes wrong!");
        confirm.setHeaderText(tittle);
        confirm.setContentText(message);
        confirm.showAndWait();
    }
}
