package it.uniparthenope.fairwind.services.tcpipserver;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferencesDialog;

/**
 * Created by raffaelemontella on 27/07/16.
 */
public class NMEA0183TcpIpServerDialog extends DataListenerPreferencesDialog {


    @Override
    public int getLayoutId() {
        return R.layout.dialog_nmea0183tcpipserver;
    }

    @Override
    public void onInit() {
        NMEA0183TcpIpServerPreferences nmea0183TcpIpServerPreferences=(NMEA0183TcpIpServerPreferences)dataListenerPreferences;
        EditText editText_port=(EditText)view.findViewById(R.id.edit_port);
        editText_port.setText(String.format("%d",nmea0183TcpIpServerPreferences.getServerPort()));
    }

    @Override
    public void onFinish() {
        NMEA0183TcpIpServerPreferences nmea0183TcpIpServerPreferences=(NMEA0183TcpIpServerPreferences)dataListenerPreferences;
        EditText editText_port=(EditText)view.findViewById(R.id.edit_port);
        nmea0183TcpIpServerPreferences.setServerPort(Integer.parseInt(editText_port.getText().toString()));

    }
}