package it.uniparthenope.fairwind.sdk.util;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import it.uniparthenope.fairwind.sdk.R;
import it.uniparthenope.fairwind.sdk.model.Constants;
import it.uniparthenope.fairwind.sdk.model.FairWindModel;
import mjson.Json;

/**
 * Created by raffaelemontella on 25/01/2017.
 */

public class Units {
    public static final String LOG_TAG="UNITS";

    private Json units;

    public Units(Json units) {
        this.units=units;
    }

    public double getUnitConvertedValue(double srcValue, String srcUnits, String dstUnits) {
        double dstValue=srcValue;
        if (srcUnits.equals(dstUnits)==false) {
            Json j1=units.asJsonMap().get(srcUnits);
            if (j1!=null) {
                Json j2=j1.asJsonMap().get(dstUnits);
                if (j2!=null) {
                    double a=j2.at("a").asDouble();
                    double b=j2.at("b").asDouble();
                    double c=j2.at("c").asDouble();
                    dstValue=((a+srcValue)*b)+c;
                }
            }
        }
        return dstValue;
    }
}
