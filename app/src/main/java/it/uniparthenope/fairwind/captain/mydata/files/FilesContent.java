package it.uniparthenope.fairwind.captain.mydata.files;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.uniparthenope.fairwind.FairWindApplication;

/**
 * Created by raffaelemontella on 19/09/2017.
 */

public class FilesContent {
    public static final String LOG_TAG="FilesContent";

    /**
     * An array of sample (dummy) items.
     */
    private List<FilesItem> items = new ArrayList<FilesItem>();
    public List<FilesItem> getItems() {
        return items;
    }

    private String current;


    public FilesContent() {


        setCurrent(getBase());

    }

    public String getBase() {
        File externalFilesDir= FairWindApplication.getInstance().getExternalFilesDir(null);
        String base=externalFilesDir.getAbsolutePath()+File.separator;
        return base;
    }
    public void setCurrent(String current) {
        clear();

        this.current=current.replace("//","/");;


        File currentPath = new File(current);

        if (currentPath.exists()==false) {
            currentPath=new File(getBase());
        }

        if (current.equals(getBase())==false) {
            addItem(new FilesItem("../", 0, 0));
        }

        File[] dirs = currentPath.listFiles(new FileFilter() {
            @Override public boolean accept(File file) {
                return (file.isDirectory() && file.canRead());
            }
        });

        File[] files = currentPath.listFiles(new FileFilter() {
            @Override public boolean accept(File file) {
                if (!file.isDirectory()) {
                    if (!file.canRead()) {
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        });

        Arrays.sort(dirs);
        Arrays.sort(files);

        if (dirs != null && dirs.length > 0) {
            for (File dir : dirs) {
                String fileName = dir.getName()+File.separator;
                addItem(new FilesItem(fileName, dir.lastModified(), dir.length()));
            }
        }


        if (files != null && files.length > 0) {
            for (File file : files) {
                addItem(new FilesItem(file.getName(), file.lastModified(), file.length()));
            }
        }
    }



    public void addItem(FilesItem item) {
        items.add(item);
    }

    public void clear() {
        items.clear();
    }

    public String getCurrent() {
        return current;
    }

}
