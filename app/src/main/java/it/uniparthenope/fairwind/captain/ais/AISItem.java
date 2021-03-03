package it.uniparthenope.fairwind.captain.ais;


import it.uniparthenope.fairwind.FairWindApplication;
//import it.uniparthenope.fairwind.sdk.model.signalk.Vessel;
import it.uniparthenope.fairwind.model.impl.FairWindModelImpl;
import it.uniparthenope.fairwind.sdk.util.Position;

/**
 * Created by raffaelemontella on 28/08/15.
 */
public class AISItem {
    private long id;
    private String uuid;

    public long getId() { return id; }
    public int getType() { return  0; }

    public String getUuid() { return uuid; }
    public String getMmsi() {
        FairWindModelImpl fairWindModel = FairWindApplication.getFairWindModel();
        String mmsi = fairWindModel.getMmsi(uuid);
        return mmsi;
    }
    public String getName() { return FairWindApplication.getFairWindModel().getName(uuid); }
    public Double getSpeed() { return FairWindApplication.getFairWindModel().getAnySpeed(uuid);  }
    public Double getCourse() { return FairWindApplication.getFairWindModel().getAnyCourse(uuid); }
    public Position getPosition() { return FairWindApplication.getFairWindModel().getNavPosition(uuid);}


    public Double getRange() {
        Position currentPosition=FairWindApplication.getFairWindModel().getPosition();
        Position position=getPosition();
        if (position!=null && currentPosition!=null) {
            return position.distanceTo(currentPosition);
        }
        return null;
    }

    public Double getBearing() {
        Position currentPosition=FairWindApplication.getFairWindModel().getPosition();
        Position position=getPosition();
        if (position!=null && currentPosition!=null) {
            return Math.toRadians(position.bearingTo(currentPosition));
        }
        return null;
    }

    public AISItem(long id, String uuid ) {
        this.id=id;
        this.uuid=uuid;
    }

    @Override
    public String toString() {
        return String.format("%n %n %s %s %s %d %d %d %d",id,getType(), uuid, getMmsi(),getName(),getPosition().getLatitude(),getPosition().getLongitude(),getSpeed(),getCourse());
    }

}
