package it.uniparthenope.fairwind.services.logger;

import android.os.Build;
import android.util.Log;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Param;
import com.ning.http.client.Response;
import com.ning.http.client.multipart.FilePart;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by raffaelemontella on 07/11/2017.
 */

public class AsyncHttpClient extends AsyncHttpClientBase {

    public static final String LOG_TAG="ANDROID ASYNC HTTP CLIE";

    private com.ning.http.client.AsyncHttpClient client;


    public AsyncHttpClient(Uploader uploader) {
        super(uploader);
        client = new  com.ning.http.client.AsyncHttpClient();
    }

    @Override
    public void onPost(String uploadUrl, File file) throws PostException {

        com.ning.http.client.AsyncHttpClient.BoundRequestBuilder boundRequestBuilder=client.preparePost(uploadUrl);
        boundRequestBuilder.setRequestTimeout(3000);

        ArrayList<Param> queryParams=new ArrayList<>();
        queryParams.add(new Param("userid", getUploader().getUserId()));
        queryParams.add(new Param("deviceid", getUploader().getDeviceId()));
        queryParams.add(new Param("sessionid",getSessionId()));

        FilePart part = new FilePart("file", file);
        boundRequestBuilder.addBodyPart(part);
        boundRequestBuilder.addQueryParams(queryParams);

        boundRequestBuilder.execute(new AsyncCompletionHandler<Response>() {

            @Override
            public Response onCompleted(Response response) throws Exception {
                int status = response.getStatusCode();
                String responseBody = response.getResponseBody();
                Log.d(LOG_TAG, "THREADS  "+ getSessionId() +"| onSuccess:"+ responseBody);
                //Log.d(LOG_TAG, "          headers ->" + response.getHeaders());
                if (status >= 200 && status < 300) {
                    success(responseBody);
                } else {
                    failure(responseBody);
                }
                return response;
            }

            @Override
            public void onThrowable(Throwable t) {
                // Something wrong happened.
                String message=t.getMessage();
                if (message.contains("EAI_NODATA")) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        Log.e(LOG_TAG,e.getMessage());
                    }
                }
                String responseBody="{\"status\":\"fail\",\"message\":\""+t.getMessage()+"\"}";
                Log.d(LOG_TAG, "THREADS "+ getSessionId() +"| onThrowable: " + responseBody);
                failure(responseBody);
            }
        });

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
