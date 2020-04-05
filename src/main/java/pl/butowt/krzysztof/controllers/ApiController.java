package pl.butowt.krzysztof.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lombok.*;

import lombok.extern.java.Log;
import pl.butowt.krzysztof.dataIn.model.FileType;
import pl.butowt.krzysztof.dataIn.model.HeaderSettings;
import pl.butowt.krzysztof.errorHandler.*;
import pl.butowt.krzysztof.dataOut.ScheduleGenerator;
import pl.butowt.krzysztof.errorHandler.interfaces.Dialogs;
import pl.butowt.krzysztof.errorHandler.interfaces.Errors;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;

/**
 * Application controller responsible for run all user's choices.
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Log
public class ApiController implements Initializable {

    @FXML
    private GridPane apiWindow;

    @FXML
    private TextField footer;

    @FXML
    private TextField directoryToSchedules;

    @FXML
    private TextField directoryToDrawings;

    @FXML
    private ChoiceBox<FileType> fileTypes;

    @Setter
    private Stage stage;

    private String workDirectory;

    private File directoryToLoadFiles;

    private File directoryWithSchedules;

    private ObservableList<FileType> obsFileTypes = FXCollections.observableArrayList();

    private Errors errorManager = new ErrorManager(ErrorStorage.getInstance());

    private DirectoryChooser directoryChooser = new DirectoryChooser();

    private Dialogs errorWindow = new DialogWindow();

    private ScheduleGenerator generator = new ScheduleGenerator();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        obsFileTypes = fileTypes.getItems();
        obsFileTypes.addAll(Arrays.asList(FileType.values()));
        fileTypes.setValue(FileType.DWG);

        directoryToDrawings.setEditable(false);
        directoryToSchedules.setEditable(false);
        footer.setEditable(false);
    }

    /**
     * User chooses directory to files which will add to schedule.
     *
     * @param event
     */
    public void selectData(ActionEvent event) {
        try {
            directoryToLoadFiles = this.chooseDirectoryAndGetFile();
            directoryToDrawings.setText(directoryToLoadFiles.getAbsolutePath());
        } catch (IllegalArgumentException e) {
            log.log(Level.WARNING, "IllegalArgumentException");
            e.printStackTrace();
        } catch (NullPointerException e) {
            log.log(Level.WARNING, "NullPointerException");
            e.printStackTrace();
        }
    }

    /**
     * User chooses directory where new schedule will generate.
     *
     * @param actionEvent
     */
    public void selectDrawingSchedulesDirectory(ActionEvent actionEvent) {
        try {
            directoryWithSchedules = this.chooseDirectoryAndGetFile();
            directoryToSchedules.setText(directoryWithSchedules.getAbsolutePath());
        } catch (IllegalArgumentException e) {
            log.log(Level.WARNING, "IllegalArgumentException");
            e.printStackTrace();
        } catch (NullPointerException e) {
            log.log(Level.WARNING, "NullPointerException");
            e.printStackTrace();
        }
    }

    /**
     * Main method responsible for run generating new schedule. If schedule is generated correctly then will open automatically.
     * In case of errors there is created file with derror descriptions.
     *
     * @param actionEvent
     */

    public void generateSchedule(ActionEvent actionEvent) {
        try {
            HeaderSettings header = new HeaderSettings(2, 10, 1);
            File file = new File(generator.generateSchedule(this.getFileTypeFromUser(), directoryToLoadFiles, directoryWithSchedules, header));
            Desktop desktop = Desktop.getDesktop();
            desktop.open(file);
        } catch (NullPointerException e) {
            if (directoryToLoadFiles == null) {
                errorWindow.showErrorDialog("Wrong directory", "Any directory to generate schedule is not selected!");
            } else if (directoryWithSchedules == null) {
                errorWindow.showErrorDialog("Wrong directory", "Any directory to generate schedule is not selected!");
            } else {
                log.log(Level.WARNING, "NullPointerException");
                e.printStackTrace();
            }
        } catch (IOException e) {
            log.log(Level.WARNING, "There is problem with saving file");
        }
    }

    /**
     * Checks user choice about files types.
     *
     * @return one of two possible types: .dwg or .pdf type
     */
    public FileType getFileTypeFromUser() {
        return Arrays.stream(FileType.values())
                .filter(f -> f.equals(fileTypes.getSelectionModel().getSelectedItem()))
                .findFirst()
                .orElse(FileType.DWG);
    }

    private File chooseDirectoryAndGetFile() {
        return directoryChooser.showDialog(stage);
    }
}
