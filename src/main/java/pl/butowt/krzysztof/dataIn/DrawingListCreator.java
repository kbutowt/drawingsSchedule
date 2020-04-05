package pl.butowt.krzysztof.dataIn;

import lombok
        .AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import pl.butowt.krzysztof.dataIn.interfaces.DrawingCollector;
import pl.butowt.krzysztof.dataIn.model.Drawing;
import pl.butowt.krzysztof.dataIn.model.DrawingGroup;
import pl.butowt.krzysztof.dataIn.model.FileType;
import pl.butowt.krzysztof.errorHandler.*;
import pl.butowt.krzysztof.errorHandler.interfaces.Dialogs;
import pl.butowt.krzysztof.errorHandler.interfaces.Errors;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Create objects from file names
 */
@Log
@NoArgsConstructor
@AllArgsConstructor
public class DrawingListCreator implements DrawingCollector {

    private Errors errorManager = new ErrorManager(ErrorStorage.getInstance());
    private Dialogs errorWindow = new DialogWindow();

    /**
     * Add new drawings to drawing group
     * @param drawingGroups list with drawing groups (subdirectories chosen by user)
     * @param type collect only files which type equal as user's choice (.pdf or .dwg)
     */
    @Override
    public void getGroupsWithDrawingsToFile(List<DrawingGroup> drawingGroups, FileType type) {
        for (DrawingGroup drawingGroup : drawingGroups) {
            for (String newDrawingName : drawingGroup.getNewDrawingsNames()) {
                SortedMap<Integer, Drawing> newDrawing = this.createNewObjectsFromFileName(newDrawingName, type, drawingGroup);
                drawingGroup.addToDrawingsToExcel(newDrawing);
            }
        }
    }

    /**
     * Join list new drawings from subdirectory (group) and join with the same group with existing file.
     * User receives information if generated schedule is empty.
     * @param allDrawings contains drawings from existing file
     * @param newDrawings contains new drawing loaded from directories
     * @return combined list. This list will use to create new schedule
     */
    @Override
    public List<DrawingGroup> joinExistingAndNewDrawings(List<DrawingGroup> allDrawings, List<DrawingGroup> newDrawings) {
        List<DrawingGroup> temporaryList = new ArrayList<>();
        if (allDrawings.size() == 0) {
            allDrawings = newDrawings;
        } else {
            for (DrawingGroup allDrawing : allDrawings) {
                for (DrawingGroup newDrawing : newDrawings) {
                    if (allDrawing.getGroupName().equals(newDrawing.getGroupName())) {
                        allDrawing.getDrawingsToExcel().putAll(newDrawing.getDrawingsToExcel());
                        newDrawing.getDrawingsToExcel().clear();
                    }
                    if (!temporaryList.contains(newDrawing)) {
                        temporaryList.add(newDrawing);
                    }
                }
            }
        }
        allDrawings.addAll(temporaryList);
        allDrawings = allDrawings.stream().sorted().collect(Collectors.toList());
        allDrawings.removeIf(group -> (group.getDrawingsToExcel().size() == 0));
        if (allDrawings.size() == 0) {
            errorWindow.showWarningDialog("Warning", "Generated schedule is empty");
        }
        return allDrawings;
    }

    /**
     * If drawing has new  revision, thenthen new date is added to list of revisions.
     * Revision name is validated during reading data. In case error, user receives information.
     * @param allDrawings contains drawings from existing file
     * @param type collect only files which type equal as user's choice (.pdf or .dwg)
     * @param revisedDrawings contains drawings with new revision
     * @return
     */
    @Override
    public List<DrawingGroup> addNewRevisionToDrawing(List<DrawingGroup> allDrawings, FileType type, List<String> revisedDrawings) {
        for (DrawingGroup allDrawing : allDrawings) {
            SortedMap<Integer, Drawing> map = allDrawing.getDrawingsToExcel();
            for (String revisedDrawing : revisedDrawings) {
                String[] devisedDrawing = revisedDrawing.split("-");
                for (Map.Entry<Integer, Drawing> entry : map.entrySet()) {
                    if (entry.getKey().equals(Integer.valueOf(devisedDrawing[0]))) {
                        try {
                            devisedDrawing[2] = devisedDrawing[2].toLowerCase().replaceAll("rew_", "");
                            devisedDrawing[2] = devisedDrawing[2].replaceAll(type.getFileType(), "");
                            entry.getValue().setRevisionVersion(devisedDrawing[2].toUpperCase());
                        } catch (ArrayIndexOutOfBoundsException e) {
                            errorWindow.showErrorDialog("Reader error", "Failure during reading data from directory. Check file error.txt");
                            errorManager.saveErrorsToStorage("File : " + revisedDrawing + " has wrong name");
                        }
                        LocalDate today = LocalDate.now();
                        entry.getValue().addToRevisionDate(today.toString());
                    }
                }
            }
        }
        return allDrawings;
    }

    /**
     * Get revised drawing to group
     * @param groups current group from revised drawings are get
     * @return revised drawings
     */
    @Override
    public List<String> getAllRevisedDrawingsFromGroup(List<DrawingGroup> groups) {
        List<String> revisedDrawings = new ArrayList<>();
        for (DrawingGroup group : groups) {
            revisedDrawings.addAll(group.getRevisionDrawingsNames());
        }
        return revisedDrawings;
    }

    /**
     * Convert string name of drawing and create object which is attributed to group
     * @param newFileName name of single drawing from directory chosen by user
     * @param type collect only files which type equal as user's choice (.pdf or .dwg)
     * @param drawingGroup here new drawing is attributed
     * @return map (drawing number, drawing (object) for current group
     */
    private SortedMap<Integer, Drawing> createNewObjectsFromFileName(String newFileName, FileType type, DrawingGroup drawingGroup) {
        SortedMap<Integer, Drawing> map = new TreeMap<>();
        try {
            Drawing newDrawing = new Drawing();
            String[] splitFile = newFileName.split("-");
            newDrawing.setName(splitFile[1].replace(type.getFileType(), ""));
            newDrawing.setRevisionVersion("0");
            LocalDate today = LocalDate.now();
            newDrawing.addToRevisionDate(today.toString());
            map.put(Integer.valueOf(splitFile[0].trim()), newDrawing);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException ex) {
            errorWindow.showErrorDialog("Reader error", "Failure during reading data from directory. Check file error.txt");
            errorManager.saveErrorsToStorage("File : " + newFileName + " in directory: " + drawingGroup.getGroupName() + " has wrong name");
        }
        return map;
    }
}
