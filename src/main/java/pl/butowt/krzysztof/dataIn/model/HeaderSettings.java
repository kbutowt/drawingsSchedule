package pl.butowt.krzysztof.dataIn.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HeaderSettings {

    private int sheetHeight;

    private int sheetWidth;

    private int titleLocation;
}
