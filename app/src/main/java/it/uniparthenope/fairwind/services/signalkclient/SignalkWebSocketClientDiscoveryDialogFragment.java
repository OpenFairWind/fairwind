package it.uniparthenope.fairwind.services.signalkclient;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.services.web.WebServer;
import nz.co.fortytwo.signalk.util.SignalKConstants;

/**
 * Created by raffaelemontella on 10/12/2016.
 */

public class SignalkWebSocketClientDiscoveryDialogFragment extends DialogFragment implements Runnable  {

    public final static String LOG_TAG="SIGNALK...DISCOVERY";

    private SignalkWebSocketClientDialog signalkWebSocketClientDialog;

    private DiscoveryThread discoveryThread;
    private Executor executor;

    private ServiceInfoAdapter arrayAdapter;
    private ArrayList<ServiceInfo> serviceInfos;


    private Handler handler;



    @Override
    public void run() {
        arrayAdapter.notifyDataSetChanged();
    }

    class DiscoveryThread implements Runnable, ServiceListener {

        public static final int ACTION_CANCEL=0;
        public static final int ACTION_DISMISS=1;

        private boolean done=false;
        private JmDNS jmdns=null;
        private android.net.wifi.WifiManager.MulticastLock lock;
        private AlertDialog dialog;
        private int action=ACTION_CANCEL;

        public DiscoveryThread(AlertDialog dialog) {
            this.dialog=dialog;

        }

        @Override
        public void serviceAdded(ServiceEvent event) {
            Log.d(LOG_TAG, "Service added:" + event.getInfo());
            jmdns.requestServiceInfo(event.getType(), event.getName(), 1000);
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            Log.d(LOG_TAG, "Service removed:" + event.getInfo());
            serviceInfos.remove(event.getInfo());
            handler.post(SignalkWebSocketClientDiscoveryDialogFragment.this);

        }

        @Override
        public void serviceResolved(ServiceEvent event) {
            Log.d(LOG_TAG, "Service resolved:" + event.getInfo());
            serviceInfos.add(event.getInfo());
            handler.post(SignalkWebSocketClientDiscoveryDialogFragment.this);
        }

        @Override
        public void run() {
            try {
                android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) FairWindApplication.getInstance().getApplicationContext().getSystemService(android.content.Context.WIFI_SERVICE);

                /*Allows an application to receive
                Wifi Multicast packets. Normally the Wifi stack
                filters out packets not explicitly addressed to
                this device. Acquiring a MulticastLock will cause
                the stack to receive packets addressed to multicast
                addresses. Processing these extra packets can
                cause a noticable battery drain and should be
                disabled when not needed. */
                lock = wifi.createMulticastLock(getClass().getSimpleName());

                /*Controls whether this is a reference-counted or
                non-reference- counted MulticastLock.
                Reference-counted MulticastLocks keep track of the
                number of calls to acquire() and release(), and
                only stop the reception of multicast packets when
                every call to acquire() has been balanced with a
                call to release(). Non-reference- counted
                MulticastLocks allow the reception of multicast
                packets whenever acquire() is called and stop
                accepting multicast packets whenever release() is
                called.*/
                lock.setReferenceCounted(false);

                // Get the ip address
                /*
                String[] ipAddress=WebServer.getIPAddress().split("[.]");
                byte[] bytes=new byte[4];
                for (int i=0;i<ipAddress.length;i++) {
                    bytes[i] = Byte.parseByte(ipAddress[i]);
                }

                InetAddress inetAddress=InetAddress.getByAddress(bytes);
                */
                InetAddress inetAddress=WebServer.getIPAddress();
                String hostName=inetAddress.getHostName();

                // Acquire the lock
                lock.acquire();

                // Create a JmDNS instance
                jmdns = JmDNS.create(inetAddress,hostName);

                // Add a service listener
                jmdns.addServiceListener(SignalKConstants._SIGNALK_WS_TCP_LOCAL, this);

                while(!done) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {

                    }
                }
                jmdns.removeServiceListener(SignalKConstants._SIGNALK_WS_TCP_LOCAL, this);
                //Release the lock
                lock.release();

                switch(action) {
                    case ACTION_CANCEL:
                        dialog.cancel();
                        break;
                    case ACTION_DISMISS:
                        dialog.dismiss();
                        break;
                }

            } catch (UnknownHostException ex) {
                Log.d(LOG_TAG,ex.getMessage());
            } catch (IOException ex) {
                Log.d(LOG_TAG,ex.getMessage());
            }
        }

        public void stop(int action) {
            this.action=action;
            done=true;
        }
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        handler=new Handler();


        // Use the Builder class for convenient dialog construction
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("SignalK WebSocket services");
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        serviceInfos=new ArrayList<ServiceInfo>();
        arrayAdapter = new ServiceInfoAdapter(
                builder.getContext(),
                android.R.layout.select_dialog_singlechoice,
                serviceInfos);



        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (signalkWebSocketClientDialog!=null) {
                    //Log.d(LOG_TAG,"New className: " + mapPreferences.getClassName());
                    signalkWebSocketClientDialog.setUri(arrayAdapter.getItem(which));

                }
                discoveryThread.stop(DiscoveryThread.ACTION_DISMISS);
                //dialog.dismiss();
            }
        });


        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                discoveryThread.stop(DiscoveryThread.ACTION_CANCEL);
                //dialog.cancel();
            }});


        // Create the AlertDialog object and return it
        //return builder.create();

        AlertDialog dialog=builder.create();
        discoveryThread=new DiscoveryThread(dialog);
        executor= Executors.newSingleThreadExecutor();
        executor.execute(discoveryThread);
        return dialog;

    }

    public void setSignalkWebSocketClientDialog(SignalkWebSocketClientDialog signalkWebSocketClientDialog) {
        this.signalkWebSocketClientDialog = signalkWebSocketClientDialog;
    }
}
