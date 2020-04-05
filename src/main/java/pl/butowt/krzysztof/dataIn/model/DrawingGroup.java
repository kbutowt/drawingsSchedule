package pl.butowt.krzysztof.dataIn.model;

import lombok.*;

import java.util.*;

/**
 * Group represents each directory with files.
 */

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode

public class DrawingGroup implements Comparable<DrawingGroup> {
    private String groupName;
    private String pathName;
    private List<String> newDrawingsNames = new ArrayList<>();
    private List<String> revisionDrawingsNames = new ArrayList<>();
    private SortedMap<Integer, Drawing> drawingsToExcel = new TreeMap<>();
    private int excelStartScope;
    private int excelFinishScope;

    public DrawingGroup(String groupName, String pathName) {
        this.groupName = groupName;
        this.pathName = pathName;
    }

    public void addToNewDrawings(String newDrawingName) {
        newDrawingsNames.add(newDrawingName);
    }

    public void addToRevisionDrawingsNames(String revisionDrawing) {
        revisionDrawingsNames.add(revisionDrawing);
    }

    public void addToDrawingsToExcel(SortedMap<Integer, Drawing> map) {
        drawingsToExcel.putAll(map);
    }

    @Override
    public int compareTo(DrawingGroup group) {
        if (group == null) {
            return 1;
        }
        int ret = 0;
        if (groupName == null && group.groupName != null) {
            return -1;
        } else if (groupName != null && group.groupName == null) {
            return 1;
        } else if (groupName != null && group.groupName != null) {
            ret = groupName.compareTo(group.groupName);
        }
        if (ret == 0) {
            ret = drawingsToExcel.size() - group.drawingsToExcel.size();
        }
        return ret;
    }
}
