package it.uniparthenope.fairwind.services;

import android.util.Log;


import com.jraska.console.Console;

import net.sf.marineapi.nmea.parser.SentenceFactory;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.data.nmea0183.Builder;
import it.uniparthenope.fairwind.data.pcdin.PgnHandler;
import it.uniparthenope.fairwind.data.nmea0183.sentence.SUPSentence;
import net.sf.marineapi.nmea.sentence.Sentence;
import net.sf.marineapi.nmea.sentence.SentenceValidator;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;

import it.uniparthenope.fairwind.data.stalk.StalkHandler;
import it.uniparthenope.fairwind.sdk.model.Constants;
import it.uniparthenope.fairwind.model.UpdateException;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import nz.co.fortytwo.signalk.handler.AISHandler;
import nz.co.fortytwo.signalk.handler.NMEAHandler;
import nz.co.fortytwo.signalk.model.SignalKModel;
import nz.co.fortytwo.signalk.model.event.PathEvent;
import nz.co.fortytwo.signalk.util.SignalKConstants;


/**
 * Created by raffaelemontella on 03/12/15.
 */
public abstract class NMEA0183Listener extends DataListener {
    // The Log tag
    private static final String LOG_TAG = "NMEA0183_LISTENER";

    private NMEAHandler nmeaHandler;
    private AISHandler aisHandler;
    private StalkHandler stalkHandler;
    private PgnHandler pgnHandler;

    private Builder nmeaBuilder;


    // The sentence factory
    private SentenceFactory sentenceFactory;
    protected SentenceFactory getSentenceFactory() { return sentenceFactory; }




    //public abstract void start() throws DataListenerException;
    //public abstract void stop();
    public abstract void send(String sSentence) throws DataListenerException;

    @Override
    public long getTimeout() { return 1000; }



    public NMEA0183Listener() {

    }

    public NMEA0183Listener( NMEA0183ListenerPreferences prefs) {
        super(prefs);
        init();
    }

    public NMEA0183Listener( String name) {
        super(name);
        init();
    }

    private void init() {
        sentenceFactory = SentenceFactory.getInstance();
        nmeaHandler=new NMEAHandler();
        aisHandler=new AISHandler();
        stalkHandler=new StalkHandler();
        pgnHandler=new PgnHandler();

        //sentenceFactory.registerParser("SUP", it.uniparthenope.fairwind.data.nmea0183.parser.SUPParser.class);
        sentenceFactory.registerParser("ATD", it.uniparthenope.fairwind.data.nmea0183.parser.ATDParser.class);
        sentenceFactory.registerParser("BWC", it.uniparthenope.fairwind.data.nmea0183.parser.BWCParser.class);
        sentenceFactory.registerParser("HSC", it.uniparthenope.fairwind.data.nmea0183.parser.HSCParser.class);
        sentenceFactory.registerParser("VPW", it.uniparthenope.fairwind.data.nmea0183.parser.VPWParser.class);

        nmeaHandler.addSentenceListener(new NMEA0183SentenceListener());

        nmeaBuilder=new Builder(FairWindApplication.getFairWindModel());
        nmeaBuilder.register(SignalKConstants.env_wind_angleApparent+".value",it.uniparthenope.fairwind.data.nmea0183.builder.EnvironmentWindAngleSpeedApparentBuilder.class);
        nmeaBuilder.register(SignalKConstants.env_wind_speedApparent+".value",it.uniparthenope.fairwind.data.nmea0183.builder.EnvironmentWindAngleSpeedApparentBuilder.class);
        nmeaBuilder.register(SignalKConstants.env_wind_angleTrueGround+".value",it.uniparthenope.fairwind.data.nmea0183.builder.EnvironmentWindAngleSpeedTrueBuilder.class);
        nmeaBuilder.register(SignalKConstants.env_wind_angleTrueWater+".value",it.uniparthenope.fairwind.data.nmea0183.builder.EnvironmentWindAngleSpeedTrueBuilder.class);
        nmeaBuilder.register(SignalKConstants.env_wind_speedTrue+".value",it.uniparthenope.fairwind.data.nmea0183.builder.EnvironmentWindAngleSpeedTrueBuilder.class);

        nmeaBuilder.register(SignalKConstants.env_depth_belowTransducer+".value",it.uniparthenope.fairwind.data.nmea0183.builder.EnvironmentDepthBelowTransducerBuilder.class);

        nmeaBuilder.register(SignalKConstants.nav_speedThroughWater+".value",it.uniparthenope.fairwind.data.nmea0183.builder.NavigationSpeedThroughWaterBuilder.class);
        nmeaBuilder.register(SignalKConstants.nav_headingMagnetic+".value",it.uniparthenope.fairwind.data.nmea0183.builder.NavigationHeadingMagneticrBuilder.class);
        nmeaBuilder.register(SignalKConstants.nav_headingTrue+".value",it.uniparthenope.fairwind.data.nmea0183.builder.NavigationHeadingTrueBuilder.class);

        nmeaBuilder.register(SignalKConstants.nav_speedOverGround+".value",it.uniparthenope.fairwind.data.nmea0183.builder.NavigationSpeedOverGroundBuilder.class);
        nmeaBuilder.register(SignalKConstants.nav_courseOverGroundMagnetic+".value",it.uniparthenope.fairwind.data.nmea0183.builder.NavigationCourseOverGroundMagneticBuilder.class);
        nmeaBuilder.register(SignalKConstants.nav_courseOverGroundTrue+".value",it.uniparthenope.fairwind.data.nmea0183.builder.NavigationCourseOverGroundTrueBuilder.class);


        nmeaBuilder.register(SignalKConstants.nav_position_latitude,it.uniparthenope.fairwind.data.nmea0183.builder.NavigationPositionBuilder.class);
        nmeaBuilder.register(SignalKConstants.nav_position_longitude,it.uniparthenope.fairwind.data.nmea0183.builder.NavigationPositionBuilder.class);
    }


    @Override
    public void onUpdate(PathEvent pathEvent) throws UpdateException {

        Vector<String> sSentences = null;
        try {
            sSentences = nmeaBuilder.build(pathEvent);

            if (sSentences != null && !sSentences.isEmpty()) {
                for (String sSentence : sSentences) {
                    try {
                        send(sSentence);
                    } catch (DataListenerException e) {
                        Log.e(LOG_TAG, e.getMessage());
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    Update the status
     */

    public void process(String sSentence) {
        Log.d(LOG_TAG,"process:"+sSentence);
        // Remove the line separator form the sentence
        sSentence = sSentence.replace(System.getProperty("line.separator"), "");
        sSentence = sSentence.replace("\r", "");

        // Check if the sentence is valid
        if (SentenceValidator.isValid(sSentence)) {
            SignalKModel signalKObject=null;

            Console.writeLine(Thread.currentThread().getName()+":"+sSentence);

            // The sentence has been received!
            if (StalkHandler.sniff(sSentence)) {
                Log.d(LOG_TAG,"STALK Sentence: "+sSentence);
                signalKObject=stalkHandler.handle(sSentence,"nmea_stalk");
                Log.d(LOG_TAG,"STALK Done!");
            } else if (PgnHandler.sniff(sSentence)) {
                Log.d(LOG_TAG,"PGN Sentence: "+sSentence);
                signalKObject=pgnHandler.handle(sSentence,"nmea_pcdin");
                Log.d(LOG_TAG,"PGN Done!");
            } else
            try {
                // Parse the sentence
                Sentence aSentence = sentenceFactory.createParser(sSentence);

                if (aSentence.isAISSentence()) {
                    try {
                        Log.d(LOG_TAG,"AISERROR:"+aSentence.toSentence());
                        signalKObject=aisHandler.handle(aSentence.toSentence(),"nmea_ais");

                    } catch (Exception e) {
                        Log.e(LOG_TAG,"AISERROR:"+e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    signalKObject=nmeaHandler.handle(aSentence.toSentence(),"nmea_0");
                }





            } catch (IllegalArgumentException e) {
                Log.e(LOG_TAG,e.getMessage());
                //Log.d(LOG_TAG, "ID SENTENCE NON RICONOSCIUTO: " + sSentence);
                // e.printStackTrace();

            }


            if (signalKObject!=null && signalKObject.getFullData().isEmpty()==false) {
                Log.d(LOG_TAG,"process:"+signalKObject);
                process(signalKObject);
                Log.d(LOG_TAG,"process done.");
            }

        } else {
            Log.d(LOG_TAG, "Not valid sentence: "+sSentence);
        }
    }

    private void manageCustomSentences(Sentence sentence) {


        String xx = sentence.getTalkerId().name();
        String yyy = sentence.getSentenceId();


        // Check if it is a FairWind specific sentence
        if (xx.matches("FW")) {

            // Check if a system update
            if (yyy.matches("SUP")) {
                SUPSentence supSentence = (SUPSentence) sentence;

            } /*else// Check if a SeaTalk message over NMEA
                if (yyy.matches("STN")) {
                    STNSentence stn = (STNSentence) sentence;
                    Stalk stalk = StalkFactory.createParser(stn);
                    if (stalk != null) {
                        switch (stalk.getId()) {
                            case SeaTalk.SEATALK_ID_DEPTH:
                                Stalk00 stalk00 = (Stalk00) stalk;
                                float depth = stalk00.getDepth();
                                if (!Float.isNaN(depth) && 1 == 0) {
                                    FairWindApplication.getFairWindModel().getData().put(SignalKConstants.env_depth_belowTransducer, new Double(depth));
                                }
                                break;

                            default:
                                break;

                        }
                    }
                }
            */
        }

        try {
            log(sentence);
        } catch (IOException ioException) {
            Log.d(LOG_TAG, ioException.getMessage());
        }


    }

    void log(Sentence sentence) throws IOException {
        // Skip FW generated sequences
        //if (sentence.getTalkerId().name().matches("FW")) return;

        FairWindModelImpl fairWindModel=FairWindApplication.getFairWindModel();
        if (fairWindModel.getPreferences().getConfigPropertyBoolean(Constants.PREF_KEY_BOARDLOG_NMEA_ACTIVE)) {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            String formattedDate = df.format(c.getTime());

            String fileName=formattedDate+".nmea";

            File dir = new File(fairWindModel.getPreferences().getConfigProperty(Constants.PREF_KEY_BOARDLOG_NMEA_PATH));
            dir.mkdirs();
            File file = new File(dir,fileName);
            FileWriter fileWriter = new FileWriter(file,true);
            fileWriter.write(sentence.toSentence() + "\n");
            fileWriter.flush();
            fileWriter.close();

        }

    }


}
