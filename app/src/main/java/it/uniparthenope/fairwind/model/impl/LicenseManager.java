package it.uniparthenope.fairwind.model.impl;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.sdk.model.Constants;
import it.uniparthenope.fairwind.services.logger.SecureFilePacker;
import mjson.Json;

/**
 * Created by raffaelemontella on 23/04/16.
 */
public class LicenseManager implements SecureFilePacker{
    private static final String LOG_TAG = "LICENSE_MANAGER";

    public static final String KEY="C3BOH725R2D2BB8";
    private FairWindModelImpl fairWindModel;

    private boolean checked=false;
    public boolean hasBeenChecked() { return checked; }





    private static String deviceId=getDeviceId();
    public static String getDeviceId() {
        if (deviceId==null || deviceId.isEmpty()==true) {
            // 3 android ID - unreliable
            String m_szAndroidID="";
            String m_szImei ="";
            String m_szDevIDShort="";
            String m_szWLANMAC="";
            String m_szBTMAC="";

            Context context=FairWindApplication.getInstance().getApplicationContext();

            ContentResolver contentResolver=context.getContentResolver();
            if (contentResolver!=null) {
                m_szAndroidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                Log.d(LOG_TAG,"m_szAndroidID " + m_szAndroidID);
            }

            //Context context=getBaseContext();

            if (context!=null) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                if (tm!=null) {
                    try {
                        m_szImei = tm.getDeviceId(); // Requires READ_PHONE_STATE
                    }  catch (SecurityException ex) {
                        Log.d(LOG_TAG,ex.getMessage());
                    }
                }

                // 2 compute DEVICE ID
                m_szDevIDShort = "35"
                        + // we make this look like a valid IMEI
                        Build.BOARD.length() % 10 + Build.BRAND.length() % 10
                        + Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10
                        + Build.DISPLAY.length() % 10 + Build.HOST.length() % 10
                        + Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10
                        + Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10
                        + Build.TAGS.length() % 10 + Build.TYPE.length() % 10
                        + Build.USER.length() % 10; // 13 digits
                Log.d(LOG_TAG,"m_szDevIDShort " + m_szDevIDShort);

                // 4 wifi manager, read MAC address - requires
                // android.permission.ACCESS_WIFI_STATE or comes as null
                WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                if (wm!=null) {
                    m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
                    Log.d(LOG_TAG, "m_szWLANMAC " + m_szWLANMAC);
                }
            }



            // 5 Bluetooth MAC address android.permission.BLUETOOTH required
            BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth adapter
            m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (m_BluetoothAdapter!=null) {
                m_szBTMAC = m_BluetoothAdapter.getAddress();
                Log.d(LOG_TAG,"m_szBTMAC " + m_szBTMAC);
            }
            // 6 SUM THE IDs
            String m_szLongID = m_szImei + m_szDevIDShort + m_szAndroidID + m_szWLANMAC + m_szBTMAC;
            Log.d(LOG_TAG,"m_szLongID " + m_szLongID);
            MessageDigest m = null;
            try {
                m = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
            byte p_md5Data[] = m.digest();

            deviceId = new String();
            for (int i = 0; i < p_md5Data.length; i++) {
                int b = (0xFF & p_md5Data[i]);

                // if it is a single digit, make sure it have 0 in front (proper padding)
                if (b <= 0xF) {
                    deviceId += "0";
                }

                // add number to string
                deviceId += Integer.toHexString(b);
            }

            String sLow=deviceId.substring(0,deviceId.length()/2);
            String sHigh=deviceId.substring(deviceId.length()/2);

            BigInteger low = new BigInteger(sLow, 16);
            BigInteger high = new BigInteger(sHigh, 16);

            UUID deviceUuid = new UUID(low.longValue(),high.longValue());
            deviceId = deviceUuid.toString();
            Log.i(LOG_TAG, deviceId);
            Log.d(LOG_TAG, "DeviceId that generated MPreferenceActivity:" + deviceId);

        }
        return deviceId;
    }

    public LicenseManager(FairWindModelImpl fairWindModel) {
        this.fairWindModel=fairWindModel;
    }

    public String getUserId() {

        return fairWindModel.getPreferences().getConfigProperty("user_id");
    }

    public void setUserId(String userId) {

        fairWindModel.getPreferences().setConfigProperty("user_id", userId);
    }

    public void setPassword(String password) throws ValidationException {
        if (password!=null && !password.isEmpty()) {
            String userId = getUserId();
            if (userId != null && !userId.isEmpty()) {
                Json jsonPassword = Json.object();
                jsonPassword.set("password", password);

                try {
                    SSL ssl=new SSL(FairWindApplication.getInstance().getPackageName() + deviceId);
                    fairWindModel.getPreferences().setConfigProperty("password", ssl.obfuscate(jsonPassword,PreferencesImpl.KEY));
                } catch (GeneralSecurityException ex) {
                    throw new ValidationException(ex.getMessage());
                }
            }
        }

    }
    
    public String getPassword() throws ValidationException {
        SSL ssl=new SSL(FairWindApplication.getInstance().getPackageName() + deviceId);
        Json jsonPassword = ssl.unobfuscate(fairWindModel.getPreferences().getConfigProperty(Constants.PREF_KEY_LICENSE_KEY),PreferencesImpl.KEY);
        return jsonPassword.asMap().get("password").toString();
    }

    public void setLicenseString(String licenseString) throws ValidationException {

        String userId=getUserId();
        if (userId!=null && !userId.isEmpty()) {

            SSL ssl=new SSL(FairWindApplication.getInstance().getPackageName() + deviceId);
            Json licenseJson = null;
            String s=licenseString.split("==")[0];
            licenseJson = ssl.unobfuscate(s, "license");
            if (licenseJson != null) {
                Log.d(LOG_TAG, "license ->" + licenseJson.toString());
                fairWindModel.getPreferences().setConfigProperty("license", licenseString);
            }
        }
    }

    public void check() throws ValidationException {
        checked=false;
        String userId=getUserId();
        if (userId!=null && !userId.isEmpty()) {

            SSL ssl=new SSL(FairWindApplication.getInstance().getPackageName() + deviceId);
            String licenseString = fairWindModel.getPreferences().getConfigProperty("license");
            if (licenseString!=null && !licenseString.isEmpty()) {
                Json licenseJson = null;
                String s=licenseString.split("==")[0];
                licenseJson = ssl.unobfuscate(s, "license");
                Log.d(LOG_TAG, "license ->" + licenseJson.toString());
                checked=true;
            } else {
                clean();
                throw new ValidationException("Unlicensed");
            }
        } else {
            throw new ValidationException("Undefined or empty username");
        }

    }

    public void clean() {

        fairWindModel.getPreferences().setConfigProperty("license", null);
        checked=false;
    }

    public void pack(File source, File destination) throws IOException, GeneralSecurityException {
        /*
        ToDo:
        1. Calculate the signature
        2. Add the signature
        3. Encrypt with the user private key
        4. Zip everything
        */


        String userId=getUserId();
        if (userId!=null && userId.isEmpty()==false && deviceId!=null && deviceId.isEmpty()==false) {

            BufferedReader reader = new BufferedReader(new FileReader (source));
            String         line = null;
            StringBuilder  stringBuilder = new StringBuilder();

            String dataAsString=null;
            try {
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                    stringBuilder.append(",");
                }

                dataAsString = "{\"data\":[" + stringBuilder.toString() + "]}";
                reader.close();


                if (dataAsString != null && dataAsString.isEmpty() == false) {
                    final int BUFFER = 2048;

                    // Read the source file as a Json object
                    Json jsonData = Json.read(dataAsString);

                    // Calculate the signature from its string representation
                    String signature = md5(jsonData);

                    // Create a new Json object
                    Json jsonPack = Json.object();

                    // add the signature
                    jsonPack.set("signature", signature);

                    // add the data
                    jsonPack.set("data", jsonData.at("data"));

                    // create an obfuscator
                    SSL ssl=new SSL(FairWindApplication.getInstance().getPackageName() + deviceId);

                    // Obfuscate the pack as a string
                    String obfuscatedPack = ssl.obfuscate(jsonPack, "pack");

                    // Use the string as input stream
                    InputStream is = new ByteArrayInputStream(obfuscatedPack.getBytes(StandardCharsets.UTF_8));

                    // Create the buffered input stream
                    BufferedInputStream origin = new BufferedInputStream(is, BUFFER);

                    String destinationPath = destination.getAbsolutePath()+File.separator + deviceId+"_"+source.getName() +  ".aes.zip";

                    Log.d(LOG_TAG, "secureFilePacking:" + source.getAbsolutePath() + " -> " + destinationPath);

                    // Create the destination file
                    FileOutputStream dest = new FileOutputStream(destinationPath);

                    // Compress the file
                    ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
                    byte data[] = new byte[BUFFER];
                    ZipEntry entry = new ZipEntry(deviceId+"_"+source.getName() + ".aes");
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, BUFFER)) != -1) {
                        out.write(data, 0, count);
                    }
                    out.close();
                }
            } catch (IOException ex) {
                Log.d(LOG_TAG,ex.getMessage());
            }
        }
    }

    public static final String md5(Json json) {
        final String s=json.toString();
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
