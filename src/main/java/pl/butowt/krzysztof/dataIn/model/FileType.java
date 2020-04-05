package pl.butowt.krzysztof.dataIn.model;

public enum FileType {
    DWG(".dwg"),
    PDF(".pdf");

    private final String fileType;

    FileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileType() {
        return fileType;
    }
}
