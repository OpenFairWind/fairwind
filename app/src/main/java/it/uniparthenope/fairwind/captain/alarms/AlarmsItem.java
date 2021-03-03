package it.uniparthenope.fairwind.captain.alarms;

import it.uniparthenope.fairwind.sdk.captain.setup.meta.State;

/**
 * Created by raffaelemontella on 23/02/2017.
 */

public class AlarmsItem {
    private String path;
    private String description;
    private State state;
    private String message;

    public AlarmsItem(String path, String description, State state, String message) {
        this.path=path;
        this.description=description;
        this.state=state;
        this.message=message;
    }

    public String getPath() { return path; }
    public String getDescription() { return description; }
    public State getState() { return state; }
    public String getMessage() { return message; }
}
