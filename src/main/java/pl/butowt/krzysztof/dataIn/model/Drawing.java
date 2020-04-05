package pl.butowt.krzysztof.dataIn.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Drawing {

    private Long drawingNumber;
    private String name;
    private String revisionVersion;
    private List<String> revisionDates = new ArrayList<>();

    public void addToRevisionDate(String date) {
        revisionDates.add(date);
    }
}
