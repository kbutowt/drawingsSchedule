package pl.butowt.krzysztof.errorHandler;

import lombok.extern.java.Log;
import pl.butowt.krzysztof.errorHandler.interfaces.Errors;

import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;

@Log
public class ErrorManager implements Errors {

    private ErrorStorage errorStorage;

    public ErrorManager(ErrorStorage errorStorage) {
        this.errorStorage = ErrorStorage.getInstance();
    }

    @Override
    public void saveErrorsToStorage(String errorText) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        ErrorMessage message = new ErrorMessage(errorText, formatter.format(date));

        errorStorage.getErrors().add(message);
    }

    @Override
    public void writeErrorsToFile(String pathToSchedulesDirectory) {
        File file = new File(pathToSchedulesDirectory + "\\error.txt");
        if (errorStorage.getErrors().size() > 0) {
            try (Writer wr = new FileWriter(file);
                 BufferedWriter bwr = new BufferedWriter(wr)) {
                for (ErrorMessage error : errorStorage.getErrors()) {
                    bwr.write(error.getDate() + " " + error.getErrorContent() + '\n');
                }
                Desktop desktop = Desktop.getDesktop();
                desktop.open(file);
            } catch (IOException ex) {
                log.log(Level.WARNING, ex.getMessage(), ex);
            }
        }
        errorStorage.getErrors().clear();
    }
}
