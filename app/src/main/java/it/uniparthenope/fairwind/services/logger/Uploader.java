package it.uniparthenope.fairwind.services.logger;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by raffaelemontella on 28/06/2017.
 */

public class Uploader implements Runnable {
    public static final String LOG_TAG="UPLOADER";

    private long millis=5000;
    private SecureFilePacker secureFilePacker;

    private String uploadUrl;
    private String userId;
    private String deviceId;
    private String uploadPath;

    private DBHelper dbHelper;

    private String httpClientClassName;

    private ExecutorService mExecutor;

    private HashMap<String,AsyncHttpClientBase> httpClients=new HashMap<String, AsyncHttpClientBase>();

    private int maxClients;
    private int nClients;
    private int nFailures;

    private Float[] speedValues;
    private int speedIdx;
    private float averageSpeed=Float.NaN;

    private long lastSpeedUpdate;
    private long lastAverageSpeedUpdade;

    public String getUserId() { return userId; }
    public String getDeviceId() { return deviceId; }


    public Uploader(String httpClientClassName, DBHelper dbHelper, SecureFilePacker secureFilePacker, String apiUrl, String userId, String deviceId, String uploadPath, int maxClients) {
        this.httpClientClassName=httpClientClassName;
        this.secureFilePacker=secureFilePacker;
        uploadUrl=apiUrl+"/upload";
        this.userId=userId;
        this.deviceId=deviceId;
        this.dbHelper=dbHelper;
        this.uploadPath=uploadPath;

        this.maxClients=maxClients;
        if (maxClients==0) {
            maxClients=1;
        }

        nClients=maxClients/2;
        if (nClients==0) {
            nClients=1;
        }

        nFailures=0;

        speedValues=new Float[10];
        for(int i=0;i<speedValues.length;i++) {
            speedValues[i]=Float.NaN;
        }
        speedIdx=0;

        File folder = new File(uploadPath);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    /*
    public String getUploadPath() { return uploadPath; }

    */private boolean done;


    public  void upload() {
        Log.d(LOG_TAG, "ANDROID upload ***");

        moveToUpload();

        File folder = new File(uploadPath);
        File[] files = folder.listFiles();
        if (files!=null) {
            Log.d(LOG_TAG, "Files to upload in " + uploadPath + ":" + files.length);
            //UploadTaskBase uploadTaskBase =new AndroidUploadTask(httpClientClassName,uploadUrl,this);
            //uploadTaskBase.execute(files);

            //Thread thread = new Thread() {
            Handler handler=new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    File folder = new File(uploadPath);
                    File[] files = folder.listFiles();
                    Log.d(LOG_TAG, "Files to upload in "+ uploadPath+":"+ files.length);
                    for (File file:files) {

                        if (isUploading(file.getAbsolutePath()) == false) {
                            Log.d(LOG_TAG, "UPLOADTASK :" + getCurrentClients() + "/" + getAvailableClients());
                            if (getAvailableClients() - getCurrentClients() > 0) {
                                try {

                                    Class<?> clazz = Class.forName(httpClientClassName);
                                    Constructor<?> constructor = clazz.getConstructor(Uploader.class);
                                    AsyncHttpClientBase client = (AsyncHttpClientBase) constructor.newInstance(Uploader.this);
                                    client.post(uploadUrl, file);
                                    Log.d(LOG_TAG,"UPLOADTASK :"+getCurrentClients()+"/"+getAvailableClients()+" | "+file.getAbsolutePath());


                                } catch (ClassNotFoundException ex1) {
                                    throw new RuntimeException(ex1);
                                } catch (NoSuchMethodException ex2) {
                                    throw new RuntimeException(ex2);
                                } catch (IllegalAccessException ex3) {
                                    throw new RuntimeException(ex3);
                                } catch (InvocationTargetException ex4) {
                                    throw new RuntimeException(ex4);
                                } catch (InstantiationException ex5) {
                                    throw new RuntimeException(ex5);
                                }


                            } else {
                                Log.d(LOG_TAG, "No available clients");
                                break;
                            }
                        }
                    }
                }
            });
            //thread.start();
        }
    }



    public void moveToUpload() {

        // Check if there are files to be moved to upload
        ArrayList<String> filePaths=dbHelper.getAllFiles();
        Log.d(LOG_TAG, "Files to move to upload: "+ uploadPath+":"+ filePaths.size());
        for (String filePath:filePaths) {
            Log.d(LOG_TAG, ":" + filePath);
            moveToUpload(filePath);
        }

    }

    public void moveToUpload(String filePath) {

        // Check if the current file have to be moved to the upload folder
        if (filePath!=null && filePath.isEmpty()==false) {


            File source = new File(filePath);
            File destination = new File(uploadPath);
            if (source.exists()) {

                try {
                    secureFilePacker.pack(source, destination);
                } catch (IOException ex) {
                    Log.e(LOG_TAG, ex.getMessage());
                    throw new RuntimeException(ex);
                } catch (GeneralSecurityException ex) {
                    Log.e(LOG_TAG, ex.getMessage());
                    throw new RuntimeException(ex);
                }
            }

        }
        if (filePath!=null) {
            Log.d(LOG_TAG,"Deleted:" + filePath);
            dbHelper.deleteFile(filePath);
        }

    }

    public boolean add(String filePath)  {
        boolean result=false;
        if (filePath!=null && filePath.isEmpty()==false) {
            Log.d(LOG_TAG, "Inserting file:[" + filePath + "]");
            int n=dbHelper.insertFile(filePath);
            Log.d(LOG_TAG,"files:"+n);
            if (n>0) {
                result=true;
            }
        }
        return result;
    }

    @Override
    public void run() {
        while(!done) {
            try{
                Log.d(LOG_TAG,"Run");

                // Try to upload
                upload();

                // Get the current time in milliseconds
                long currentTimeMillis=System.currentTimeMillis();

                // Update the average speed each two cycles
                if (currentTimeMillis-lastAverageSpeedUpdade>2*millis) {
                    // Update the average speed only if a transfer has been done in the last cycle
                    if (currentTimeMillis-lastSpeedUpdate<millis) {
                        // Update the average speed
                        averageSpeed = getSpeed();
                    } else {
                        // Reset the average speed
                        averageSpeed=0;

                        // Reset the speed
                        for(int i=0;i<speedValues.length;i++) {
                            speedValues[i]=0.0f;
                        }
                        speedIdx=0;
                    }
                    // Mark the last time when the average speed has been evaluated
                    lastAverageSpeedUpdade = currentTimeMillis;
                }
                Log.d(LOG_TAG,"Average speed:"+averageSpeed+" byte s-1");
                Thread.sleep(millis);
            } catch (InterruptedException ex) {
                Log.e(LOG_TAG,ex.getMessage());
            }
        }
    }

    public int getAvailableClients() { return nClients; }
    public int getCurrentClients() { return httpClients.size(); }



    float getSpeed() {
        float speed=0;
        int s=0;
        for (float value:speedValues) {
            if (Float.isNaN(value)==false) {
                speed+=value;
                s++;
            }
        }
        return speed/s;
    }

    private float getAverageSpeed() {
        return averageSpeed;
    }

    public synchronized void put(String sessionId, AsyncHttpClientBase httpClient) {
        httpClients.put(sessionId,httpClient);
        Log.d(LOG_TAG,"THREADS   Added:"+sessionId+" | "+getCurrentClients()+"/"+getAvailableClients());
    }

    public boolean isUploading(String filePath) {
        if (getSessionId(filePath)!=null) {
            return true;
        }
        return false;
    }
    public String getSessionId(String filePath) {
        String result=null;
        for(AsyncHttpClientBase asyncHttpClientBase :httpClients.values()) {
            if (asyncHttpClientBase.getFile().getAbsolutePath().equals(filePath)) {
                result= asyncHttpClientBase.getSessionId();
                break;
            }
        }
        return result;
    }

    public synchronized void remove(String sessionId) {
        httpClients.remove(sessionId);
        Log.d(LOG_TAG,"THREADS Removed:"+sessionId+" | "+getCurrentClients()+"/"+getAvailableClients());

        Log.d(LOG_TAG, "UPLPERF MIL "+System.currentTimeMillis()+" FAL FCT "+getFileToTransferCount()+" ASP " + averageSpeed + " SPD " + getSpeed() + " NCL " + nClients + " ACL " + getAvailableClients() + " MCL " + maxClients + " NFAL " + nFailures);


        nFailures++;

        Log.d(LOG_TAG, "Failures:" + nClients);
        if (nFailures == nClients) {
            nFailures = 0;
            nClients--;
            if (nClients < 1) {
                nClients = 1;
            }

            Log.d(LOG_TAG, "Clients:" + nClients);
        }
    }

    public synchronized void remove(String sessionId, File file) {
        AsyncHttpClientBase httpClient=httpClients.get(sessionId);
        httpClients.remove(sessionId);
        Log.d(LOG_TAG,"THREADS Removed:"+sessionId+" | "+getCurrentClients()+"/"+getAvailableClients()+" | "+file.getAbsolutePath());



        long timeStamp=httpClient.getTimeStamp();
        long fileSize=httpClient.getFile().length();
        long currentTimeMillis=System.currentTimeMillis();



        Float deltaSecs = (currentTimeMillis - timeStamp) / 1000.0f;

        //total = total + fileSize;
        speedValues[speedIdx] = fileSize / deltaSecs;
        speedIdx++;
        if (speedIdx == speedValues.length) {
            speedIdx = 0;
        }
        lastSpeedUpdate=currentTimeMillis;
        file.delete();

        Log.d(LOG_TAG, "Average Speed:" + averageSpeed + "  bytes sec-1. Speed:" + getSpeed() + " bytes sec-1");

        Log.d(LOG_TAG, "UPLPERF MIL "+System.currentTimeMillis()+" SUC FCT "+getFileToTransferCount()+" ASP " + averageSpeed + " SPD " + getSpeed() + " NCL "+nClients+ " ACL "+getAvailableClients()+ " MCL "+maxClients+" NFAL "+nFailures);


        if (Float.isNaN(averageSpeed) == false && averageSpeed > 0) {
            float speedRate = getSpeed() / averageSpeed;
            //Log.d(LOG_TAG, "Speed rate:" + speedRate);
            nClients=calculateNumberOfClientsBySpeedRate(speedRate);

            if (nClients < 1) {
                nClients = 1;
            } else if (nClients > maxClients) {
                nClients = maxClients;
            }

        }
        Log.d(LOG_TAG, "Clients:" + nClients);
    }

    public int calculateNumberOfClientsBySpeedRate(float speedRate) {
        int nClients=this.nClients;
        if (speedRate > 1.25) {
            nClients = nClients + 2;
        } else if (speedRate > 1.125) {
            nClients = nClients + 1;
        } else if (speedRate > 1.05) {
            nClients = nClients - 1;
        } else if (speedRate < .75) {
            nClients = nClients - 2;
        } else if (speedRate < .875) {
            nClients = nClients - 1;
        } else if (speedRate < .95) {
            nClients = nClients + 1;
        }
        return nClients;
    }

    public void start() {
        Log.d(LOG_TAG,"Start");
        mExecutor= Executors.newSingleThreadExecutor();
        mExecutor.submit(this);
    }

    public int getFileToTransferCount() {
        int result=0;
        if (uploadPath!=null && uploadPath.isEmpty()==false) {
            File folder = new File(uploadPath);
            if (folder!=null) {
                File[] files = folder.listFiles();
                if (files!=null) {
                    result = files.length;
                }
            }
        }
        return result;
    }
}
