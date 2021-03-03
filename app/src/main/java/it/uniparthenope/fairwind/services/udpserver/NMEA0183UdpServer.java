package it.uniparthenope.fairwind.services.udpserver;

import android.util.Log;

import org.bouncycastle.util.IPAddress;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;
import it.uniparthenope.fairwind.services.DataListener;
import it.uniparthenope.fairwind.services.DataListenerException;
import it.uniparthenope.fairwind.services.IDataListenerPreferences;
import it.uniparthenope.fairwind.services.NMEA0183Listener;
import it.uniparthenope.fairwind.services.web.WebServer;

/**
 * Created by raffaelemontella on 01/05/2017.
 */

public class NMEA0183UdpServer extends NMEA0183Listener implements IDataListenerPreferences,Runnable {
    // The Log tag
    private static final String LOG_TAG = "NMEA_UDP_SERVER";


    private long millis=1000;

    // The server socket
    private DatagramSocket datagramSocket;


    private String host="192.168.1.255";
    // The server port
    private int port=2000;

    private ExecutorService mExecutor;

    @Override
    public boolean onIsAlive() {
        if (mExecutor==null || mExecutor.isTerminated() || mExecutor.isShutdown() ) {
            return false;
        }
        return true;
    }


    public NMEA0183UdpServer() {
        init();
    }

    // The constructor
    public NMEA0183UdpServer(String name, int serverPort) {
        super(name);

        // Assign the local server port
        this.port=serverPort;

        init();


    }

    // The constructor
    public NMEA0183UdpServer(NMEA0183UdpServerPreferences prefs) {
        super(prefs.getName());

        // Assign the local server port
        this.port=prefs.getServerPort();
        init();


    }

    private void init() {
        InetAddress inetAddress=WebServer.getIPAddress();
        byte[] ipAddress=inetAddress.getAddress();
        ipAddress[3]=(byte)0xff;
        try {
            InetAddress broadcastInetAddress = InetAddress.getByAddress(ipAddress);
            host = broadcastInetAddress.getHostAddress();
        } catch (UnknownHostException ex) {
            Log.e(LOG_TAG,ex.getMessage());
        }
        Log.d(LOG_TAG,"host:"+host+" port:"+port);
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
        return true;
    }

    public void send(String sSentence) throws DataListenerException {
        if (datagramSocket!=null) {
            try {
                InetAddress broadcast = InetAddress.getByName(host);
                byte[] message = sSentence.getBytes();
                DatagramPacket p = new DatagramPacket(message, message.length, broadcast, port);
                try {
                    datagramSocket.send(p);
                } catch (IOException e) {
                    throw new DataListenerException(e);
                }
            } catch (UnknownHostException e) {
                throw new DataListenerException(e);
            }
        }
    }

/*
    The run method
     */
    public void run() {

        try {
            datagramSocket=new DatagramSocket();

            datagramSocket.setBroadcast(true);

            while (!isDone()) {

                try {
                    Thread.sleep(millis);
                } catch (InterruptedException e) {
                    Log.e(LOG_TAG,e.getMessage());
                }

            }

            datagramSocket.close();

        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DataListener newFromDataListenerPreferences(DataListenerPreferences prefs) {
        return new NMEA0183UdpServer((NMEA0183UdpServerPreferences)prefs);
    }

}

