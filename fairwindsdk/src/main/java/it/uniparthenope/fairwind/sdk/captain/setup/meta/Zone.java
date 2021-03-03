package it.uniparthenope.fairwind.sdk.captain.setup.meta;

import mjson.Json;

/**
 * Created by raffaelemontella on 07/02/2017.
 */

public class Zone {
    private double lower;
    private double upper;
    private State state;
    private String message;

    public double getLower() { return lower; }
    public double getUpper() { return upper; }
    public State getState() { return state; }
    public String getMessage() { return  message;}

    public void setLower(double lower) { this.lower = lower; }
    public void setUpper(double upper) { this.upper = upper; }
    public void setState(State state) { this.state = state; }
    public void setMessage(String message) { this.message = message; }

    public Zone() {
        lower=0;
        upper=0;
        state= State.NORMAL;
        message="";
    }

    public Zone(double lower, double upper, State state, String message) {
        init();
        this.lower=lower;
        this.upper=upper;
        this.state=state;
        this.message=message;
    }

    public Zone(Json json) {
        init();
        byJson(json);
    }

    public Zone(Zone zone) {
        this.lower=zone.lower;
        this.upper=zone.upper;
        this.state=zone.state;
        this.message=zone.message;
    }

    private void init() {

    }

    public void byJson(Json json) {
        lower=json.at("lower","").asDouble();
        upper=json.at("upper","").asDouble();
        state=State.valueOf(json.at("state","").asString().replace("\"","").toUpperCase());
        message=json.at("message","").asString().replace("\"","");
    }

    public Json asJson() {
        Json json=Json.object();
        json.set("lower",lower);
        json.set("upper",upper);
        json.set("state",state.toString().toLowerCase());
        json.set("message",message);


        return json;
    }



}
