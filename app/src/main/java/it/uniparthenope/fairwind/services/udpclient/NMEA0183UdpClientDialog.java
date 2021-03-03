package it.uniparthenope.fairwind.services.udpclient;

import android.widget.EditText;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferencesDialog;

/**
 * Created by raffaelemontella on 01/05/2017.
 */

public class NMEA0183UdpClientDialog extends DataListenerPreferencesDialog {


    @Override
    public int getLayoutId() {
        return R.layout.dialog_nmea0183tcpipclient;
    }

    @Override
    public void onFinish() {
        NMEA0183UdpClientPreferences nmea0183UdpClientPreferences=(NMEA0183UdpClientPreferences) dataListenerPreferences;
        EditText editHost= (EditText) view.findViewById(R.id.edit_host);
        EditText editPort= (EditText) view.findViewById(R.id.edit_port);
        nmea0183UdpClientPreferences.setHost(editHost.getText().toString());
        nmea0183UdpClientPreferences.setPort(new Integer(editPort.getText().toString()));
    }

    @Override
    public void onInit() {
        NMEA0183UdpClientPreferences nmea0183UdpClientPreferences=(NMEA0183UdpClientPreferences) dataListenerPreferences;
        EditText editHost= (EditText) view.findViewById(R.id.edit_host);
        EditText editPort= (EditText) view.findViewById(R.id.edit_port);
        editHost.setText(nmea0183UdpClientPreferences.getHost());
        editPort.setText(String.format("%d",nmea0183UdpClientPreferences.getPort()));
    }
}
