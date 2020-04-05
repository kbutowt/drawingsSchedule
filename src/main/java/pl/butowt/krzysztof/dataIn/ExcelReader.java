package pl.butowt.krzysztof.dataIn;

import lombok.NoArgsConstructor;
import lombok.extern.java.Log;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import pl.butowt.krzysztof.dataIn.model.Drawing;
import pl.butowt.krzysztof.dataIn.model.DrawingGroup;
import pl.butowt.krzysztof.dataIn.model.HeaderSettings;
import pl.butowt.krzysztof.errorHandler.*;
import pl.butowt.krzysztof.errorHandler.interfaces.Dialogs;
import pl.butowt.krzysztof.errorHandler.interfaces.Errors;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Stream;

/**
 * Find file from data are loaded to memory.
 */
@NoArgsConstructor
@Log
public class ExcelReader implements pl.butowt.krzysztof.dataIn.interfaces.FileReader {

    private Errors errorManager = new ErrorManager(ErrorStorage.getInstance());
    private Dialogs errorWindow = new DialogWindow();
    private String pathToFile;

    /**
     * Data from existing files are loaded from lat modified file.
     * @param pathToSchedulesDirectory directory where will new schedule will create. From this directory will load data from existing schedules.
     * @return path to last modified file if exist. Otherwise return path to resources and new empty file with header.
     */
    @Override
    public String findLastModifiedFile(String pathToSchedulesDirectory) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            URL resource = classLoader.getResource("HeaderTemplate.xls");
            pathToFile = resource.getPath();
        } catch (NullPointerException e) {
            log.log(Level.WARNING, "No directory is selected");
        }
        try (Stream<Path> walk = Files.walk(Paths.get(pathToSchedulesDirectory))) {
            Optional<File> file = walk.map(path -> path.getFileName().toString())
                    .filter(f -> f.startsWith("DSG Drawings Summary"))
                    .map(name -> new File(pathToSchedulesDirectory + "\\" + name))
                    .min((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
            file.ifPresent(value -> pathToFile = value.getAbsolutePath());
        } catch (IOException e) {
            errorManager.saveErrorsToStorage("Header template does not exist!");
        }
        return pathToFile;
    }

    /**
     * Read drawings from existing file.
     * @param pathToExcelFile last modified file from schedules directory. If schedule is generated first time return empty list
     * @param header contains header's params
     * @return list with drawing groups (included single drawings with all information)
     */
    @Override
    public List<DrawingGroup> readDataFromFile(String pathToExcelFile, HeaderSettings header) {
        List<DrawingGroup> groupsFromExcel = new ArrayList<>();
        File file = new File(pathToExcelFile);
        try (FileInputStream headerTemplate = new FileInputStream(new File(pathToExcelFile))) {
            Workbook drawingsSchedule = new HSSFWorkbook(headerTemplate);
            Sheet sheet = drawingsSchedule.getSheet(drawingsSchedule.getSheetName(0));
            groupsFromExcel = this.createGroupsWithScopes(sheet, header);
            for (DrawingGroup group : groupsFromExcel) {
                this.insertDataToGroup(sheet, group, file, header);
            }
        } catch (IOException ex) {
            errorManager.saveErrorsToStorage("Header template does not exist!");
        }
        return groupsFromExcel;
    }

    /**
     * Scan file and save only drawing groups names with additional information about drawings range (number of first and last row in group) for each group
     * @param sheet  current sheet which include data
     * @param headerSettings contains header's params
     * @return new groups (at this moment without drawings, only group name and range)
     */
    private List<DrawingGroup> createGroupsWithScopes(Sheet sheet, HeaderSettings headerSettings) {
        List<DrawingGroup> groups = new ArrayList<>();
        for (int rowNum = headerSettings.getSheetHeight(); rowNum < sheet.getPhysicalNumberOfRows(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row.getCell(0).getCellStyle().getFillForegroundColor() == 22) {
                DrawingGroup newGroup = new DrawingGroup();
                newGroup.setGroupName(row.getCell(0).getStringCellValue().trim());
                newGroup.setExcelStartScope(rowNum + 1);
                groups.add(newGroup);
                if (groups.size() > 1) {
                    groups.get(groups.size() - 2).setExcelFinishScope(rowNum - 1);
                }
            }
        }
        if (groups.size() > 0) {
            groups.get(groups.size() - 1).setExcelFinishScope(sheet.getPhysicalNumberOfRows() - 1);
        }
        return groups;
    }

    /**
     * Based on method 'createGroupsWithScopes' in every group are added drawings from range
     * @param sheet  current sheet which include data
     * @param group  current group which is completed
     * @param file   object from all data are loaded (lat modified Excel file)
     * @param header contains header's params
     */
    private void insertDataToGroup(Sheet sheet, DrawingGroup group, File file, HeaderSettings header) {
        SortedMap<Integer, Drawing> excelData = new TreeMap<>();
        Row row = sheet.getRow(0);
        for (int i = group.getExcelStartScope(); i <= group.getExcelFinishScope(); i++) {
            this.readLineFromExcel(excelData, row, i, sheet, file, header, group);
        }
        group.getDrawingsToExcel().putAll(excelData);
    }

    /**
     * Read single row from Excel file. Every row is represented by new object in memory.
     */
    private void readLineFromExcel(SortedMap<Integer, Drawing> excelData, Row row, int i, Sheet sheet, File readFile, HeaderSettings header, DrawingGroup group) {
        int sheetWidth = header.getSheetWidth();
        try {
            Drawing drawing = new Drawing();
            row = sheet.getRow(i);
            drawing.setRevisionVersion(row.getCell(2).getStringCellValue());
            drawing.setName(row.getCell(0).getStringCellValue());
            for (int revDataCell = 3; revDataCell <= sheetWidth; revDataCell++) {
                DataFormatter dataFormatter = new DataFormatter();
                String cellStringValue = dataFormatter.formatCellValue(row.getCell(revDataCell));
                if (cellStringValue.contains("/")) {
                    drawing.addToRevisionDate(this.changeDateFormatIfIsInCorrect(cellStringValue));
                } else if (!cellStringValue.equals("")) {
                    drawing.addToRevisionDate(cellStringValue);
                }
            }
            excelData.put((int) row.getCell(1).getNumericCellValue(), drawing);
        } catch (NumberFormatException | ParseException | ArrayIndexOutOfBoundsException e) {
            errorWindow.showErrorDialog("Reader error", "Failure during reading data from Excel file. Check file error.txt");
            errorManager.saveErrorsToStorage("Reader error from Excel file: " + readFile.getName() + " in row: " + (i + 1) + " drawing: " + row.getCell(1) + " " + row.getCell(2));
        }
    }

    /**
     * If user changes manually data in existing file during load data, date is parsed in wrong format. Then change date format is necessary.
     */
    private String changeDateFormatIfIsInCorrect(String oldDate) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
        Date date = formatter.parse(oldDate);
        SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
        return formatter1.format(date);
    }
}
