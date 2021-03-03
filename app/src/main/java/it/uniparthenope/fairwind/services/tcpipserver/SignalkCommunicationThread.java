package it.uniparthenope.fairwind.services.tcpipserver;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.SortedMap;

import mjson.Json;
import nz.co.fortytwo.signalk.handler.FullToDeltaConverter;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.impl.SignalKModelFactory;
import nz.co.fortytwo.signalk.util.JsonSerializer;

/**
 * Created by raffaelemontella on 29/01/16.
 */
public class SignalkCommunicationThread extends Thread {

    // The Log tag
    private static final String LOG_TAG = "SIGNALKTCPIP_THREAD";

    public final static int FULL=0;
    public final static int DELTA=1;
    private int mode=FULL;
    public int getMode() { return mode; }


    // The done flag
    boolean done = false;
    synchronized public void setAsDone() { done=true; }

    // The client socket
    private Socket socket;

    // The output buffered stream
    private PrintWriter printWriter;

    // The input buffered stream
    private BufferedReader bufferedReader;

    // The Signalk server thread reference
    private SignalkTcpIpServer signalkTcpIpServer;

    /*
    The constructor
     */
    public SignalkCommunicationThread(SignalkTcpIpServer signalkTcpIpServer, Socket socket) {

        // assign the local Signalk server thread
        this.signalkTcpIpServer = signalkTcpIpServer;

        // Assign the local socket
        this.socket = socket;

        // Prepare i/o and start the communication thread
        try {
            // Create the output buffered stream
            printWriter = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()),true);

            // Create the input buffered stream
            bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));

            // Start the thread
            start();
        } catch (IOException e) {
            //Toast.makeText(getApplicationContext(), "No Signalk communication thread!", Toast.LENGTH_LONG).show();
            Log.e(LOG_TAG,e.getMessage());
            e.printStackTrace();
        }

    }

    /*
    Send a sentence
     */
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
                    if (getMode()==SignalkCommunicationThread.DELTA) {
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

    /*
    The run method
     */
    @Override
    public void run() {
        Log.d(LOG_TAG, "Created");

        // While is not done and the current thread is not interrupted
        while (!done && !Thread.currentThread().isInterrupted()) {

            try {
                String line = bufferedReader.readLine();
                if (line != null && !line.isEmpty()) {
                    try {


                        Json json = Json.read(line);
                        SignalKModel signalkObject = SignalKModelFactory.getCleanInstance();
                        signalkObject.putAll((SortedMap) json.asMap());
                        // Process the sentence
                        signalkTcpIpServer.process(signalkObject);
                    } catch (RuntimeException e) {
                        done = true;
                        Log.e(LOG_TAG,e.getMessage());
                    }

                } else {
                    done = true;
                    Log.d(LOG_TAG, "SignalkTcpIpServer Terminated");
                }
            } catch (IOException ioException) {
                Log.e(LOG_TAG,ioException.getMessage());
                ioException.printStackTrace();
            }
        }

        // Remove the current Signalk communication thread from the vector
        synchronized (this) {
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

                // Remove the Signalk communication thread reference from the vector
                signalkTcpIpServer.getSignalkCommunicationThreads().remove(this);
            } catch (IOException e) {
                // Print the stack trace
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
