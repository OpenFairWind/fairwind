package it.uniparthenope.fairwind.sdk.model.resources.waypoints;

/**
 * Created by raffaelemontella on 04/11/2017.
 */

public enum Sort {
    ASCENDANT(0),
    DESCENDANT(1);

    private int value;

    Sort(int value) {
        this.value=value;
    }
}
