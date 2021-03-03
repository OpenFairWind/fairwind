package it.uniparthenope.fairwind.captain.mydata.files;

/**
 * Created by raffaelemontella on 19/09/2017.
 */

public class FilesItem {
    private String fileName;
    private long lastModified;
    private long size;

    public FilesItem(String fileName, long lastModified, long size) {
        this.fileName=fileName;
        this.lastModified=lastModified;
        this.size=size;
    }

    public String getFileName() { return fileName; }
    public long getLastModified() { return lastModified; }
    public long getSize() { return size; }


}
