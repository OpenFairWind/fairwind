package it.uniparthenope.fairwind.services.udpserver;

import android.widget.EditText;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferencesDialog;

/**
 * Created by raffaelemontella on 01/05/2017.
 */

public class NMEA0183UdpServerDialog extends DataListenerPreferencesDialog {


    @Override
    public int getLayoutId() {
        return R.layout.dialog_nmea0183udpserver;
    }

    @Override
    public void onInit() {
        NMEA0183UdpServerPreferences nmea0183UdpServerPreferences=(NMEA0183UdpServerPreferences)dataListenerPreferences;
        EditText editText_port=(EditText)view.findViewById(R.id.edit_port);
        editText_port.setText(String.format("%d",nmea0183UdpServerPreferences.getServerPort()));
    }

    @Override
    public void onFinish() {
        NMEA0183UdpServerPreferences nmea0183UdpServerPreferences=(NMEA0183UdpServerPreferences)dataListenerPreferences;
        EditText editText_port=(EditText)view.findViewById(R.id.edit_port);
        nmea0183UdpServerPreferences.setServerPort(Integer.parseInt(editText_port.getText().toString()));

    }
}