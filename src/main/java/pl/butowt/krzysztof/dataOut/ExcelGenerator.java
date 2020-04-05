package pl.butowt.krzysztof.dataOut;

import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import pl.butowt.krzysztof.dataIn.model.Drawing;
import pl.butowt.krzysztof.dataIn.model.DrawingGroup;
import pl.butowt.krzysztof.dataIn.model.HeaderSettings;
import pl.butowt.krzysztof.dataOut.interfaces.FileGenerator;
import pl.butowt.krzysztof.dataOut.interfaces.Styles;
import pl.butowt.krzysztof.errorHandler.DialogWindow;
import pl.butowt.krzysztof.errorHandler.interfaces.Dialogs;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.SortedMap;
import java.util.logging.Level;

/**
 *Create new schedule based on information from earlier schedules and new data
 */
@NoArgsConstructor
@Log
public class ExcelGenerator implements FileGenerator {

    private Dialogs errorWindow = new DialogWindow();
    private String excelFormat = ".xls";
    private static final String EXCEL_PREFIX = "DSG Drawings Summary ";
    private Workbook drawingsSchedule;
    private ExcelFormatter formatter = new ExcelFormatter();
    private Styles style = new BasicStyles();

    /**
     * Create and save new schedule. Styles have to be created only once and reused in each row,
     * otherwise excel memory is overload what makes in case large amount of data some part of them couldn't load
     * @param drawingsGroup list with drawing groups contains drawings from earlier generated schedules and new data from chosen directory
     * @param pathToExcelsDirectory path chosen by user where schedule should save
     * @param header contains information about header params
     * @return path to saved schedule location
     * @throws IOException
     */
    @Override
    public String createNewSchedule(List<DrawingGroup> drawingsGroup, String pathToExcelsDirectory, HeaderSettings header) throws IOException {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            URL resource = classLoader.getResource("HeaderTemplate"+excelFormat);
            String pathToFile;
            if (resource != null) {
                pathToFile = resource.getPath();
            } else {
                throw new NullPointerException();
            }
            FileInputStream headerTemplate = new FileInputStream(new File(pathToFile));
            drawingsSchedule = new HSSFWorkbook(headerTemplate);
            Sheet sheet = drawingsSchedule.getSheet(drawingsSchedule.getSheetName(0));
            formatter.setScheduleTitle(sheet, header);

            int rowNum = header.getSheetHeight();
            CellStyle drawingGroupStyle = style.getMainHeaderStyle(drawingsSchedule);
            CellStyle externalBorderRightWithBackground = style.getExternalBorderRightWithBackground(drawingsSchedule);
            CellStyle secondaryRowStyle = style.getStyleForRows(drawingsSchedule);


            for (DrawingGroup drawingGroup : drawingsGroup) {
                Row primaryRow = sheet.createRow(rowNum++);
                formatter.formatDrawingGroupRow(primaryRow, header, drawingGroupStyle);
                primaryRow.createCell(header.getSheetWidth()).setCellStyle(externalBorderRightWithBackground);
                primaryRow.getCell(0).setCellValue(drawingGroup.getGroupName());
                sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 1, 2));
                SortedMap<Integer, Drawing> map = drawingGroup.getDrawingsToExcel();
                for (SortedMap.Entry<Integer, Drawing> entry : map.entrySet()) {
                    Drawing drawing = entry.getValue();
                    Row secondaryRow = sheet.createRow(rowNum++);
                    formatter.formatForDrawingRow(secondaryRow, header, secondaryRowStyle);
                    secondaryRow.getCell(0).setCellValue(drawing.getName());
                    secondaryRow.getCell(1).setCellValue(entry.getKey());
                    secondaryRow.getCell(2).setCellValue(drawing.getRevisionVersion());
                    for (int i = 0; i < drawing.getRevisionDates().size(); i++) {
                        secondaryRow.getCell(3 + i).setCellValue(drawing.getRevisionDates().get(i));
                    }
                }
            }
        } catch (FileNotFoundException e) {
            errorWindow.showErrorDialog("Error!", "Header file doesn't exist or has invalid name.");
        } catch (NullPointerException e) {
            log.log(Level.WARNING, "Wrong directory is selected or header does't exist");
        }
        String directoryToSavedFile = pathToExcelsDirectory + "\\" + createExcelName(pathToExcelsDirectory);
        FileOutputStream fileInSchedulesDirectory = new FileOutputStream(pathToExcelsDirectory + "\\" + createExcelName(pathToExcelsDirectory));
        this.saveToFile(fileInSchedulesDirectory, drawingsSchedule);

        return directoryToSavedFile;
    }

    /**
     * Create new name for file. Default name is @EXCEL_PREFIX + creation date. In case another schedule in the same date is added another number for version.
     * That makes files aren't overwritten.
     * @param directoryPath path to directory where schedule should generates
     * @return name of file with generated schedule
     */
    private String createExcelName(String directoryPath) {
        LocalDate today = LocalDate.now();
        String localExcelPrefix = EXCEL_PREFIX + today.toString();
        String nameToSave = localExcelPrefix + excelFormat;
        int fileVersion = 0;
        if (checkIfExist(directoryPath, nameToSave)) {
            do {
                fileVersion++;
                nameToSave = localExcelPrefix + "-(" + fileVersion + ")" + excelFormat;
            } while (checkIfExist(directoryPath, nameToSave));
        }
        return nameToSave;
    }

    /**
     * Check if file already exist. Auxiliary method for created name and check another version of file
     * @param directoryPath path to directory where schedule should generates
     * @param excelName another version of name which is checked for saving possibility
     * @return return information is it name is safe to save (avoid overwritten)
     */
    private boolean checkIfExist(String directoryPath, String excelName) {
        File file = new File(directoryPath + "\\" + excelName);
        return file.exists();
    }

    private void saveToFile(FileOutputStream outputStream, Workbook drawingsSchedule) throws IOException {
        try {
            drawingsSchedule.write(outputStream);
        } catch (NullPointerException e) {
            log.log(Level.WARNING, "File doesn't exist");
        } finally {
            outputStream.close();
        }
    }
}
