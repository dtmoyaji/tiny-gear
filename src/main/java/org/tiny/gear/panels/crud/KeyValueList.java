package org.tiny.gear.panels.crud;

import java.util.ArrayList;

/**
 *
 * @author dtmoyaji
 */
public class KeyValueList extends ArrayList<KeyValue> {

    public KeyValue get(String key) {
        KeyValue rvalue = null;
        for (KeyValue kv : this) {
            if (kv.getKey().equals(key)) {
                rvalue = kv;
                break;
            }
        }
        return rvalue;
    }

    public boolean contains(String key) {
        boolean rvalue = false;
        for (KeyValue keyvalue : this) {
            if(keyvalue.getKey().equals(key)){
                rvalue = true;
                break;
            }
        }
        
        return rvalue;
    }
    
    @Override
    public boolean add(KeyValue keyValue){
        keyValue.setParent(this);
        return super.add(keyValue);
    }

}
