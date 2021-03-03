package it.uniparthenope.fairwind.services.tcpipclient;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.SortedMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.model.UpdateException;
import it.uniparthenope.fairwind.services.DataListener;
import it.uniparthenope.fairwind.services.DataListenerException;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;
import it.uniparthenope.fairwind.services.IDataListenerPreferences;
import mjson.Json;
import nz.co.fortytwo.signalk.handler.FullToDeltaConverter;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.JsonSerializer;

/**
 * Created by raffaelemontella on 29/01/16.
 */
public class SignalkTcpIpClient extends DataListener implements IDataListenerPreferences, Runnable{
    // The Log tag
    private static final String LOG_TAG = "SIGNALKTCPIP_CLIENT";

    public final static int FULL=0;
    public final static int DELTA=1;
    private int mode=FULL;
    public int getMode() { return mode; }




    private String host="192.168.1.1";
    private int port=55555;

    private Socket socket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private String sentence;

    private ExecutorService mExecutor;
    @Override
    public boolean onIsAlive() {
        if (mExecutor==null || mExecutor.isTerminated() || mExecutor.isShutdown() ) {
            return false;
        }
        return true;
    }


    public SignalkTcpIpClient() {
        init();
    }

    public SignalkTcpIpClient(String name, String host, int port) {
        super(name);
        init();
        this.host=host;
        this.port=port;

    }

    public SignalkTcpIpClient(SignalkTcpIpClientPreferences prefs) {
        super(prefs.getName());
        init();
        this.host=prefs.getHost();
        this.port=prefs.getPort();

    }

    private void init() {

    }


    @Override
    public long getTimeout() {
        return 1000;
    }

    @Override
    public void onStart() throws DataListenerException {
        try {
            InetAddress inetAddress = InetAddress.getByName(host);
            try {
                socket = new Socket(inetAddress, port);
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                mExecutor= Executors.newSingleThreadExecutor();
                mExecutor.submit(this);
            } catch (IOException ioException) {
                throw new DataListenerException(ioException);
            }
        } catch (UnknownHostException unknownHostException) {
            throw new DataListenerException(unknownHostException);
        }
    }

    @Override
    public void onStop() {
    }

    @Override
    public void onUpdate(PathEvent pathEvent) throws UpdateException {
        SignalKModel signalKModel=(SignalKModel) FairWindApplication.getFairWindModel();

        // To be fixed using pathEvent
        if (signalKModel.getData() != null && !signalKModel.getData().isEmpty()) {
            try {
                send(signalKModel);
            } catch (IOException e) {
                Log.e(LOG_TAG,e.getMessage());
                e.printStackTrace();
            }
        }
    }

    void send(SignalKModel signalKModel) throws IOException{
        JsonSerializer jsonSerializer=new JsonSerializer();
        FullToDeltaConverter fullToDeltaConverter=new FullToDeltaConverter();
        String jsonString;

        // Check if the thread is alive
        if (isAlive()) {

            // Check if the output buffered stream is valid
            if (printWriter!=null) {

                Json jsonFull = jsonSerializer.writeJson(signalKModel);
                if (jsonFull!=null) {
                    if (getMode()== SignalkTcpIpClient.DELTA) {
                        List<Json> jsons = fullToDeltaConverter.handle(jsonFull);
                        if (jsons != null && !jsons.isEmpty()) {
                            for (Json json : jsons) {
                                jsonString = json.toString();
                                Log.d(LOG_TAG, "S:delta " + jsonString);
                                printWriter.println(jsonString);
                            }
                        }
                    } else {
                        jsonString = jsonFull.toString();
                        Log.d(LOG_TAG, "S:full " + jsonString);
                        printWriter.println(jsonString);
                    }
                }
            }
        }
    }

    @Override
    public boolean mayIUpdate() {
        return false;
    }



    @Override
    public void run() {

        Log.d(LOG_TAG, "Created");

        // While is not done and the current thread is not interrupted
        while (!isDone() && !Thread.currentThread().isInterrupted()) {

            try {
                String line = bufferedReader.readLine();
                if (line != null && !line.isEmpty()) {
                    Json json=Json.read(line);
                    SignalKModel signalkObject= SignalKModelFactory.getCleanInstance();
                    signalkObject.putAll((SortedMap)json.asMap());
                    // Process the sentence
                    process(signalkObject);

                } else {
                    setDone();
                    Log.d(LOG_TAG, "SignalkTcpIpClient Terminated");
                }
            } catch (IOException ioException) {
                Log.d(LOG_TAG,ioException.getMessage());
            }
        }


        try {
            // Close the input buffered stream
            bufferedReader.close();
            bufferedReader = null;

            // Close tho output buffered stream
            printWriter.close();
            printWriter = null;

            // Close the socket
            socket.close();
            socket = null;

        } catch (IOException e) {
            // Print the stack trace
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public DataListener newFromDataListenerPreferences(DataListenerPreferences prefs) {
        return new SignalkTcpIpClient((SignalkTcpIpClientPreferences)prefs);
    }
}
