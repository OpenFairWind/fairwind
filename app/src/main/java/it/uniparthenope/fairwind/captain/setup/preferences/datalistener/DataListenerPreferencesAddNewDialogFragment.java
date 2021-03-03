package it.uniparthenope.fairwind.captain.setup.preferences.datalistener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;
import it.uniparthenope.fairwind.services.rules.RulesListenerPreferences;
import it.uniparthenope.fairwind.services.bluetooth.NMEA0183BluetoothClientPreferences;
import it.uniparthenope.fairwind.services.file.NMEA0183PlayerPreferences;
import it.uniparthenope.fairwind.services.logger.LoggerListenerPreferences;
import it.uniparthenope.fairwind.services.signalkclient.SignalkWebSocketClientPreferences;
import it.uniparthenope.fairwind.services.tcpipclient.NMEA0183TcpIpClientPreferences;
import it.uniparthenope.fairwind.services.tcpipclient.SignalkTcpIpClientPreferences;
import it.uniparthenope.fairwind.services.tcpipserver.NMEA0183TcpIpServerPreferences;
import it.uniparthenope.fairwind.services.tcpipserver.SignalkTcpIpServerPreferences;
import it.uniparthenope.fairwind.services.udpclient.NMEA0183UdpClientPreferences;
import it.uniparthenope.fairwind.services.udpserver.NMEA0183UdpServerPreferences;
import it.uniparthenope.fairwind.services.usbserial.NMEA0183UsbSerialClientPreferences;
import it.uniparthenope.fairwind.services.web.WebServerPreferences;

/**
 * Created by raffaelemontella on 17/10/16.
 */

public class DataListenerPreferencesAddNewDialogFragment extends DialogFragment {
    public static final String LOG_TAG="...ADDNEW...FRAGMENT";

    private DataListenerPreferencesFragment dataListenerPreferencesFragment;

    public DataListenerPreferencesAddNewDialogFragment() {

    }

    public void setDataListenerPreferencesFragment(DataListenerPreferencesFragment dataListenerPreferencesFragment) {
        this.dataListenerPreferencesFragment=dataListenerPreferencesFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("New Data Listener Service");
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                builder.getContext(),
                android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("NMEA 0183 Player");
        arrayAdapter.add("NMEA 0183 USB/Serial");
        arrayAdapter.add("NMEA 0183 Bluetooth");
        arrayAdapter.add("NMEA 0183 TcpIp Client");
        arrayAdapter.add("NMEA 0183 Udp Client");
        arrayAdapter.add("SignalK Web Socket Client");
        arrayAdapter.add("SignalK TcpIp Client");
        arrayAdapter.add("NMEA 0183 TcpIp Server");
        arrayAdapter.add("NMEA 0183 Udp Server");
        arrayAdapter.add("SignalK TcpIp Server");
        arrayAdapter.add("Web Server");
        arrayAdapter.add("LoggerListener");
        arrayAdapter.add("Alerts");


        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DataListenerPreferences dataListenerPreferences = null;
                switch (which) {

                    case 0:
                        dataListenerPreferences=new NMEA0183PlayerPreferences();
                        break;

                    case 1:
                        dataListenerPreferences=new NMEA0183UsbSerialClientPreferences();
                        break;

                    case 2:
                        dataListenerPreferences=new NMEA0183BluetoothClientPreferences();
                        break;

                    case 3:
                        dataListenerPreferences=new NMEA0183TcpIpClientPreferences();
                        break;

                    case 4:
                        dataListenerPreferences=new NMEA0183UdpClientPreferences();
                        break;

                    case 5:
                        dataListenerPreferences=new SignalkWebSocketClientPreferences();
                        break;

                    case 6:
                        dataListenerPreferences=new SignalkTcpIpClientPreferences();
                        break;

                    case 7:
                        dataListenerPreferences=new NMEA0183TcpIpServerPreferences();
                        break;

                    case 8:
                        dataListenerPreferences=new NMEA0183UdpServerPreferences();
                        break;

                    case 9:
                        dataListenerPreferences=new SignalkTcpIpServerPreferences();
                        break;

                    case 10:
                        dataListenerPreferences=new WebServerPreferences();
                        break;

                    case 11:
                        dataListenerPreferences=new LoggerListenerPreferences();
                        break;

                    case 12:
                        dataListenerPreferences=new RulesListenerPreferences();
                        break;



                }
                if (dataListenerPreferences!=null) {
                    Log.d(LOG_TAG,"New className: " + dataListenerPreferences.getClassName());
                    dataListenerPreferencesFragment.addItem(dataListenerPreferences);

                }
                dialog.dismiss();
            }
        });


        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }});

        // Create the AlertDialog object and return it
        return builder.create();

    }
}
