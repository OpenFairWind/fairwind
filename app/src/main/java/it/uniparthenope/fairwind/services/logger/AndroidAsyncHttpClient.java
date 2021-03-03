package it.uniparthenope.fairwind.services.logger;

import android.os.Build;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

/**
 * Created by raffaelemontella on 11/07/2017.
 */

public class AndroidAsyncHttpClient extends AsyncHttpClientBase {

    public static final String LOG_TAG="ANDROID ASYNC HTTP CLIE";

    private AsyncHttpClient client;


    public AndroidAsyncHttpClient(Uploader uploader) {
        super(uploader);

        client = new AsyncHttpClient();
        client.setTimeout(30000);
    }



    @Override
    public void onPost(String uploadUrl, File file) throws PostException {
        boolean result=false;
        RequestParams requestParams = new RequestParams();
        requestParams.add("userid", getUploader().getUserId());
        requestParams.add("deviceid", getUploader().getDeviceId());
        requestParams.add("sessionid", getSessionId());
        try {
            requestParams.put("file", file);
        } catch (FileNotFoundException ex) {
            throw new PostException(ex);
        }

        Log.d(LOG_TAG,"Uploading:" + getUploader().getUserId()+"/"+getUploader().getDeviceId()+"/"+file.getAbsolutePath());
        ResponseHandler responseHandler=new ResponseHandler();
        try {
            RequestHandle requestHandle = client.post(uploadUrl, requestParams, responseHandler);
            if (requestHandle != null) {
                Log.d(LOG_TAG, "Requested: " + uploadUrl);
                result = true;
            } else {
                Log.d(LOG_TAG, "Request failed!: " + uploadUrl);
            }
        } catch (Exception ex) {
            Log.e(LOG_TAG,ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    class ResponseHandler extends AsyncHttpResponseHandler {
        public ResponseHandler() {
            //Looper.prepareMainLooper();
        }

        public void onSuccess(int statusCode, Header[] headers, byte[] bytes) {
            String responseBody="{}";
            if (bytes!=null) {
                responseBody = new String(bytes);
            }
            Log.d(LOG_TAG,"onSuccess:");
            Log.d(LOG_TAG,"          headers ->"+headers);
            Log.d(LOG_TAG,"          resoponseBody ->"+responseBody);
            success(responseBody);
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, byte[] bytes, Throwable error) {
            String responseBody="{}";
            if (bytes!=null) {
                responseBody = new String(bytes);
            }
            Log.d(LOG_TAG,"onFailure:"+statusCode);
            Log.d(LOG_TAG,"          headers ->"+headers);
            Log.d(LOG_TAG,"          resoponseBody ->"+responseBody);
            failure(responseBody);
        }
    }

    public static int getNumberOfCores() {
        if(Build.VERSION.SDK_INT >= 17) {
            return Runtime.getRuntime().availableProcessors();
        }
        else {
            // Use saurabh64's answer
            return getNumCoresOldPhones();
        }
    }

    /**
     * Gets the number of cores available in this device, across all processors.
     * Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
     * @return The number of cores, or 1 if failed to get result
     */
    private static int getNumCoresOldPhones() {
        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                if(Pattern.matches("cpu[0-9]+", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }

        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            //Return the number of cores (virtual CPU devices)
            return files.length;
        } catch(Exception e) {
            //Default to return 1 core
            return 1;
        }
    }
}
