package pl.butowt.krzysztof.dataOut;


import org.apache.poi.ss.usermodel.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.util.CellRangeAddress;
import pl.butowt.krzysztof.dataIn.model.HeaderSettings;
import pl.butowt.krzysztof.dataOut.interfaces.Styles;

import java.time.LocalDate;

/**
 * Formatter for schedules'es header and each row
 */
@NoArgsConstructor
@Data
public class ExcelFormatter {

    private Styles style = new BasicStyles();

    /**
     * Set schedule title located in header
     * @param sheet current sheet where schedule will create
     * @param headerSettings contains information about header params
     */
    public void setScheduleTitle(Sheet sheet, HeaderSettings headerSettings){
        int rowNum = headerSettings.getTitleLocation()-1;
        Row row = sheet.getRow(rowNum);
        LocalDate today =  LocalDate.now();

        row.getCell(0).setCellValue("Drawing schedule generated on "+ today.getYear()+"-"+this.getFormattedMonthNumber(today)+"-"+today.getDayOfMonth());
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0 ,1));
    }

    /**
     * Set main header style for chosen row and set row's height
     * @param row contains drawing group name
     * @param headerSettings contains information about header params
     * @param drawingGroupStyle style for drawing group row (created in class implements interface @Style)
     */
    public void formatDrawingGroupRow (Row row, HeaderSettings headerSettings,CellStyle drawingGroupStyle){
        row.setHeightInPoints(29.25F);
        for (int i =0; i <= headerSettings.getSheetWidth(); i++){
            row.createCell(i).setCellStyle(drawingGroupStyle);
        }

    }

    /**
     * Set style for drawings' rows
     * @param row contains drawing's information
     * @param headerSettings contains information about header params
     * @param secondaryRowStyle  style fir drawing row (created in class implements interface @Style)
     */
    public void formatForDrawingRow(Row row, HeaderSettings headerSettings, CellStyle secondaryRowStyle) {
        for (int i = 0; i < headerSettings.getSheetWidth(); i++) {
            row.createCell(i).setCellStyle(secondaryRowStyle);
        }
    }

    /**
     * Auxiliary method to format date
     * @param date current date, users receives information when schedule was generated
     * @return value of month, formatted with  prefix "0" for month from January to September
     */
     private String getFormattedMonthNumber(LocalDate date){
        String monthNumber;
        if (date.getMonthValue() <10){
            monthNumber = "0"+date.getMonthValue();
        } else {
            monthNumber = String.valueOf(date.getMonthValue());
        }
           return monthNumber;
    }
}
