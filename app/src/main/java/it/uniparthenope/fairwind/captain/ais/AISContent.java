package it.uniparthenope.fairwind.captain.ais;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class AISContent {

    /**
     * An array of sample (dummy) items.
     */
    private List<AISItem> items = new ArrayList<AISItem>();
    public List<AISItem> getItems() {
        return items;
    }

    public void sortItems(final AISField aisField, final AISOrder aisOrder) {
        Collections.sort(items, new Comparator<AISItem>(){
            public int compare(AISItem o1, AISItem o2){
                int result=-1;
                if (o1!=null && o2!=null) {
                    if (aisOrder == AISOrder.DESCENDING) {
                        AISItem temp = o1;
                        o1 = o2;
                        o2 = temp;
                    }
                    String s1,s2;
                    Double v1,v2;
                    switch (aisField) {
                        case TYPE:
                            result = o1.getType() - (o2.getType());
                            break;
                        case MMSI:
                            s1=o1.getMmsi();
                            s2=o2.getMmsi();
                            if (s1!=null && s2!=null) {
                                result = s1.compareTo(s2);
                            }
                            break;
                        case NAME:
                            s1=o1.getName();
                            s2=o2.getName();
                            if (s1!=null && s2!=null) {
                                result = s1.compareTo(s2);
                            }
                            break;
                        case RANGE:
                            v1=o1.getRange();
                            v2=o2.getRange();
                            if (v1!=null && v2!=null) {
                                result=(int)(v1-v2);
                            }
                            break;
                        case BEARING:
                            v1=o1.getBearing();
                            v2=o2.getBearing();
                            if (v1!=null && v2!=null) {
                                result=(int)(v1-v2);
                            }
                            break;
                        case SPEED:
                            v1=o1.getSpeed();
                            v2=o2.getSpeed();
                            if (v1!=null && v2!=null) {
                                result=(int)(v1-v2);
                            }
                            break;
                        case COURSE:
                            v1=o1.getCourse();
                            v2=o2.getCourse();
                            if (v1!=null && v2!=null) {
                                result=(int)(v1-v2);
                            }
                            break;
                    }
                }
                return result;
            }
        });
    }

    /**
     * A map of sample (dummy) items, by ID.
     */
    private  Map<Long, AISItem> item_map = new HashMap<Long, AISItem>();
    private Map<String, AISItem> item_map_uudi = new HashMap<String,AISItem>();


    public AISContent() {
        //FairWindModelImpl fairWindModel= FairWindApplication.getFairWindModel();
        //addItem(new AISItem(1,36,"66767","Pippo",new Position(40,13),5.7,14.2));
    }

    public void addItem(AISItem item) {
        items.add(item);
        item_map.put(item.getId(), item);
        item_map_uudi.put(item.getUuid(),item);
    }

    public void clear() {
        items.clear();
        item_map.clear();
        item_map_uudi.clear();
    }

    public AISItem getByUuid(String uuid) {
        return item_map_uudi.get(uuid);
    }
}
