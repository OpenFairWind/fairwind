package it.uniparthenope.fairwind.captain.maps;

import java.util.UUID;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.sdk.captain.setup.maps.MapPreferences;

/**
 * Created by raffaelemontella on 14/01/2017.
 */

public class LegendItem {
    private UUID uuid;
    private String name;
    private boolean visible;
    private int icon;

    public LegendItem(String name, int icon, boolean visible) {
        this.uuid=null;
        this.name=name;
        this.icon=icon;
        this.visible=visible;
    }

    public LegendItem(MapPreferences mapPreferences) {
        this.uuid=mapPreferences.getUuid();
        this.name=mapPreferences.getName();
        this.icon= R.drawable.icon_layers;
        this.visible=mapPreferences.isVisible();
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public int getIcon() { return  icon; }
    public boolean isVisible() { return visible; }

}
