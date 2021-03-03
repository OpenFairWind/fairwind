package it.uniparthenope.fairwind.services.tcpipserver;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

import it.uniparthenope.fairwind.FairWindApplication;

/**
 * Created by raffaelemontella on 21/08/15.
 */
public class NMEA0183CommunicationThread extends Thread {
    // The Log tag
    private static final String LOG_TAG = "NMEA_TCPIP_COMM_THREAD";


    // The done flag
    boolean done = false;
    synchronized public void setAsDone() { done=true; }

    // The client socket
    private Socket socket;

    // The output buffered stream
    private PrintWriter printWriter;

    // The input buffered stream
    private BufferedReader bufferedReader;

    // The nmea server thread reference
    private NMEA0183TcpIpServer nmeaTcpIpServer;

    /*
    The constructor
     */
    public NMEA0183CommunicationThread(NMEA0183TcpIpServer nmeaTcpIpServer, Socket socket) {

        // assign the local nmea server thread
        this.nmeaTcpIpServer = nmeaTcpIpServer;

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
            //Toast.makeText(getApplicationContext(), "No NMEA communication thread!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    /*
    Send a sentence
     */
    void send(String sentence) {

        // Check if the thread is alive
        if (isAlive()) {

            // Check if the output buffered stream is valid
            if (printWriter!=null) {

                // Print the sentence
                printWriter.println(sentence);
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
                String sentence = bufferedReader.readLine();
                if (sentence != null && !sentence.isEmpty()) {

                    // Process the sentence
                    nmeaTcpIpServer.process(sentence);

                } else {
                    done = true;
                    Log.d(LOG_TAG, "TcpIpServer Terminated");
                }
            } catch (IOException ioException) {
                Log.d(LOG_TAG,ioException.getMessage());
            }
        }

        // Remove the current NMEA communication thread from the vector
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

                // Remove the NMEA communication thread reference from the vector
                nmeaTcpIpServer.getNMEACommunicationThreads().remove(this);
            } catch (IOException e) {
                // Print the stack trace
                Log.e(LOG_TAG, e.getMessage());
            }
        }
    }
}

