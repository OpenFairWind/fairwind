package it.uniparthenope.fairwind.sdk.util;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by raffaelemontella on 23/09/15.
 */
public class Formatter {

    public static final int UNIT_RANGE_M=1;
    public static final int UNIT_RANGE_NM=2;
    public static final int UNIT_RANGE_KM=3;
    public static final int UNIT_RANGE_FT=4;
    public static final int UNIT_RANGE_YR=5;


    public static String formatRange(int unit,Double temp, String def) {
        if (temp == null) return def;
        String result = "";
        double finalValue;

        switch (unit) {
            case UNIT_RANGE_M:
                finalValue=temp;
                result=String.format("%5.0f m",finalValue);
                break;

            case UNIT_RANGE_KM:
                finalValue=temp/1000;
                result=String.format("%4.1f Km",finalValue);
                break;

            case UNIT_RANGE_NM:
                finalValue=temp/1852;
                result=String.format("%3.1f NM",finalValue);
                break;

            case UNIT_RANGE_FT:
                finalValue=temp*3.28084;
                result=String.format("%3.1f ft",finalValue);
                break;

            case UNIT_RANGE_YR:
                finalValue=temp*1.09361;
                result=String.format("%3.1f yr",finalValue);
                break;

            default:
                result=temp.toString();
                break;
        }

        return result;
    }


    public static String formatRatio(Double ratio, String s) {
        if (ratio == null) return "";
        return  String.format("%2.0f",ratio);
    }

    public static final int UNIT_TEMP_K=1;
    public static final int UNIT_TEMP_C=2;
    public static final int UNIT_TEMP_F=3;

    public static String formatTemp(int unit, Double temp, String s) {
        if (temp==null) return "";
        String result="";
        double finalTemp;

        switch (unit) {
            case UNIT_TEMP_K:
                finalTemp=temp;
                result=String.format("%5.0f° K",finalTemp);
                break;

            case UNIT_TEMP_C:
                finalTemp=temp-273.15;
                result=String.format("%4.1f° C",finalTemp);
                break;

            case UNIT_TEMP_F:
                finalTemp=temp*9/5 - 459.67+273.15;
                result=String.format("%3.1f° F",finalTemp);
                break;
            default:
                result=temp.toString();
                break;
        }

        return result;
    }



    public static final int UNIT_PRESSURE_PA=0;
    public static final int UNIT_PRESSURE_HPA=1;
    public static final int UNIT_PRESSURE_MB=1;
    public static final int UNIT_PRESSURE_HGMM=2;
    public static final int UNIT_PRESSURE_PSI=3;


    public static String formatPressure(int unit, Double pressure, String s) {
        if (pressure==null) return "";
        String result="";
        double finalPressure;
        switch (unit) {
            case UNIT_PRESSURE_PA:
                finalPressure=pressure;
                result=String.format("%5.0f",finalPressure);
                break;

            case UNIT_PRESSURE_HPA:
                finalPressure=pressure/100;
                result=String.format("%5.1fh",finalPressure);
                break;

            case UNIT_PRESSURE_HGMM:
                finalPressure=pressure*0.007500617;
                result=String.format("%3.1f",finalPressure);
                break;

            case UNIT_PRESSURE_PSI:
                finalPressure=pressure*0.000145038;
                result=String.format("%2.2f",finalPressure);
                break;
            default:
                result=pressure.toString();
                break;
        }

        return result;
    }



    public static final int UNIT_SPEED_MS=1;
    public static final int UNIT_SPEED_KMH=2;
    public static final int UNIT_SPEED_MPH=3;
    public static final int UNIT_SPEED_KNT=4;

    public static String formatSpeed(int unit, Double speed, String s) {
        if (speed==null) return "";
        String result="";
        double finalSpeed;
        switch (unit) {
            case UNIT_SPEED_MS:
                finalSpeed=speed;
                break;

            case UNIT_SPEED_KMH:
                finalSpeed=speed*3.6;
                break;

            case UNIT_SPEED_MPH:
                finalSpeed=speed*2.23693629;
                break;

            case UNIT_SPEED_KNT:
                finalSpeed=speed*1.94384449;
                if (finalSpeed>=0 && finalSpeed<1) {
                    result=String.format("%01.02f",finalSpeed);
                } else if (finalSpeed>=1 && finalSpeed<10) {
                    result=String.format("%1.1f",finalSpeed);
                } else {
                    result=String.format("%2.1f",finalSpeed);
                }
                result=result+" kts";
                break;
            default:
                result=speed.toString();
                break;
        }

        return result;
    }

    public static final int COORDS_STYLE_DD=1;
    public static final int COORDS_STYLE_DDMM=2;
    public static final int COORDS_STYLE_DDMMSS=3;

    public static String formatLatitude(int coordsStyle,Double latitude, String defValue) {
        if (latitude==null) return defValue;
        String ns, result="";
        int dd,mm,ss;

        if (latitude<0) ns="S"; else ns="N";
        latitude=Math.abs(latitude);

        switch (coordsStyle) {
            case COORDS_STYLE_DDMM:
                dd=(int)latitude.intValue();
                result=String.format("%02d°%02.03f'%s",dd,(latitude-dd)*60,ns);
                break;

            case COORDS_STYLE_DDMMSS:
                dd=(int)latitude.intValue();
                double mmmm=(latitude-dd)*60;
                mm=(int)mmmm;
                ss=(int)((mmmm-mm)*60);
                result=String.format("%02d°%02d'%02d\"%s",dd,mm,ss,ns);
                break;

            case COORDS_STYLE_DD:
                result=String.format("%02.04f",latitude);
                break;

            default:
                result=latitude.toString();
        }

        return result;
    }

    public static String formatLongitude(int coordsStyle, Double longitude, String defValue) {
        if (longitude==null) return defValue;
        String ns, result="";
        int dd,mm,ss;

        if (longitude<0) ns="W"; else ns="E";
        longitude=Math.abs(longitude);

        switch (coordsStyle) {
            case COORDS_STYLE_DDMM:
                dd=(int)longitude.intValue();
                result=String.format("%03d°%02.03f'%s",dd,(longitude-dd)*60,ns);
                break;

            case COORDS_STYLE_DDMMSS:
                dd=(int)longitude.intValue();
                double mmmm=(longitude-dd)*60;
                mm=(int)mmmm;
                ss=(int)((mmmm-mm)*60);
                result=String.format("%03d°%02d'%02d\"%s",dd,mm,ss,ns);
                break;

            case COORDS_STYLE_DD:
                result=String.format("%03.04f",longitude);
                break;

            default:
                result=longitude.toString();
                break;
        }

        return result;
    }

    public static final int UNIT_DEPTH_M=0;
    public static final int UNIT_DEPTH_FEET=1;
    public static final int UNIT_DEPTH_FATHOMS=2;

    public static String formatDepth(int unit, Double depth, String defValue) {
        if (depth==null) return defValue;
        String result=defValue;
        double finalDepth;
        switch (unit) {
            case UNIT_DEPTH_M:
                finalDepth=depth;
                result=String.format("%3.1f m",finalDepth);
                break;

            case UNIT_DEPTH_FEET:
                finalDepth=depth*3.280;
                result=String.format("%3.1f f",finalDepth);
                break;

            case UNIT_DEPTH_FATHOMS:
                finalDepth=depth*0.546806649;
                result=String.format("%3.1f F",finalDepth);
                break;

            default:
                result=depth.toString();
                break;
        }

        return result;
    }

    public static String formatAngle(Double angle, String defValue) {
        if (angle==null) return defValue;
        String result="";
        String portStarboard="";
        double finalWindAngle;
        angle=Math.toDegrees(angle);
        if (angle>180 && angle<=360) {
            angle=angle-360;
        }
        if (angle>0) {
            portStarboard="S";
        } else {
            portStarboard="P";
        }
        finalWindAngle=Math.abs(angle);
        result=String.format("%3.0f°%s", finalWindAngle,portStarboard);
        return result;
    }

    public static String formatDirection(Double direction, String defValue) {
        if (direction==null) return defValue;
        double finalDirection=Math.toDegrees(direction);
        if (finalDirection>=360) {
            finalDirection=finalDirection-360;
        } else if (finalDirection<0) {
            finalDirection=360+finalDirection;
        }
        String result=String.format("%03.0f°",finalDirection);
        return result;
    }

    public static final int TIME_STYLE_HHMMSS=0;
    public static final int TIME_STYLE_HHMM=1;

    //public static String formatTime(String time) {
    //    return time;
    //}
    public static String formatTime(int timeStyle, DateTime dateTime, String defValue) {
        if (dateTime==null) return defValue;
        DateTimeFormatter dateTimeFormatter=null;
        switch (timeStyle) {
            case TIME_STYLE_HHMM:
                dateTimeFormatter=DateTimeFormat.forPattern("HH:mm");
                break;

            default:
                dateTimeFormatter=DateTimeFormat.forPattern("HH:mm:ss");
                break;
        }
        String result= dateTime.toString(dateTimeFormatter);
        return result;
    }

    public static final int DATE_STYLE_DDMMYYYY = 0;
    public static final int DATE_STYLE_YYYYMMDD = 0;

    public static String formatDate(int dateStyle, DateTime dateTime, String defValue) {
        if (dateTime==null) return defValue;
        DateTimeFormatter dateTimeFormatter=null;
        switch (dateStyle) {
            case DATE_STYLE_YYYYMMDD:
                dateTimeFormatter=DateTimeFormat.forPattern("yyyyMMdd");
                break;

            default:
                dateTimeFormatter=DateTimeFormat.forPattern("MM/dd/yyyy");
                break;
        }
        String result= dateTime.toString(dateTimeFormatter);
        return result;
    }

    public static final int UNIT_RPM_X1=0;
    public static final int UNIT_RPM_X1000=1;

    public static String formatRPM(int unit, Double rpm, String defValue) {
        if (rpm==null) return defValue;
        String result=defValue;
        double finalRpm;
        switch (unit) {
            case UNIT_RPM_X1:
                finalRpm=rpm/60;
                result=String.format("%3.1f m",finalRpm);
                break;

            case UNIT_RPM_X1000:
                finalRpm=rpm/60000;
                result=String.format("%3.1f f",finalRpm);
                break;

            default:
                result=rpm.toString();
                break;
        }

        return result;
    }

    public static String formatString(String name, String s) {
        if (name == null || name.isEmpty()) return s;
        else return name;
    }

    public static String formatTimeSpan(int timeStyle, Double seconds, String s) {
        String result=s;
        if (seconds != null && Double.isNaN(seconds)==false) {

            long absSeconds = (long) Math.abs(seconds);
            switch (timeStyle){
                case TIME_STYLE_HHMM:
                    result = String.format(
                            "%d:%02d",
                            absSeconds / 3600,
                            (absSeconds % 3600) / 60);
                    break;
                case TIME_STYLE_HHMMSS:
                    result = String.format(
                            "%d:%02d:%02d",
                            absSeconds / 3600,
                            (absSeconds % 3600) / 60,
                            absSeconds % 60);
                    break;
            }
        }
        return result;
    }

    public static String formatInteger(Integer points, String s) {
        String result=s;
        if (points!=null) {
            result=String.format("%d",points);
        }
        return result;
    }


}
