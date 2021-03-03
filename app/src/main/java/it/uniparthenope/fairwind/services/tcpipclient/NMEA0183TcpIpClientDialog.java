package it.uniparthenope.fairwind.services.tcpipclient;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferencesDialog;

/**
 * Created by raffaelemontella on 27/07/16.
 */
public class NMEA0183TcpIpClientDialog extends DataListenerPreferencesDialog {


    @Override
    public int getLayoutId() {
        return R.layout.dialog_nmea0183tcpipclient;
    }

    @Override
    public void onFinish() {
        NMEA0183TcpIpClientPreferences nmea0183TcpIpClientPreferences=(NMEA0183TcpIpClientPreferences) dataListenerPreferences;
        EditText editHost= (EditText) view.findViewById(R.id.edit_host);
        EditText editPort= (EditText) view.findViewById(R.id.edit_port);
        nmea0183TcpIpClientPreferences.setHost(editHost.getText().toString());
        nmea0183TcpIpClientPreferences.setPort(new Integer(editPort.getText().toString()));
    }

    @Override
    public void onInit() {
        NMEA0183TcpIpClientPreferences nmea0183TcpIpClientPreferences=(NMEA0183TcpIpClientPreferences) dataListenerPreferences;
        EditText editHost= (EditText) view.findViewById(R.id.edit_host);
        EditText editPort= (EditText) view.findViewById(R.id.edit_port);
        editHost.setText(nmea0183TcpIpClientPreferences.getHost());
        editPort.setText(String.format("%d",nmea0183TcpIpClientPreferences.getPort()));
    }
}
