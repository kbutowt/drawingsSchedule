package pl.butowt.krzysztof.dataIn.interfaces;

import pl.butowt.krzysztof.dataIn.model.DrawingGroup;
import pl.butowt.krzysztof.dataIn.model.FileType;


import java.util.List;

public interface DrawingLoader {

    List<DrawingGroup> getFullFileNamesGroups(String path, FileType type);
}
