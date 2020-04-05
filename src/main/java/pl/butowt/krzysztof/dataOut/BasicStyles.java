package pl.butowt.krzysztof.dataOut;

import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import pl.butowt.krzysztof.dataOut.interfaces.Styles;

/**
 * Generate basic styles for schedule.
 */
@NoArgsConstructor
public class BasicStyles implements Styles {

    /**
     * Create style for main headers (drawing groups)
     * @param workbook object which will save with new schedule
     * @return style for drawing groups
     */
    @Override
    public CellStyle getMainHeaderStyle(Workbook workbook){
        CellStyle drawingGroupStyle = workbook.createCellStyle();
        drawingGroupStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        drawingGroupStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        drawingGroupStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        drawingGroupStyle.setBorderBottom(BorderStyle.THIN);
        drawingGroupStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        drawingGroupStyle.setBorderTop(BorderStyle.THIN);
        drawingGroupStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());

        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Lucida Sans");
        font.setFontHeight((short) 260);
        drawingGroupStyle.setFont(font);
        return drawingGroupStyle;
    }

    /**
     * Create style for external border of drawings schedule
     * @param workbook object which will save with new schedule
     * @return style for external border
     */
    @Override
    public CellStyle getExternalBorderRightWithBackground (Workbook workbook){
        CellStyle externalBorderRightWithBackground= workbook.createCellStyle();
        externalBorderRightWithBackground.setBorderLeft(BorderStyle.THIN);
        externalBorderRightWithBackground.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        return  externalBorderRightWithBackground;
    }

    /**
     * Create style for row including drawings
     * @param workbook object which will save with new schedule
     * @return style for drawings' rows
     */
    @Override
    public CellStyle getStyleForRows(Workbook workbook){
        CellStyle secondaryStringRow = workbook.createCellStyle();
        secondaryStringRow.setBorderBottom(BorderStyle.THIN);
        secondaryStringRow.setAlignment(HorizontalAlignment.CENTER);
        secondaryStringRow.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        secondaryStringRow.setBorderTop(BorderStyle.THIN);
        secondaryStringRow.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        secondaryStringRow.setBorderRight(BorderStyle.THIN);
        secondaryStringRow.setRightBorderColor(IndexedColors.BLACK.getIndex());
        secondaryStringRow.setBorderLeft(BorderStyle.THIN);
        secondaryStringRow.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeight((short) 220);
        secondaryStringRow.setFont(font);
        return secondaryStringRow;
    }
}
