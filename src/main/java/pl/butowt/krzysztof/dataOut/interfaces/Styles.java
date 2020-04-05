package pl.butowt.krzysztof.dataOut.interfaces;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

public interface Styles {
    CellStyle getMainHeaderStyle(Workbook workbook);
    CellStyle getExternalBorderRightWithBackground (Workbook workbook);
    CellStyle getStyleForRows(Workbook workbook);

}
