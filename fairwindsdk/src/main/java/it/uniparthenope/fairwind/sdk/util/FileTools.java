package it.uniparthenope.fairwind.sdk.util;

import android.app.Application;
import android.content.res.AssetManager;
import android.util.Log;

import org.apache.commons.io.FilenameUtils;
import org.osmdroid.tileprovider.modules.IArchiveFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by raffaelemontella on 11/07/16.
 */
public class FileTools {
    public static final String LOG_TAG="FileTools";

    public static void listFiles(Set<String> setFileArchives, File directoryPath, final String ext, boolean b) {
        setFileArchives.clear();

        File[] files = directoryPath.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(ext);
            }
        });
        for(File file:files) {
            setFileArchives.add(file.getAbsolutePath());
        }
    }

    public static void unzipFromAssets(Application application, String fileName, String extStorageDirectory) throws IOException {
        Log.d(LOG_TAG,"Unzipping from assets -> "+fileName);

        File file=new File(fileName);
        String dirName= FilenameUtils.removeExtension(file.getName());
        File destDir=new File(extStorageDirectory+File.separator+dirName);
        if (destDir.exists() == false) {
            InputStream inputStream = application.getAssets().open(fileName);
            Decompress decompress = new Decompress(inputStream, extStorageDirectory+ File.separator);
            decompress.unzip();
        }
    }

    /**
     * -- Check to see if the sdCard is mounted and create a directory w/in it
     * ========================================================================
     **/
    /*
    public static void copyDirectory(Application application, String root, String extStorageDirectory) {
        Log.d(LOG_TAG,"copyDirectory from assets: "+root);

        String destRoot=extStorageDirectory + File.separator +root+ File.separator ;

        File txtDirectory = new File(destRoot);
        txtDirectory.mkdirs();// Have the object build the directory
        // structure, if needed.
        copyAssets(application, root,destRoot); // Then run the method to copy the file.

    }
    */

    /**
     * -- Copy the file from the assets folder to the sdCard
     * ===========================================================
     **/
    public static void copyAssets(Application application, String sourceRoot, String destRoot) {


        String[] files = null;
        try {
            files = application.getAssets().list(sourceRoot);
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
        for (int i = 0; i < files.length; i++) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = application.getAssets().open(sourceRoot+"/"+files[i]);
                out = new FileOutputStream(destRoot+"/" + files[i]);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch (Exception e) {
                Log.e("tag", e.getMessage());
            }
        }
    }

    public static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    public static void assetsCopyDirectory(Application application,String path,String extStorageDirectory ) {
        AssetManager assetManager = application.getAssets();
        String assets[] = null;
        try {
            assets = assetManager.list(path);
            if (assets.length == 0) {
                assetsCopyFile(application,path,extStorageDirectory );
            } else {
                String fullPath = extStorageDirectory + File.separator + path;
                File dir = new File(fullPath);
                if (!dir.exists())
                    dir.mkdir();
                for (int i = 0; i < assets.length; ++i) {
                    assetsCopyDirectory(application,path + File.separator + assets[i],extStorageDirectory);
                }
            }
        } catch (IOException ex) {
            Log.e(LOG_TAG, "I/O Exception", ex);
        }
    }

    public static void assetsCopyFile(Application application,String filename, String extStorageDirectory ) {
        AssetManager assetManager = application.getAssets();

        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(filename);
            String newFileName = extStorageDirectory + File.separator + filename;
            out = new FileOutputStream(newFileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
        }

    }


}
