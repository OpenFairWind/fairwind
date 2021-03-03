package it.uniparthenope.fairwind.services.signalkclient;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import javax.jmdns.ServiceInfo;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferencesDialog;

/**
 * Created by raffaelemontella on 26/07/16.
 */
public class SignalkWebSocketClientDialog extends DataListenerPreferencesDialog {
    public final static String LOG_TAG="SIGNALKWEB...DIALOG";

    @Override
    public int getLayoutId() {
        return R.layout.dialog_signalkwebsocketclient;
    }

    @Override
    public void onFinish() {
        SignalkWebSocketClientPreferences signalkWebSocketClientPreferences=(SignalkWebSocketClientPreferences)dataListenerPreferences;
        EditText editUri= (EditText) view.findViewById(R.id.edit_uri);
        EditText editUuid= (EditText) view.findViewById(R.id.edit_uuid);
        signalkWebSocketClientPreferences.setUri(editUri.getText().toString());
        signalkWebSocketClientPreferences.setUuid(editUuid.getText().toString());

    }

    @Override
    public void onInit() {
        SignalkWebSocketClientPreferences signalkWebSocketClientPreferences=(SignalkWebSocketClientPreferences)dataListenerPreferences;
        EditText editUri= (EditText) view.findViewById(R.id.edit_uri);
        EditText editUuid= (EditText) view.findViewById(R.id.edit_uuid);
        editUri.setText(signalkWebSocketClientPreferences.getUri());
        editUuid.setText(signalkWebSocketClientPreferences.getSignalKUuid());
        Button btnDiscovery=(Button)view.findViewById(R.id.btn_discovery);
        btnDiscovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignalkWebSocketClientDiscoveryDialogFragment signalkWebSocketClientDiscoveryDialogFragment=new SignalkWebSocketClientDiscoveryDialogFragment();
                signalkWebSocketClientDiscoveryDialogFragment.setSignalkWebSocketClientDialog(SignalkWebSocketClientDialog.this);
                signalkWebSocketClientDiscoveryDialogFragment.show(getFragmentManager(),"signalkWebSocketClientDiscoveryDialogFragment");
            }
        });
    }

    public void setUri(ServiceInfo serviceInfo) {
        Log.d(LOG_TAG,"Service info:"+serviceInfo);
        EditText editUri= (EditText) view.findViewById(R.id.edit_uri);
        //addrs=serviceInfo.getInet6Addresses();

        String[] urls=serviceInfo.getURLs();
        if (urls!=null && urls.length>0) {
            String uri = urls[0];
            uri=uri.replace("http://","ws://");
            if (uri.endsWith("signalk")) {
                uri+="/v1/stream";
            }
            editUri.setText(uri);
        }

    }
}