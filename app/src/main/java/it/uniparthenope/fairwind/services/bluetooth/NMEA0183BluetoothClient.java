package it.uniparthenope.fairwind.services.bluetooth;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.sdk.model.FairWindEvent;
import it.uniparthenope.fairwind.services.DataListener;
import it.uniparthenope.fairwind.services.DataListenerException;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;
import it.uniparthenope.fairwind.services.IDataListenerPreferences;
import it.uniparthenope.fairwind.services.NMEA0183Listener;

/**
 * Created by Luigi Giustiniani on 02/10/15.
 */
public class NMEA0183BluetoothClient extends NMEA0183Listener implements IDataListenerPreferences, Runnable {
    // The Log tag
    private static final String LOG_TAG = "NMEA_BLUETOOTH_CLIENT";

    private ExecutorService mExecutor;



    private BluetoothSocket bSocket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private String sentence;
    private String deviceConnected;
    private BluetoothAdapter ba;



    public NMEA0183BluetoothClient(){

    }


    public NMEA0183BluetoothClient(String name, String deviceConnected) {
        super(name);
        this.deviceConnected=deviceConnected;
        init();

    }

    public NMEA0183BluetoothClient(NMEA0183BluetoothClientPreferences prefs) {
        super(prefs.getName());
        this.deviceConnected=prefs.getDeviceConnected();
        init();

    }

    private void init() {

    }


    @Override
    public boolean onIsAlive() {
        if (mExecutor==null || mExecutor.isTerminated() || mExecutor.isShutdown() ) {
            return false;
        }
        return true;
    }

    @Override
    public void onStart() throws DataListenerException {

        ba = BluetoothAdapter.getDefaultAdapter();

        if (ba.isEnabled()) {

            if(deviceConnected!=null && deviceConnected.equals("No Device")==false && deviceConnected.isEmpty()==false) {

                BluetoothDevice device = ba.getRemoteDevice(deviceConnected);


                try {

                    bSocket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                    bSocket.connect();
                    bufferedReader = new BufferedReader(new InputStreamReader(bSocket.getInputStream()));
                    printWriter = new PrintWriter(new OutputStreamWriter(bSocket.getOutputStream()), true);

                } catch (IOException ioException) {
                    throw new DataListenerException(ioException);
                }

                mExecutor=Executors.newSingleThreadExecutor();
                mExecutor.submit(this);
            }

        }else{
            //attiva bluetooth
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
        return true;
    }


    @Override
    public void run() {

        while (!isDone()) {
            if (ba.isEnabled()) {
                try {
                    sentence = bufferedReader.readLine();
                    Log.d(LOG_TAG, sentence);
                    if (sentence != null && !sentence.isEmpty()) {

                        process(sentence);


                    } else {
                        setDone();
                        Log.d(LOG_TAG, "BluetoothClient Terminated");
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }

        try {
            printWriter.close();
            bufferedReader.close();
            bSocket.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }


    @Override
    public DataListener newFromDataListenerPreferences(DataListenerPreferences prefs) {
        return new NMEA0183BluetoothClient((NMEA0183BluetoothClientPreferences)prefs);
    }
}
