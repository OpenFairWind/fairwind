package it.uniparthenope.fairwind.sdk.captain.setup.meta;

/**
 * Created by raffaelemontella on 08/02/2017.
 */

public enum State {
    NORMAL("normal"), ALERT("alert"), WARN("warn"), ALARM("alarm"),EMERGENCY("emergency"),UNKNOWN("unknown");

    private String text;

    State(String text) {
        this.text=text;
    }

    /*
    public String toString() {
        return text;
    }
    */

}
