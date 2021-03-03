package it.uniparthenope.fairwind.captain.mydata.signalk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class SignalKContent {

    private List<SignalKItem> items = new ArrayList<SignalKItem>();
    public List<SignalKItem> getItems() { return items; }

    /**
     * A map of sample (dummy) items, by ID.
     */
    private  Map<Long, SignalKItem> item_map = new HashMap<Long, SignalKItem>();

    public void addItem(SignalKItem item) {
        items.add(item);
        item_map.put(item.getId(), item);
    }

    public void clear() {
        items.clear();
        item_map.clear();
    }
}
