package pl.butowt.krzysztof.dataIn;

import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import pl.butowt.krzysztof.dataIn.interfaces.DrawingLoader;
import pl.butowt.krzysztof.dataIn.model.DrawingGroup;
import pl.butowt.krzysztof.dataIn.model.FileType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Load all files selected and recognized by file names.
 */

@NoArgsConstructor
@Log
public class DrawingNamesLoader implements DrawingLoader {

    /**
     * Create drawings group which are represented by subdirectories from main directory
     * @param pathToDrawings to directory to files which will add to schedule
     * @param type chosen by user (dwg or pdf)
     * @return all created drawing groups with files marked as new and revised drawings
     */
    @Override
    public List<DrawingGroup> getFullFileNamesGroups(String pathToDrawings, FileType type){
        List<DrawingGroup> drawingGroups = new ArrayList<>();
        File directory = new File(pathToDrawings);
        File[] directories = directory.listFiles();
        for (File file : directories) {
            if (file.isDirectory()){
                DrawingGroup drawingGroup = new DrawingGroup(file.getName(), file.getAbsolutePath());
                this.createDrawingGroup(drawingGroup, type);
                drawingGroups.add(drawingGroup);
            }
        }
        return drawingGroups;
    }

    /**
     * Collect all files to group which are the same as user's choice.
     * @param group new object represents
     * @param userTypeChoice
     * Drawings are separated for new drawings and
     */
    private void createDrawingGroup (DrawingGroup group, FileType userTypeChoice){
        try (Stream<Path> walk = Files.walk(Paths.get(group.getPathName()))) {
            List<String> drawings = walk.map(path -> path.getFileName().toString())
                    .filter(f -> f.endsWith(userTypeChoice.getFileType())).collect(Collectors.toList());
            this.separateNewAndRevisionDrawings(drawings, group);
        } catch (IOException e) {
            log.log(Level.WARNING, e.toString()+"\n Error during create group");
        }
    }

    /**
     * Separate drawings for new ones and revised.
     * @param allFullNamesDrawings list wih all drawing names from group
     * @param group represents each subfolder form main folder chosen by user
     */
    private void separateNewAndRevisionDrawings(List <String> allFullNamesDrawings, DrawingGroup group){
        for (String fullNameDrawing : allFullNamesDrawings) {
            if (fullNameDrawing.toUpperCase().contains("REW_")){
                group.addToRevisionDrawingsNames(fullNameDrawing);
            } else {
                group.addToNewDrawings(fullNameDrawing);
            }
        }
    }
}
