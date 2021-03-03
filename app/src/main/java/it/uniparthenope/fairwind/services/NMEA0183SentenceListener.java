package it.uniparthenope.fairwind.services;

import android.util.Log;

import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.event.SentenceListener;
import net.sf.marineapi.nmea.parser.DataNotAvailableException;

import it.uniparthenope.fairwind.FairWindApplication;
import it.uniparthenope.fairwind.data.nmea0183.mapper.HDGMapper;
import it.uniparthenope.fairwind.data.nmea0183.mapper.HDMMapper;
import it.uniparthenope.fairwind.data.nmea0183.mapper.HDTMapper;
import it.uniparthenope.fairwind.data.nmea0183.mapper.MWDMapper;
import it.uniparthenope.fairwind.data.nmea0183.mapper.VHWMapper;
import it.uniparthenope.fairwind.data.nmea0183.mapper.VTGMapper;
import it.uniparthenope.fairwind.data.nmea0183.mapper.XDRMapper;
import it.uniparthenope.fairwind.data.nmea0183.mapper.ZDAMapper;


/**
 * Created by raffaelemontella on 16/12/15.
 */
public class NMEA0183SentenceListener implements SentenceListener {
    // The Log tag
    private static final String LOG_TAG = "NMEA0183SENTENCE_LIST";

    private boolean startLat;
    private boolean startLon;
    private double previousLat;
    private double previousLon;
    private double previousSpeed;
    private static final double ALPHA = 1 - 1.0 / 6;

    public NMEA0183SentenceListener() {
        startLat = true;
        startLon = true;
        previousLat = 0;
        previousLon = 0;
        previousSpeed = 0;
    }

    @Override
    public void readingPaused() {

    }

    @Override
    public void readingStarted() {

    }

    @Override
    public void readingStopped() {

    }

    @Override
    public void sentenceRead(SentenceEvent evt) {
        Log.d(LOG_TAG,"sentenceRead:"+evt.getSentence().toSentence());


        try{


            if (ZDAMapper.is(evt)) new ZDAMapper(evt).map();
            if (XDRMapper.is(evt)) new XDRMapper(evt).map();

            if (MWDMapper.is(evt) && !FairWindApplication.getFairWindModel().isTrueWindComputed()) new MWDMapper(evt).map();

            if (HDTMapper.is(evt)) new HDTMapper(evt).map();
            if (HDMMapper.is(evt)) new HDMMapper(evt).map();


            if (VHWMapper.is(evt)) new VHWMapper(evt).map();
            if (VTGMapper.is(evt)) new VTGMapper(evt).map();
            /*
            if (MTWMapper.is(evt)) new MTWMapper(evt).map();


            if (HDTMapper.is(evt)) new HDTMapper(evt).map();
            if (HDMMapper.is(evt)) new HDMMapper(evt).map();
            if (HDGMapper.is(evt)) new HDGMapper(evt).map();
            if (HDMMapper.is(evt)) new HDMMapper(evt).map();
            if (HDTMapper.is(evt)) new HDTMapper(evt).map();



            if (MWVMapper.is(evt)) new MWVMapper(evt).adapt();

            if (VBWMapper.is(evt)) new VBWMapper(evt).map();
            if (VDRMapper.is(evt)) new VDRMapper(evt).adapt();
            if (VHWMapper.is(evt)) new VHWMapper(evt).adapt();
            if (BWCMapper.is(evt)) new BWCMapper(evt).adapt();
            if (GSVMapper.is(evt)) new GSVMapper(evt).adapt();
            if (GSAMapper.is(evt)) new GSAMapper(evt).adapt();
            if (DBTMapper.is(evt)) new DBTMapper(evt).adapt();
            if (DPTMapper.is(evt)) new DPTMapper(evt).adapt();
            if (VWTMapper.is(evt)) new VWTMapper(evt).adapt();
            if (VWRMapper.is(evt)) new VWRMapper(evt).adapt();
            if (VPWMapper.is(evt)) new VPWMapper(evt).adapt();
            if (ATDMapper.is(evt)) new ATDMapper(evt).adapt();
            if (HSCMapper.is(evt)) new HSCMapper(evt).adapt();
            if (HVMMapper.is(evt)) new HVMMapper(evt).adapt();
            if (VLWMapper.is(evt)) new VLWMapper(evt).adapt();
            if (RSAMapper.is(evt)) new RSAMapper(evt).adapt();
            if (VTGMapper.is(evt)) new VTGMapper(evt).adapt();
*/
            //////////////////////////////////////////
        }catch (DataNotAvailableException e){
            Log.e(LOG_TAG,e.getMessage()+":"+evt.getSentence().toSentence());

        }


    }
}
