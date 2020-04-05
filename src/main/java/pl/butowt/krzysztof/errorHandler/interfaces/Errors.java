package pl.butowt.krzysztof.errorHandler.interfaces;

public interface Errors {
    void saveErrorsToStorage (String errorText);
    void writeErrorsToFile (String pathToSchedulesDirectory);
}
