package it.uniparthenope.fairwind.services.udpclient;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;
import it.uniparthenope.fairwind.services.DataListener;
import it.uniparthenope.fairwind.services.DataListenerException;
import it.uniparthenope.fairwind.services.IDataListenerPreferences;
import it.uniparthenope.fairwind.services.NMEA0183Listener;

/**
 * Created by raffaelemontella on 01/05/2017.
 */

public class NMEA0183UdpClient extends NMEA0183Listener implements IDataListenerPreferences, Runnable {
    // The Log tag
    private static final String LOG_TAG = "NMEA_UDP_CLIENT";




    private String host="0.0.0.0";
    private int port=2000;

    private DatagramSocket datagramSocket;

    private ExecutorService mExecutor;
    @Override
    public boolean onIsAlive() {
        if (mExecutor==null || mExecutor.isTerminated() || mExecutor.isShutdown() ) {
            return false;
        }
        return true;
    }


    public NMEA0183UdpClient() {

    }

    public NMEA0183UdpClient(String name, String host, int port) {
        super(name);
        init();
        this.host=host;
        this.port=port;

    }

    public NMEA0183UdpClient(NMEA0183UdpClientPreferences prefs) {
        super(prefs.getName());
        init();
        this.host=prefs.getHost();
        this.port=prefs.getPort();

    }


    private void init(){

    }
    @Override
    public void onStart() throws DataListenerException {

        mExecutor= Executors.newSingleThreadExecutor();
        mExecutor.submit(this);
    }

    @Override
    public void onStop() {

    }

    @Override
    public void send(String sSentence) throws DataListenerException {
        byte[] buf=sSentence.getBytes();
        try {
            InetAddress inetAddress = InetAddress.getByName(host);
            DatagramPacket packet = new DatagramPacket(buf, buf.length, inetAddress, port);
            try {
                datagramSocket.send(packet);
            } catch (IOException ex) {
                Log.e(LOG_TAG, ex.getMessage());
            }
        } catch (UnknownHostException ex) {
            Log.e(LOG_TAG, ex.getMessage());
        }
    }

    @Override
    public boolean mayIUpdate() {
        return false;
    }

    @Override
    public void run() {

        try {
            // Create the server socket
            datagramSocket = new DatagramSocket(port);

            byte[] message = new byte[1024];

            // While the current thread is not interrupted...
            while (!isDone() && !Thread.currentThread().isInterrupted()) {

                // receive request
                DatagramPacket datagramPacket = new DatagramPacket(message, message.length);

                try {
                    // Get the connection socket
                    datagramSocket.receive(datagramPacket);

                    String sSentence = new String(message, 0, datagramPacket.getLength());

                    process(sSentence);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            // Close the datagram socket
            datagramSocket.close();
            datagramSocket=null;

        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

    }


    @Override
    public DataListener newFromDataListenerPreferences(DataListenerPreferences prefs) {
        return new NMEA0183UdpClient((NMEA0183UdpClientPreferences)prefs);
    }
}
