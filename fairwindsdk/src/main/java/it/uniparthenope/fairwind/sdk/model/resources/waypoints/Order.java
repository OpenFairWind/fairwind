package it.uniparthenope.fairwind.sdk.model.resources.waypoints;

/**
 * Created by raffaelemontella on 24/10/2017.
 */

public enum Order {
    DATEANDTIME(0),
    NAME(1),
    RANGE(2),
    TYPE(3);

    private int value;

    Order(int value) {
        this.value=value;
    }
}
