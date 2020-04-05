package pl.butowt.krzysztof.dataIn.interfaces;

import pl.butowt.krzysztof.dataIn.model.DrawingGroup;
import pl.butowt.krzysztof.dataIn.model.HeaderSettings;

import java.util.List;

public interface FileReader {
    String findLastModifiedFile(String pathToSchedulesDirectory);

    List<DrawingGroup> readDataFromFile(String pathTFile, HeaderSettings header);
}
