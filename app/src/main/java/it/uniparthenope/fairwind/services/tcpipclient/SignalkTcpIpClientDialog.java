package it.uniparthenope.fairwind.services.tcpipclient;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferencesDialog;

/**
 * Created by raffaelemontella on 27/07/16.
 */
public class SignalkTcpIpClientDialog extends DataListenerPreferencesDialog {



    @Override
    public int getLayoutId() {
        return R.layout.dialog_signaktcpipclient;
    }

    @Override
    public void onFinish() {
        SignalkTcpIpClientPreferences signalkTcpIpClientPreferences=(SignalkTcpIpClientPreferences) dataListenerPreferences;
        EditText editHost= (EditText)view.findViewById(R.id.edit_host);
        EditText editPort= (EditText)view.findViewById(R.id.edit_port);
        signalkTcpIpClientPreferences.setHost(editHost.getText().toString());
        signalkTcpIpClientPreferences.setPort(new Integer(editPort.getText().toString()));
    }

    @Override
    public void onInit() {
        SignalkTcpIpClientPreferences signalkTcpIpClientPreferences=(SignalkTcpIpClientPreferences) dataListenerPreferences;
        EditText editHost= (EditText) view.findViewById(R.id.edit_host);
        EditText editPort= (EditText) view.findViewById(R.id.edit_port);
        editHost.setText(signalkTcpIpClientPreferences.getHost());
        editPort.setText(String.format("%d",signalkTcpIpClientPreferences.getPort()));
    }
}
