package it.uniparthenope.fairwind.services.logger;

import java.util.ArrayList;

/**
 * Created by raffaelemontella on 11/07/2017.
 */

public interface DBHelper {
    public Integer insertFile (String path);
    public Integer deleteFile (String path);
    public ArrayList<String> getAllFiles();
    public int numberOfRows();

}
