package it.uniparthenope.fairwind.captain.setup.preferences.meta;

import android.content.res.Resources;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import it.uniparthenope.fairwind.R;
import it.uniparthenope.fairwind.sdk.util.TreeNode;
import it.uniparthenope.fairwind.sdk.util.Utils;
import mjson.Json;

import it.uniparthenope.fairwind.sdk.captain.setup.meta.Key;

/**
 * Created by raffaelemontella on 10/02/2017.
 */

public class KeysContent {
    public static final String LOG_TAG="KEYS...CONTENT";


    private Stack<String> path=new Stack<String>();
    public Stack<String> getPath() {return path;}
    /**
     * An array of sample (dummy) items.
     */
    private List<Key> items = new ArrayList<Key>();
    private TreeNode<String> root = new TreeNode<String>("/");

    /**\
     * A map of sample (dummy) items, by ID.
     */
    private Map<String, Key> item_map = new LinkedHashMap<String, Key>();

    public void addItem(Key item) {
        items.add(item);
        item_map.put(item.getPath(), item);
    }

    public List<Key> getItems() { return items; }


    public KeysContent(Resources resources){
        try {
            String defaultString= Utils.readTextFromResource(resources, R.raw.keyswithmetadata);
            Json defaultJson=Json.read(defaultString);
            Map<String,Json> jsonMap=defaultJson.asJsonMap();

            for (String keyString:jsonMap.keySet()) {
                Key key = new Key(keyString, jsonMap.get(keyString));
                addItem(key);
                String[] parts=key.getPath().substring(1).split("/");
                TreeNode<String> currentNode=root;

                for (String part:parts) {
                    TreeNode<String> foundChildren = null;
                    if (currentNode.getChildren()!=null) {
                        for (TreeNode<String> node : currentNode.getChildren()) {
                            String data = node.getData();
                            if (data.equals(part)) {
                                foundChildren = node;
                                break;
                            }
                        }
                    }
                    // Check if the part represent a children
                    if (foundChildren == null) {
                        currentNode=currentNode.addChildren(part);

                    } else {
                        // Continue to visit the tree
                        currentNode=foundChildren;

                    }
                }

            }

            Collections.sort(items, new Comparator<Key>(){
                public int compare(Key o1, Key o2){
                    return o1.getPath().compareTo(o2.getPath()) ;
                }
            });
        } catch (IOException e) {
            Log.d(LOG_TAG,e.getMessage());
        }
    }

    public List<Key> getSubTree(boolean findChildren) {
        ArrayList<Key> list = new ArrayList<Key> ();
        TreeNode<String> currentNode=root;
        String pathString=getPathAsString().substring(1);
        String[] parts=pathString.split("/");
        for (String part:parts) {
            TreeNode<String> foundChildren = null;
            if (currentNode.getChildren() != null) {
                for (TreeNode<String> node : currentNode.getChildren()) {
                    String data = node.getData();
                    if (data.equals(part)) {
                        foundChildren = node;
                        break;
                    }
                }
                // Check if the part represent a children
                if (foundChildren != null) {
                    // Continue to visit the tree
                    currentNode=foundChildren;

                }
            }
        }

        for (TreeNode<String> node:currentNode.getChildren()) {

            if ((findChildren==true && node.getChildren()!=null && node.getChildren().size()>0) || (findChildren==false && (node.getChildren()==null || node.getChildren().size()==0))) {
                String keyString = pathString + "/" + node.getData();
                Key key = item_map.get(keyString);
                if (key != null) {
                    list.add(key);
                }
            }

        }
        Collections.sort(list, new Comparator<Key>(){
            public int compare(Key o1, Key o2){
                return o1.getPath().compareTo(o2.getPath()) ;
            }
        });


        return list;
    }

    public List<Key> getChildren() {
        return getSubTree(true);
    }

    public List<Key> getLeaf() {
        Log.d(LOG_TAG,"Leaf:");
        List<Key> list=getSubTree(false);
        for(Key key:list) {
            Log.d(LOG_TAG,key.getPath());
        }
        return list;
    }

    public String getPathAsString() {
        String result="/";
        for (String part:path) {
            result+=("/"+part);
        }
        return result;
    }
}
