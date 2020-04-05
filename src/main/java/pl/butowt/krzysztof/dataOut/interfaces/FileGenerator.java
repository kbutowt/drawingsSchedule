package pl.butowt.krzysztof.dataOut.interfaces;

import pl.butowt.krzysztof.dataIn.model.DrawingGroup;
import pl.butowt.krzysztof.dataIn.model.HeaderSettings;

import java.io.IOException;
import java.util.List;

public interface FileGenerator {
    String createNewSchedule (List<DrawingGroup> drawingsGroup, String pathToExcelsDirectory, HeaderSettings header) throws IOException;
}
