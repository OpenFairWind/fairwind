package it.uniparthenope.fairwind.services.usbserial;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import com.hoho.android.usbserial.driver.CdcAcmSerialDriver;
import com.hoho.android.usbserial.driver.ProbeTable;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.HexDump;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.sdk.captain.setup.datalistener.DataListenerPreferencesDialog;

/**
 * Created by raffaelemontella on 27/07/16.
 */
public class NMEA0183UsbSerialClientDialog extends DataListenerPreferencesDialog {

    public static final String LOG_TAG="NMEA0183_USB_SERIAL";
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private String mDeviceInfo="";

    private UsbManager mUsbManager;
    private TextView mProgressBarTitle;
    private ProgressBar mProgressBar;

    private static final int MESSAGE_REFRESH = 101;
    private static final long REFRESH_TIMEOUT_MILLIS = 5000;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_REFRESH:
                    refreshDeviceList();
                    mHandler.sendEmptyMessageDelayed(MESSAGE_REFRESH, REFRESH_TIMEOUT_MILLIS);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }

    };

    private List<UsbSerialPort> mEntries = new ArrayList<UsbSerialPort>();
    private ArrayAdapter<UsbSerialPort> mAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.dialog_nmea0183usbserialclient;
    }


    @Override
    public void onInit() {
        NMEA0183UsbSerialClientPreferences nmea0183UsbSerialClientPreferences=(NMEA0183UsbSerialClientPreferences)dataListenerPreferences;
        mDeviceInfo=nmea0183UsbSerialClientPreferences.getDeviceInfo();
        setBaudRate(nmea0183UsbSerialClientPreferences.getBaudRate());
        setStopBits(nmea0183UsbSerialClientPreferences.getStopBits());
        setDataBits(nmea0183UsbSerialClientPreferences.getDataBits());
        setParity(nmea0183UsbSerialClientPreferences.getParity());
        setDataTerminalReady(nmea0183UsbSerialClientPreferences.getDataTerminalReady());
        setRequestToSend(nmea0183UsbSerialClientPreferences.getRequestToSend());

        mUsbManager = (UsbManager) getActivity().getSystemService(Context.USB_SERVICE);

        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mProgressBarTitle = (TextView) view.findViewById(R.id.progressBarTitle);

        final ListView listView = (ListView) view.findViewById(R.id.deviceList);

        mAdapter = new ArrayAdapter<UsbSerialPort>(getActivity(),
                android.R.layout.simple_expandable_list_item_2, mEntries) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                final TwoLineListItem row;
                if (convertView == null){
                    final LayoutInflater inflater =
                            (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    row = (TwoLineListItem) inflater.inflate(android.R.layout.simple_list_item_activated_2, null);
                } else {
                    row = (TwoLineListItem) convertView;
                }

                final UsbSerialPort port = mEntries.get(position);
                final UsbSerialDriver driver = port.getDriver();
                final UsbDevice device = driver.getDevice();

                final String title = String.format("Vendor %s Product %s",
                        HexDump.toHexString((short) device.getVendorId()),
                        HexDump.toHexString((short) device.getProductId()));
                row.getText1().setText(title);

                String deviceAsString=driver.getDevice().toString();

                int pos1, pos2;
                pos1=deviceAsString.indexOf("mManufacturerName")+"mManufacturerName".length()+1;
                pos2=deviceAsString.indexOf(",",pos1);
                String manufacturerName=deviceAsString.substring(pos1,pos2);

                pos1=deviceAsString.indexOf("mProductName")+"mProductName".length()+1;
                pos2=deviceAsString.indexOf(",",pos1);
                String productName=deviceAsString.substring(pos1,pos2);

                final String subtitle = manufacturerName+", "+productName+" ("+driver.getClass().getSimpleName()+")";
                row.getText2().setText(subtitle);

                return row;
            }

        };
        listView.setAdapter(mAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(LOG_TAG, "Pressed item " + position);
                mDeviceInfo="";
                if (position >= mEntries.size()) {
                    Log.w(LOG_TAG, "Illegal position.");
                    return;
                }
                listView.setItemChecked(position,true);

                if (position >=0 && position<mEntries.size()) {
                    final UsbSerialPort port = mEntries.get(position);
                    if (port != null) {
                        mDeviceInfo = port.getDriver().getDevice().getVendorId()+" "+port.getDriver().getDevice().getProductId();
                        Log.d(LOG_TAG, "Device Info:" + mDeviceInfo);
                    }
                }



            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        mHandler.sendEmptyMessage(MESSAGE_REFRESH);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandler.removeMessages(MESSAGE_REFRESH);
    }

    private void refreshDeviceList() {
        showProgressBar();

        new AsyncTask<Void, Void, List<UsbSerialPort>>() {
            @Override
            protected List<UsbSerialPort> doInBackground(Void... params) {
                Log.d(LOG_TAG, "Refreshing device list ...");
                SystemClock.sleep(1000);

                ProbeTable customTable = UsbSerialProber.getDefaultProbeTable();
                customTable.addProduct(0x16d0, 0x0b03, CdcAcmSerialDriver.class);
                UsbSerialProber prober = new UsbSerialProber(customTable);
                List<UsbSerialDriver> drivers = prober.findAllDrivers(mUsbManager);



                HashMap<String,UsbDevice> devices=mUsbManager.getDeviceList();
                Log.d(LOG_TAG,"Devices:"+devices);
                Log.d(LOG_TAG,"Drivers:"+drivers.size());

                final List<UsbSerialPort> result = new ArrayList<UsbSerialPort>();
                for (final UsbSerialDriver driver : drivers) {
                    Log.d(LOG_TAG,"Device name:"+driver.getDevice().getDeviceName());
                    final List<UsbSerialPort> ports = driver.getPorts();
                    Log.d(LOG_TAG, String.format("+ %s: %s port%s",
                            driver, Integer.valueOf(ports.size()), ports.size() == 1 ? "" : "s"));
                    result.addAll(ports);
                }

                return result;
            }

            @Override
            protected void onPostExecute(List<UsbSerialPort> result) {
                mEntries.clear();
                mEntries.addAll(result);
                mAdapter.notifyDataSetChanged();
                mProgressBarTitle.setText(
                        String.format("%s device(s) found",Integer.valueOf(mEntries.size())));
                hideProgressBar();
                Log.d(LOG_TAG, "Done refreshing, " + mEntries.size() + " entries found.");

                for(int position=0;position<mEntries.size();position++) {
                    UsbDevice device=mEntries.get(position).getDriver().getDevice();
                    String deviceInfo = device.getVendorId()+" "+device.getProductId();
                    if (deviceInfo.equals(mDeviceInfo) ) {
                        ListView listView = (ListView) view.findViewById(R.id.deviceList);
                        listView.setItemChecked(position,true);
                        break;
                    }
                }

            }

        }.execute((Void) null);
    }

    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBarTitle.setText("Refreshing");
    }

    private void hideProgressBar() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onFinish() {
        NMEA0183UsbSerialClientPreferences nmea0183UsbSerialClientPreferences=(NMEA0183UsbSerialClientPreferences)dataListenerPreferences;
        nmea0183UsbSerialClientPreferences.setDeviceInfo(mDeviceInfo);
        nmea0183UsbSerialClientPreferences.setBaudRate(getBaudRate());
        nmea0183UsbSerialClientPreferences.setStopBits(getStopBits());
        nmea0183UsbSerialClientPreferences.setDataBits(getDataBits());
        nmea0183UsbSerialClientPreferences.setParity(getParity());
        nmea0183UsbSerialClientPreferences.setDataTerminalReady(getDataTerminalReady());
        nmea0183UsbSerialClientPreferences.setRequestToSend(getRequestToSend());
    }



    private void setBaudRate(int baudRate) {
        Spinner spinner_baudrate=(Spinner)view.findViewById(R.id.spinner_baudrate);
        int position=0;
        SpinnerAdapter spinnerAdapter=spinner_baudrate.getAdapter();
        for (int i=0;i<spinnerAdapter.getCount();i++) {
            if (Integer.parseInt((String)spinnerAdapter.getItem(i))==baudRate) {
                position=i;
            }
        }
        spinner_baudrate.setSelection(position);
    }

    private int getBaudRate() {
        Spinner spinner_baudrate=(Spinner)view.findViewById(R.id.spinner_baudrate);
        return Integer.parseInt((String)spinner_baudrate.getSelectedItem());
    }

    private void setStopBits(int stopBits) {
        RadioGroup radioGroup=(RadioGroup)view.findViewById(R.id.radiogroup_stopbits);
        int id=0;
        switch (stopBits) {
            case UsbSerialPort.STOPBITS_1:
                id=R.id.radio_stopbits1;
                break;
            case UsbSerialPort.STOPBITS_1_5:
                id=R.id.radio_stopbits15;
                break;
            case UsbSerialPort.STOPBITS_2:
                id=R.id.radio_stopbits2;
                break;
        }
        radioGroup.check(id);
    }

    private int getStopBits() {
        RadioGroup radioGroup=(RadioGroup)view.findViewById(R.id.radiogroup_stopbits);

        int stopBits=0;
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.radio_stopbits1:
                stopBits=UsbSerialPort.STOPBITS_1;
                break;
            case R.id.radio_stopbits15:
                stopBits=UsbSerialPort.STOPBITS_1_5;
                break;
            case R.id.radio_stopbits2:
                stopBits=UsbSerialPort.STOPBITS_2;
                break;
        }
        return stopBits;
    }

    private void setDataBits(int stopBits) {
        RadioGroup radioGroup=(RadioGroup)view.findViewById(R.id.radiogroup_databits);
        int id=0;
        switch (stopBits) {
            case UsbSerialPort.DATABITS_5:
                id=R.id.radio_databits5;
                break;
            case UsbSerialPort.DATABITS_6:
                id=R.id.radio_databits6;
                break;
            case UsbSerialPort.DATABITS_7:
                id=R.id.radio_databits7;
                break;
            case UsbSerialPort.DATABITS_8:
                id=R.id.radio_databits8;
                break;
        }
        radioGroup.check(id);
    }

    private int getDataBits() {
        RadioGroup radioGroup=(RadioGroup)view.findViewById(R.id.radiogroup_databits);

        int dataBits=0;
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.radio_databits5:
                dataBits=UsbSerialPort.DATABITS_5;
                break;
            case R.id.radio_databits6:
                dataBits=UsbSerialPort.DATABITS_6;
                break;
            case R.id.radio_databits7:
                dataBits=UsbSerialPort.DATABITS_7;
                break;
            case R.id.radio_databits8:
                dataBits=UsbSerialPort.DATABITS_8;
                break;
        }
        return dataBits;
    }

    private void setParity(int parity) {
        RadioGroup radioGroup=(RadioGroup)view.findViewById(R.id.radiogroup_parity);
        int id=0;
        switch (parity) {
            case UsbSerialPort.PARITY_EVEN:
                id=R.id.radio_parity_even;
                break;
            case UsbSerialPort.PARITY_MARK:
                id=R.id.radio_parity_mark;
                break;
            case UsbSerialPort.PARITY_NONE:
                id=R.id.radio_parity_none;
                break;

            case UsbSerialPort.PARITY_ODD:
                id=R.id.radio_parity_odd;
                break;

            case UsbSerialPort.PARITY_SPACE:
                id=R.id.radio_parity_space;
                break;
        }
        radioGroup.check(id);
    }

    private int getParity() {
        RadioGroup radioGroup=(RadioGroup)view.findViewById(R.id.radiogroup_parity);

        int parity=0;
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.radio_parity_even:
                parity=UsbSerialPort.PARITY_EVEN;
                break;
            case R.id.radio_parity_mark:
                parity=UsbSerialPort.PARITY_MARK;
                break;
            case R.id.radio_parity_none:
                parity=UsbSerialPort.PARITY_NONE;
                break;
            case R.id.radio_parity_odd:
                parity=UsbSerialPort.PARITY_ODD;
                break;
            case R.id.radio_parity_space:
                parity=UsbSerialPort.PARITY_SPACE;
                break;
        }
        return parity;
    }

    private void setDataTerminalReady(boolean dataTerminalReady) {
        CheckBox checkBox_dataterminalready=(CheckBox) view.findViewById(R.id.checkBox_dataterminalready);
        checkBox_dataterminalready.setChecked(dataTerminalReady);
    }

    private boolean getDataTerminalReady() {
        CheckBox checkBox_dataterminalready=(CheckBox) view.findViewById(R.id.checkBox_dataterminalready);
        return checkBox_dataterminalready.isChecked();
    }
    private void setRequestToSend(boolean requestToSend) {
        CheckBox checkBox_requesttosend=(CheckBox) view.findViewById(R.id.checkBox_requesttosend);
        checkBox_requesttosend.setChecked(requestToSend);
    }

    private boolean getRequestToSend() {
        CheckBox checkBox_requesttosend=(CheckBox) view.findViewById(R.id.checkBox_requesttosend);
        return checkBox_requesttosend.isChecked();
    }
}
