package pl.butowt.krzysztof.dataIn.interfaces;

import pl.butowt.krzysztof.dataIn.model.DrawingGroup;
import pl.butowt.krzysztof.dataIn.model.FileType;

import java.util.List;

public interface DrawingCollector {
    void getGroupsWithDrawingsToFile(List<DrawingGroup> drawingGroups, FileType type);

    List<DrawingGroup> joinExistingAndNewDrawings(List<DrawingGroup> allDrawings, List<DrawingGroup> newDrawings);

    List<DrawingGroup> addNewRevisionToDrawing(List<DrawingGroup> allDrawings, FileType type, List<String> revisedDrawings);

    List<String> getAllRevisedDrawingsFromGroup(List<DrawingGroup> groups);
}
