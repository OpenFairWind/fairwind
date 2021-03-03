package it.uniparthenope.fairwind.services.usbserial;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.sqlite.SQLiteOutOfMemoryException;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.preference.PreferenceManager;
import android.util.Log;


import com.hoho.android.usbserial.driver.CdcAcmSerialDriver;
import com.hoho.android.usbserial.driver.ProbeTable;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.services.IDataListenerPreferences;
import it.uniparthenope.fairwind.services.DataListenerException;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;
import it.uniparthenope.fairwind.services.LookoutService;
import it.uniparthenope.fairwind.services.DataListener;
import it.uniparthenope.fairwind.services.NMEA0183Listener;

/**
 * Created by raffaelemontella on 06/08/15.
 */
public class NMEA0183UsbSerialClient extends NMEA0183Listener implements IDataListenerPreferences,SerialInputOutputManager.Listener {

    private static final String LOG_TAG = "NMEA_USBSERIAL_CLIENT";


    private SerialInputOutputManager serialInputOutputManager;



    //private int serialDriverIndex=0;
    //private int serialPortIndex=0;
    private String deviceInfo="";
    private int baudRate=115200;
    private int stopBits=UsbSerialPort.STOPBITS_1;
    private int dataBits=UsbSerialPort.DATABITS_8;
    private int parity=UsbSerialPort.PARITY_NONE;
    private boolean requestToSend=false;
    private boolean dataTerminalReady=false;

    // The current working sentence
    private String sentence = "";

    // State machine code
    private int state = 0;

    // Number of received checksum digits
    private int nck = 0;

    // Last correctly received sentence
    //

    private ExecutorService mExecutor;
    @Override
    public boolean onIsAlive() {
        if (mExecutor==null || mExecutor.isTerminated() || mExecutor.isShutdown() ) {
            return false;
        }
        return true;
    }

    public NMEA0183UsbSerialClient() {
        init();
    }

    /*
    The constructor
     */
    public NMEA0183UsbSerialClient(String name, String deviceInfo, int baudRate, int dataBits, int stopBits, int parityBit, boolean dataTerminalReady, boolean requestToSend) {

        super(name);
        this.deviceInfo=deviceInfo;
        this.baudRate=baudRate;
        this.parity=parityBit;
        this.dataBits=dataBits;
        this.stopBits=stopBits;
        this.dataTerminalReady=dataTerminalReady;
        this.requestToSend=requestToSend;
        init();
    }

    public NMEA0183UsbSerialClient(NMEA0183UsbSerialClientPreferences prefs) {

        super(prefs);

        this.deviceInfo=prefs.getDeviceInfo();
        this.baudRate=prefs.getBaudRate();
        this.parity=prefs.getParity();
        this.dataBits=prefs.getDataBits();
        this.stopBits=prefs.getStopBits();
        this.dataTerminalReady=prefs.getDataTerminalReady();
        this.requestToSend=prefs.getRequestToSend();
        init();
    }

    private void init() {
        type="NMEA0183 Usb Serial Client";
    }


    public void send(String sentence) {
        if (serialInputOutputManager!=null) {
            serialInputOutputManager.writeAsync((sentence + "\r\n").getBytes());
        }
    }

    public DataListener newFromDataListenerPreferences(DataListenerPreferences prefs) {
        Log.d(LOG_TAG, "newFromDataListenerPreferences");
        return new NMEA0183UsbSerialClient((NMEA0183UsbSerialClientPreferences) prefs);
    }


    @Override
    public void onStop() {

        if (serialInputOutputManager!=null) {
            serialInputOutputManager.stop();
            serialInputOutputManager=null;
        }

        mExecutor.shutdown();
        mExecutor=null;
        Log.d(LOG_TAG, "Stopped");
    }

    @Override
    public boolean mayIUpdate() {
        return true;
    }

    @Override
    public void onStart() throws DataListenerException {
        Log.d(LOG_TAG, "OnStart");

        UsbSerialPort serialPort = null;

        UsbManager usbManager;
        List<UsbSerialDriver> availableDrivers;
        List<UsbSerialPort> serialPorts;

        LookoutService lookoutService=FairWindApplication.getFairWindModel().getLookoutService();

        // Get the usb manager
        usbManager = (UsbManager) lookoutService.getSystemService(Context.USB_SERVICE);

        // Get a list of all usb serial drivers

        ProbeTable customTable = UsbSerialProber.getDefaultProbeTable();
        customTable.addProduct(0x16d0, 0x0b03, CdcAcmSerialDriver.class);
        UsbSerialProber prober = new UsbSerialProber(customTable);
        List<UsbSerialDriver> drivers = prober.findAllDrivers(usbManager);

        // Check if at least one driver is available
        if (!drivers.isEmpty()) {

            // Get the first serial driver
            UsbSerialDriver driver = null;

            for (UsbSerialDriver usbSerialDriver:drivers) {


                String deviceInfo = usbSerialDriver.getDevice().getVendorId()+" "+usbSerialDriver.getDevice().getProductId();
                if (deviceInfo.equalsIgnoreCase(this.deviceInfo)) {
                    driver=usbSerialDriver;
                    break;
                }
            }

            if (driver !=null) {
                try {

                    // Get the permissions for the usb
                    PendingIntent mPermissionIntent = PendingIntent.getBroadcast(lookoutService.getApplicationContext(), 0, new Intent("com.android.example.USB_PERMISSION"), 0);

                    // Request the permission to the device
                    usbManager.requestPermission(driver.getDevice(), mPermissionIntent);

                    int count = 0;
                    UsbDeviceConnection connection = null;
                    do {
                        try {
                            // Open the device and get the connection
                            connection = usbManager.openDevice(driver.getDevice());

                        } catch (Exception e) {
                            Log.e(LOG_TAG,e.getMessage());
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException interruptedException) {
                            }
                            count++;
                        }
                    } while (connection == null && count < 10);
                    if (connection == null) {
                        throw new DataListenerException("No serial permissions.");
                    }

                    // Check if the connection is valid
                    if (connection != null) {

                        // Get a list of the available serial ports
                        serialPorts = driver.getPorts();

                        // Get the first serial port
                        serialPort = serialPorts.get(0);

                        try {
                            // Open the serial port connection
                            serialPort.open(connection);


                            // Set serial parameters
                            serialPort.setParameters(baudRate, dataBits, stopBits, parity);

                            // Set flow control
                            serialPort.setDTR(dataTerminalReady);
                            serialPort.setRTS(requestToSend);

                            serialInputOutputManager = new SerialInputOutputManager(serialPort, this);
                            try {
                                mExecutor = Executors.newSingleThreadExecutor();
                                mExecutor.submit(serialInputOutputManager);

                                Log.d(LOG_TAG, "Started:" + driver.getDevice());
                            } catch (OutOfMemoryError oomError) {
                                System.gc();
                                throw new DataListenerException(oomError.getMessage());
                            }
                        } catch (IOException ioException) {
                            throw new DataListenerException(ioException);
                        }

                    } else {
                        Log.d(LOG_TAG, "No device");
                        throw new DataListenerException("No serial device");
                    }
                } catch (SecurityException securityException) {
                    throw new DataListenerException(securityException);
                }
            }
        } else {
            Log.d(LOG_TAG, "No serial driver");
            throw new DataListenerException("No serial driver");
        }
    }




    @Override
    public void onRunError(Exception e) {
        Log.d(LOG_TAG, "Runner stopped:"+e.getMessage());
        if (serialInputOutputManager!=null) {
            serialInputOutputManager.stop();
            serialInputOutputManager=null;
        }
        if (isAlive()) {
            mExecutor.shutdown();
            mExecutor=null;
        }
    }

    @Override
    public void onNewData(final byte[] data) {


        // Create a string from the byte just received
        final String message = new String(data);

        Log.d(LOG_TAG, "New data:"+message);

        // For each character of the string...
        for (int i = 0; i < message.length(); i++) {

            // Check if it is the beginning of a nmea sentence
            if (message.charAt(i) == '$') {

                // Clean the sentence
                resetSentence();

                // Set the status to 1
                state = 1;



            } else
                // Check if it is the and of the sentence data (before the checksum)
                if (message.charAt(i) == '*') {

                    // Set the status to 2
                    state = 2;
                }



            // Check if the current status is "in sentence"
            if (state == 1) {

                // Add a character to the current sentence
                sentence += message.charAt(i);

            } else
                // Check if the status is "in checksum"
                if (state == 2) {

                    // Add the character to the sentence
                    sentence += message.charAt(i);

                    // Increase the number of checksum digits
                    nck++;

                    // Check if the number of digits is 2
                    if (nck > 2) {

                        // Process the sentence
                        process(sentence);

                        // Clean the sentence
                        resetSentence();
                    }
                }
        }
    }

    /*
    Reset the current working sentence
     */
    void resetSentence() {
        // Clean the sentence
        sentence = "";

        // Clean the working sentence
        sentence = "";

        // Set to 0 the number of received checksum digits
        nck = 0;
    }
}

