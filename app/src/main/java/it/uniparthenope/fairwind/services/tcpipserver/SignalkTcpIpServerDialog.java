package it.uniparthenope.fairwind.services.tcpipserver;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferencesDialog;

/**
 * Created by raffaelemontella on 27/07/16.
 */
public class SignalkTcpIpServerDialog extends DataListenerPreferencesDialog {



    @Override
    public int getLayoutId() {
        return R.layout.dialog_signalktcpipserver;
    }

    @Override
    public void onInit() {
        SignalkTcpIpServerPreferences signalkTcpIpServerPreferences=(SignalkTcpIpServerPreferences)dataListenerPreferences;
        EditText editText_port=(EditText)view.findViewById(R.id.edit_port);
        editText_port.setText(String.format("%d",signalkTcpIpServerPreferences.getServerPort()));
    }

    @Override
    public void onFinish() {
        SignalkTcpIpServerPreferences signalkTcpIpServerPreferences=(SignalkTcpIpServerPreferences)dataListenerPreferences;
        EditText editText_port=(EditText)view.findViewById(R.id.edit_port);
        signalkTcpIpServerPreferences.setServerPort(Integer.parseInt(editText_port.getText().toString()));

    }
}
