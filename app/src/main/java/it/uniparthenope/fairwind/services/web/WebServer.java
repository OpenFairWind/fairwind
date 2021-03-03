package it.uniparthenope.fairwind.services.web;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.model.UpdateException;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import it.uniparthenope.fairwind.services.DataListenerException;
import it.uniparthenope.fairwind.services.DataListener;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferences;
import it.uniparthenope.fairwind.services.IDataListenerPreferences;
import mjson.Json;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.util.ConfigConstants;
import nz.co.fortytwo.signalk.util.SignalKConstants;
import nz.co.fortytwo.signalk.util.Util;

import java.io.IOException;
import java.net.InetAddress;

import javax.jmdns.JmDNS;
import javax.jmdns.JmmDNS;
import javax.jmdns.ServiceInfo;

import static nz.co.fortytwo.signalk.util.SignalKConstants.SIGNALK_DISCOVERY;

/**
 * Created by raffaelemontella on 09/09/15.
 */
public class WebServer extends DataListener implements IDataListenerPreferences {
    // The Log tag
    private static final String LOG_TAG = "WEB_SERVER";

    public static final String VERSION="1.fairwind1";

    private JmDNS jmdns = null;

    private int portWebService=8080;
    private int portWebSocket=3000;
    private int timeout=500;

    private WebServiceWorker webServiceWorker=null;
    private WebSocketWorker webSocketWorker=null;
    private String wwwDocumentRoot="";

    public String getDocumentRoot() { return wwwDocumentRoot; }
    public WebServiceWorker getWebServiceWorker() { return webServiceWorker; }
    public WebSocketWorker getWebSocketWorker() { return webSocketWorker; }

    private boolean running=false;

    @Override
    public long getTimeout() { return timeout; }

    public WebServer() {
        init();
    }

    public WebServer(String name, int portWebService, int portWebSocket, String wwwDocumentRoot) {
        super(name);
        init();
        this.portWebService=portWebService;
        this.portWebSocket=portWebSocket;
        this.wwwDocumentRoot=wwwDocumentRoot;

    }

    public WebServer(WebServerPreferences prefs) {
        super(prefs.getName());
        init();
        this.portWebService=prefs.getPortWebService();
        this.portWebSocket=prefs.getPortWebSocket();
        this.timeout=prefs.getTimeout();
        this.wwwDocumentRoot=prefs.getWwwDocumentRoot();

    }

    private void init() {

    }



    @Override
    public boolean onIsAlive() {
        if (webServiceWorker!=null && webServiceWorker.isAlive()) {
            if (webSocketWorker!=null) {
                return webSocketWorker.isAlive();
            }
        }
        return false;
    }

    private Map<String,String> getMdnsTxt() {
        FairWindModel fairWindModel=FairWindApplication.getFairWindModel();
        Map<String,String> txtSet = new HashMap<String, String>();
        txtSet.put("path", SIGNALK_DISCOVERY);
        txtSet.put("server","signalk-server");
        txtSet.put("version",Util.getConfigProperty(ConfigConstants.VERSION));
        txtSet.put("vessel_name",fairWindModel.getName(SignalKConstants.self));
        txtSet.put("vessel_mmsi",fairWindModel.getMmsi(SignalKConstants.self));
        txtSet.put("vessel_uuid",SignalKConstants.self);
        return txtSet;
    }

    @Override
    public void onStart() throws DataListenerException {
        Log.d(LOG_TAG,"Start: "+portWebService);
        if (running==false) {

            // Register Bonjour services
            try {

                InetAddress inetAddress=WebServer.getIPAddress();
                if (inetAddress!=null) {
                    String hostName = inetAddress.getHostName();

                    // Create a JmDNS instance
                    jmdns = JmDNS.create(inetAddress, hostName);

                    jmdns.registerServiceType(SignalKConstants._SIGNALK_WS_TCP_LOCAL);
                    jmdns.registerServiceType(SignalKConstants._SIGNALK_HTTP_TCP_LOCAL);


                    ServiceInfo webInfo = ServiceInfo.create("_http._tcp.local.", "fairwind-http", portWebService, 0, 0, "path=index.html");
                    jmdns.registerService(webInfo);
                    ServiceInfo wsInfo = ServiceInfo.create(SignalKConstants._SIGNALK_WS_TCP_LOCAL, "signalk-ws", portWebSocket, 0, 0, getMdnsTxt());
                    jmdns.registerService(wsInfo);
                    ServiceInfo httpInfo = ServiceInfo.create(SignalKConstants._SIGNALK_HTTP_TCP_LOCAL, "signalk-http", portWebService, 0, 0, getMdnsTxt());
                    jmdns.registerService(httpInfo);
                }

            } catch (UnknownHostException e) {
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();

            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }



            webServiceWorker=new WebServiceWorker(this,portWebService);
            if (webServiceWorker!=null) {
                try {
                    Log.d(LOG_TAG,"Start web service worker: "+portWebService+" -> starting...");
                    webServiceWorker.start(-5);


                    Log.d(LOG_TAG,"Start: "+portWebService+" -> ...started");
                } catch (IOException e) {
                    throw new DataListenerException("Troubles while starting webServiceWorker",e);
                }

                webSocketWorker=new WebSocketWorker(this,portWebSocket);
                if (webSocketWorker!=null) {
                    try {
                        int timeOut = FairWindApplication.getFairWindModel().getPreferences().getConfigPropertyInt((getName() + "_timeout"),-1);
                        Log.d(LOG_TAG,"Start web socket worker: "+portWebSocket+" -> starting... ("+timeOut+")");
                        webSocketWorker.start(-5);
                        Log.d(LOG_TAG,"Start: "+portWebSocket+" -> ...started");

                        running=true;
                    } catch (IOException e) {
                        throw new DataListenerException("Troubles while starting webSocketWorker", e);
                    }
                }
            }



        } else {
            Log.d(LOG_TAG,"Start: "+portWebService+" -> Already started");
        }


    }

    @Override
    public void onStop() {

        if (webServiceWorker!=null) {
            if (webServiceWorker.isAlive()) {
                webServiceWorker.stop();
                webServiceWorker=null;
            }
        }

        if (webSocketWorker!=null) {
            if (webSocketWorker.isAlive()) {
                webSocketWorker.stop();
                webSocketWorker=null;
            }
        }

        // Check if Bonjour is active
        if (jmdns!=null) {
            // Unregister all services
            jmdns.unregisterAllServices();
        }
        running=false;
    }

    @Override
    public void onUpdate(PathEvent pathEvent) throws UpdateException {
        // Update all connected sockets if any
        Log.d(LOG_TAG,"update");
        webSocketWorker.update(pathEvent);
    }

    @Override
    public boolean mayIUpdate() {
        return true;
    }

    //@Override
    //public boolean isOutput() { return true; }


    //public Context getContext() {
    //    return fairWindModel.getApplicationContext();
        //return getIntentService().getApplicationContext();
    //}
    public String get404HTML() { return getTextFromFile(R.raw.page404).toString(); }
    public String get500HTML() {
        return getTextFromFile(R.raw.page500).toString();
    }

    public String getTextFromFile(int resourceId) {
        // The InputStream opens the resourceId and sends it to the buffer
        //InputStream is = this.getIntentService().getResources().openRawResource(resourceId);
        InputStream is = FairWindApplication.getInstance().getResources().openRawResource(resourceId);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder stringBuilder=new StringBuilder();
        String readLine = null;

        try {
            // While the BufferedReader readLine is not null
            while ((readLine = br.readLine()) != null) {
                stringBuilder.append(readLine+"\n");
            }

            // Close the InputStream and BufferedReader
            is.close();
            br.close();

        } catch (IOException e) {
            Log.d(LOG_TAG,e.getMessage());
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }


    public Json getEndpoints() {
        Log.d(LOG_TAG,"getEndpoints");

        Json v=Json.object();
        v.set(SignalKConstants.version,VERSION);
        v.set(SignalKConstants.restUrl,"http://"+getIPAddress()+":"+portWebService+SignalKConstants.SIGNALK_API);
        v.set(SignalKConstants.websocketUrl,"ws://"+getIPAddress()+":"+portWebSocket+SignalKConstants.SIGNALK_WS);

        Json endpoints=Json.object().set("endpoints",Json.object().set("v1",v));
        Log.d(LOG_TAG,"getEndpoints ->" + endpoints.toString());
        return endpoints;
    }



    public static InetAddress getIPAddress() { return getIPAddress(true);}

    /**
     * Get IP address from first non-localhost interface
     * @param useIPv4  true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    public static InetAddress getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = isIPv4Address(sAddr);
                        if (useIPv4) {
                            if (isIPv4)
                                return InetAddress.getByName(sAddr);
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                return InetAddress.getByName(delim<0 ? sAddr : sAddr.substring(0, delim));
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) { Log.e("WEB WORKER",ex.getMessage());} // for now eat exceptions
        return null;
    }

    private static final Pattern IPV4_PATTERN =
            Pattern.compile(
                    "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");

    private static final Pattern IPV6_STD_PATTERN =
            Pattern.compile(
                    "^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");

    private static final Pattern IPV6_HEX_COMPRESSED_PATTERN =
            Pattern.compile(
                    "^((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)$");

    public static boolean isIPv4Address(final String input) {
        return IPV4_PATTERN.matcher(input).matches();
    }

    public static boolean isIPv6StdAddress(final String input) {
        return IPV6_STD_PATTERN.matcher(input).matches();
    }

    public static boolean isIPv6HexCompressedAddress(final String input) {
        return IPV6_HEX_COMPRESSED_PATTERN.matcher(input).matches();
    }

    public static boolean isIPv6Address(final String input) {
        return isIPv6StdAddress(input) || isIPv6HexCompressedAddress(input);
    }

    @Override
    public DataListener newFromDataListenerPreferences(DataListenerPreferences prefs) {
        return new WebServer((WebServerPreferences)prefs);
    }
    @Override
    public boolean isLicensed() {
        return true;
    }
}

