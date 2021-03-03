package it.uniparthenope.fairwind.services.tcpipserver;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.services.DataListener;
import it.uniparthenope.fairwind.services.DataListenerException;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;
import it.uniparthenope.fairwind.services.IDataListenerPreferences;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;

/**
 * Created by raffaelemontella on 29/01/16.
 */
public class SignalkTcpIpServer extends DataListener implements IDataListenerPreferences, Runnable {
    // The Log tag
    private static final String LOG_TAG = "SIGNALKTCPIP_SERVER";



    // The server socket
    private ServerSocket serverSocket;

    // The vector of connected Signalk communication threads
    private SignalkCommunicationThreads signakCommunicationThreads = new SignalkCommunicationThreads();

    // The communication threads
    public SignalkCommunicationThreads getSignalkCommunicationThreads()  {return signakCommunicationThreads; }

    // The server port
    private int serverPort=55555;
    private int timeout=500;

    private ExecutorService mExecutor;

    @Override
    public boolean onIsAlive() {
        if (mExecutor==null || mExecutor.isTerminated() || mExecutor.isShutdown() ) {
            return false;
        }
        return true;
    }


    public SignalkTcpIpServer() {
        init();

    }

    // The constructor
    public SignalkTcpIpServer(String name, int serverPort) {
        super(name);
        init();
        // Assign the local server port
        this.serverPort=serverPort;
    }

    public SignalkTcpIpServer(SignalkTcpIpServerPreferences prefs) {
        super(prefs.getName());
        init();
        // Assign the local server port
        this.serverPort=prefs.getServerPort();
    }

    private void init() {

    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public void onStart() {

        mExecutor= Executors.newSingleThreadExecutor();
        mExecutor.submit(this);

    }

    @Override
    public void onStop() {

    }

    @Override
    public boolean mayIUpdate() {
        //Log.d(LOG_TAG, " N -> " + signakCommunicationThreads.size());
        if (signakCommunicationThreads.size() > 0) return true;
        return false;
    }


    public void send(SignalKModel signalKModel) throws DataListenerException {
        // For each communication thread in the vector...
        for (SignalkCommunicationThread signalkCommunicationThread : signakCommunicationThreads) {
            try{
                // Send the sentence
                signalkCommunicationThread.send(signalKModel);
            } catch (IOException e) {
                signakCommunicationThreads.remove(signalkCommunicationThread);
            }

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

                Log.d(LOG_TAG, "Connected:"+socket.getInetAddress());

                // Create a SignalK communication thread
                SignalkCommunicationThread signalkCommunicationThread =new SignalkCommunicationThread(this,socket);

                // Add the new SignalK communication thread to the vector
                signakCommunicationThreads.add(signalkCommunicationThread);

            }

            // Close the server socket
            serverSocket.close();
            serverSocket=null;

        } catch (IOException e) {
            Log.e(LOG_TAG,e.getMessage());
        }
    }

    public void onUpdate(PathEvent pathEvent) {
        SignalKModel signalKModel=(SignalKModel) FairWindApplication.getFairWindModel();

        // To be fixed
        if (signalKModel.getData() != null && !signalKModel.getData().isEmpty() && signakCommunicationThreads!=null && signakCommunicationThreads.size()>0) {
            try {
                send(signalKModel);
            } catch (DataListenerException e) {

            }
        }
    }

    @Override
    public DataListener newFromDataListenerPreferences(DataListenerPreferences prefs) {
        return new SignalkTcpIpServer((SignalkTcpIpServerPreferences)prefs);
    }

    @Override
    public boolean isLicensed() {
        return true;
    }
}
