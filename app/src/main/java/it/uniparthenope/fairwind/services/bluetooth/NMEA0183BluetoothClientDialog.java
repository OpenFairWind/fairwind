package it.uniparthenope.fairwind.services.bluetooth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferencesDialog;

/**
 * Created by raffaelemontella on 27/07/16.
 */
public class NMEA0183BluetoothClientDialog extends DataListenerPreferencesDialog {

    public static final String LOG_TAG="...BLUETOOTH...DIALOG";


    private BluetoothSPP bt;

    @Override
    public int getLayoutId() {
        return R.layout.dialog_nmea0183bluetoothclient;
    }

    @Override
    public void onInit() {
        NMEA0183BluetoothClientPreferences nmea0183BluetoothClientPreferences=(NMEA0183BluetoothClientPreferences)dataListenerPreferences;
        final TextView tvPaireDevice=(TextView)view.findViewById(R.id.textView_paire_device);
        final TextView tvDeviceConnected=(TextView)view.findViewById(R.id.textView_device_connected);
        Button buttonChooseDevice=(Button)view.findViewById(R.id.button_choose_device);

        tvPaireDevice.setText(nmea0183BluetoothClientPreferences.getPaireDevice());
        tvDeviceConnected.setText(nmea0183BluetoothClientPreferences.getDeviceConnected());

        bt = new BluetoothSPP(view.getContext());

        if(!bt.isBluetoothAvailable()) {
            buttonChooseDevice.setEnabled(false);
            tvDeviceConnected.setEnabled(false);
            tvPaireDevice.setEnabled(false);
        }

        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {

            public void onDeviceConnected(String name, String address) {
                Log.d("Bluetooth", "Connected to " + name + " Mac Address: " + address);
                tvDeviceConnected.setText(address);
                tvPaireDevice.setText(name);
            }

            public void onDeviceDisconnected() {
                Log.d(LOG_TAG, "Connection lost");
                Toast.makeText(view.getContext(), "Connection lost", Toast.LENGTH_LONG).show();
            }

            public void onDeviceConnectionFailed() {
                Log.d(LOG_TAG, "Unable to connect");
                Toast.makeText(view.getContext(), "Unable to connect", Toast.LENGTH_LONG).show();
            }

        });

        bt.setBluetoothStateListener(new BluetoothSPP.BluetoothStateListener(){
             @Override
             public void onServiceStateChanged(int state) {

             }
        });

        buttonChooseDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (bt.isBluetoothEnabled()) {
                    Intent intent = new Intent(getPreferenceFragment().getActivity(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                } else {
                    Log.d("Bluetooth", "Bluetooth is turn off");
                    Toast.makeText(v.getContext(), "Bluetooth is turn off", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK) {
                if (!bt.isServiceAvailable()) {
                    bt.setupService();
                    bt.startService(BluetoothState.DEVICE_OTHER);
                }

                bt.connect(data);
            }
        }
    }

    @Override
    public void onFinish() {
        NMEA0183BluetoothClientPreferences nmea0183BluetoothClientPreferences=(NMEA0183BluetoothClientPreferences)dataListenerPreferences;
        TextView tvPaireDevice=(TextView)view.findViewById(R.id.textView_paire_device);
        TextView tvDeviceConnected=(TextView)view.findViewById(R.id.textView_device_connected);
        nmea0183BluetoothClientPreferences.setPaireDevice(tvPaireDevice.getText().toString());
        nmea0183BluetoothClientPreferences.setDeviceConnected(tvDeviceConnected.getText().toString());

        if (bt.isBluetoothAvailable()) {
            bt.stopService();
        }
    }
}
