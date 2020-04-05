package pl.butowt.krzysztof.errorHandler;

import java.util.ArrayList;
import java.util.List;

public class ErrorStorage {
    private static final ErrorStorage errorStorage = new ErrorStorage();
    private static final List<ErrorMessage> errors = new ArrayList<>();

    public ErrorStorage() {
    }

    public List<ErrorMessage> getErrors() {
        return errors;
    }

    public static ErrorStorage getInstance(){
        return  errorStorage;
    }
}
