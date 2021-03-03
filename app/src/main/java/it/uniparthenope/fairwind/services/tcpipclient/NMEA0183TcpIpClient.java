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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.services.DataListener;
import it.uniparthenope.fairwind.services.DataListenerException;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;
import it.uniparthenope.fairwind.services.IDataListenerPreferences;
import it.uniparthenope.fairwind.services.NMEA0183Listener;

/**
 * Created by raffaelemontella on 09/08/15.
 */
public class NMEA0183TcpIpClient extends NMEA0183Listener implements IDataListenerPreferences, Runnable {
    // The Log tag
    private static final String LOG_TAG = "NMEA_TCPIP_CLIENT";




    private String host="192.168.1.1";
    private int port=10111;

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


    public NMEA0183TcpIpClient() {

    }

    public NMEA0183TcpIpClient(String name, String host, int port) {
        super(name);
        init();
        this.host=host;
        this.port=port;

    }

    public NMEA0183TcpIpClient(NMEA0183TcpIpClientPreferences prefs) {
        super(prefs.getName());
        init();
        this.host=prefs.getHost();
        this.port=prefs.getPort();

    }


    private void init(){

    }
    @Override
    public void onStart() throws DataListenerException {

        try {
            InetAddress inetAddress = InetAddress.getByName(host);
            try {
                socket = new Socket(inetAddress, port);
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
                mExecutor=Executors.newSingleThreadExecutor();
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
    public void send(String sSentence) throws DataListenerException {
        printWriter.println(sSentence);
    }

    @Override
    public boolean mayIUpdate() {
        return false;
    }

    @Override
    public void run() {

        while (!isDone()) {
            try {
                sentence = bufferedReader.readLine();
                if (sentence != null && !sentence.isEmpty()) {

                    process(sentence);
                    //Log.d("NMEA_TCP_CLIENT",sentence);

                } else {
                    setDone();
                    Log.d(LOG_TAG, "TcpIpClient Terminated");
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        try {
            printWriter.close();
            bufferedReader.close();
            socket.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }


    @Override
    public DataListener newFromDataListenerPreferences(DataListenerPreferences prefs) {
        return new NMEA0183TcpIpClient((NMEA0183TcpIpClientPreferences)prefs);
    }
}
