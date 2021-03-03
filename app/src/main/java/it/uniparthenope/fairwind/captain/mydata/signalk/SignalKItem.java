package it.uniparthenope.fairwind.captain.mydata.signalk;

/**
 * Created by raffaelemontella on 04/09/15.
 */
public class SignalKItem {
    private long id;
    private String sId;
    private String sData;

    public long getId() { return id; }
    public String getSId() { return sId; }
    public String getSData() { return sData; }

    public SignalKItem(long id, String sId, String sData) {
        this.id = id;
        this.sId = sId;
        this.sData = sData;
    }

    @Override
    public String toString() {
        return sId+":"+sData;
    }
}
