package it.uniparthenope.fairwind.services.tcpipserver;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.uniparthenope.fairwind.services.DataListener;
import it.uniparthenope.fairwind.services.DataListenerException;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;
import it.uniparthenope.fairwind.services.IDataListenerPreferences;
import it.uniparthenope.fairwind.services.NMEA0183Listener;

/**
 * Created by raffaelemontella on 06/08/15.
 */
public class NMEA0183TcpIpServer extends NMEA0183Listener implements IDataListenerPreferences,Runnable {
    // The Log tag
    private static final String LOG_TAG = "NMEA_TCPIP_SERVER";



    // The server socket
    private ServerSocket serverSocket;

    // The vector of connected NMEA communication threads
    private NMEA0183CommunicationThreads nmea0183CommunicationThreads;

    // The communication threads
    public NMEA0183CommunicationThreads getNMEACommunicationThreads()  {return nmea0183CommunicationThreads; }

    // The server port
    int serverPort;

    private ExecutorService mExecutor;

    @Override
    public boolean onIsAlive() {
        if (mExecutor==null || mExecutor.isTerminated() || mExecutor.isShutdown() ) {
            return false;
        }
        return true;
    }


    public NMEA0183TcpIpServer() {
        serverPort=10110;
        init();
    }

    // The constructor
    public NMEA0183TcpIpServer(String name, int serverPort) {
        super(name);

        // Assign the local server port
        this.serverPort=serverPort;
        init();


    }

    // The constructor
    public NMEA0183TcpIpServer(NMEA0183TcpIpServerPreferences prefs) {
        super(prefs.getName());

        // Assign the local server port
        this.serverPort=prefs.getServerPort();
        init();


    }

    private void init() {
        // Create the vector instance
        nmea0183CommunicationThreads = new NMEA0183CommunicationThreads();
    }

    @Override
    public void onStart() {
        mExecutor=Executors.newSingleThreadExecutor();
        mExecutor.submit(this);
    }

    @Override
    public void onStop() {

    }

    @Override
    public boolean mayIUpdate() {
        Log.d(LOG_TAG, " N -> " + nmea0183CommunicationThreads.size());
        if (nmea0183CommunicationThreads.size() > 0) return true;
        return false;
    }

    public void send(String sSentence) throws DataListenerException {
        // For each communication thread in the vector...
        for (NMEA0183CommunicationThread nmea0183CommunicationThread : nmea0183CommunicationThreads) {

            // Send the sentence
            nmea0183CommunicationThread.send(sSentence);

        }
    }


    /*
    The run method
     */
    public void run() {


        try {
            // Create the server socket
            serverSocket = new ServerSocket(serverPort);

            // While the current thread is not interrupted...
            while (!isDone() && !Thread.currentThread().isInterrupted()) {
                Log.d(LOG_TAG, "Waiting for connection...");

                // Get the connection socket
                Socket socket=serverSocket.accept();

                // Create a NMEA communication thread
                NMEA0183CommunicationThread nmea0183CommunicationThread =new NMEA0183CommunicationThread(this,socket);

                // Add the new NMEA communication thread to the vector
                nmea0183CommunicationThreads.add(nmea0183CommunicationThread);

            }

            // Close the server socket
            serverSocket.close();
            serverSocket=null;

        } catch (IOException e) {
            Log.e(LOG_TAG,e.getMessage());
        }
    }

    @Override
    public DataListener newFromDataListenerPreferences(DataListenerPreferences prefs) {
        return new NMEA0183TcpIpServer((NMEA0183TcpIpServerPreferences)prefs);
    }

    @Override
    public boolean isLicensed() {
        return true;
    }
}

