package pl.butowt.krzysztof.dataOut;


import pl.butowt.krzysztof.dataIn.*;
import pl.butowt.krzysztof.dataIn.interfaces.DrawingCollector;
import pl.butowt.krzysztof.dataIn.interfaces.FileReader;
import pl.butowt.krzysztof.dataIn.interfaces.DrawingLoader;
import pl.butowt.krzysztof.dataIn.model.DrawingGroup;
import pl.butowt.krzysztof.dataIn.model.FileType;
import pl.butowt.krzysztof.dataIn.model.HeaderSettings;

import pl.butowt.krzysztof.dataOut.interfaces.FileGenerator;
import pl.butowt.krzysztof.errorHandler.ErrorManager;
import pl.butowt.krzysztof.errorHandler.ErrorStorage;
import pl.butowt.krzysztof.errorHandler.interfaces.Errors;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Collect all main method necessary to create new schedule
 */
public class ScheduleGenerator {

    private Errors errorManager = new ErrorManager(ErrorStorage.getInstance());

    private DrawingLoader loadData = new DrawingNamesLoader();

    private DrawingCollector drawingListCreator = new DrawingListCreator();

    private FileGenerator excelGenerator = new ExcelGenerator();

    private FileReader excelReader = new ExcelReader();

    /**
     * Facade which hide all needed operations for create ne schedule.
     * At first new files (chosen by user) are  loaded to memory and selected for groups.
     * Later data from last modified schedule (from schedules also chosen by user) also loaded to memory.
     * New and existing data are joined and new schedule will create.
     * @param type of files chosen by user (.dwg of pdf)
     * @param directoryToLoadFiles directory with new data
     * @param directoryWithSchedules directory with existing schedule. There new schedule will create.
     * @param header contains  header's params
     * @return path to created file
     * @throws IOException
     */
    public String generateSchedule(FileType type, File directoryToLoadFiles, File directoryWithSchedules, HeaderSettings header) throws IOException {
        List<DrawingGroup> newDrawings = loadData.getFullFileNamesGroups(directoryToLoadFiles.getAbsolutePath(), type);
        drawingListCreator.getGroupsWithDrawingsToFile(newDrawings, type);
        String pathToLastModifiedFile = excelReader.findLastModifiedFile(directoryWithSchedules.getAbsolutePath());
        List<DrawingGroup> allDrawings = excelReader.readDataFromFile(pathToLastModifiedFile,header);
        List<String> revisedDrawings = drawingListCreator.getAllRevisedDrawingsFromGroup(newDrawings);
        allDrawings = drawingListCreator.addNewRevisionToDrawing(allDrawings, type, revisedDrawings);
        allDrawings = drawingListCreator.joinExistingAndNewDrawings(allDrawings, newDrawings);
        String path = excelGenerator.createNewSchedule(allDrawings, directoryWithSchedules.getAbsolutePath(), header);
        errorManager.writeErrorsToFile(directoryWithSchedules.getAbsolutePath());

        newDrawings.clear();
        allDrawings.clear();
        revisedDrawings.clear();

        return path;
    }
}
