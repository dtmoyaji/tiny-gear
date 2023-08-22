package org.tiny.gear.panels.crud;

import java.io.Serializable;

/**
 *
 * @author dtmoyaji
 */
public class KeyValue implements Serializable{
    
    private String key;
    
    private String value;
    
    private KeyValueList parent;
    
    private boolean primaryKey;
    
    public KeyValue(){
        
    }
    
    public KeyValue(String key, String value){
        this.key = key;
        this.value = value;
    }
    
    public void setKey(String key){
        this.key = key;
    }
    
    public String getKey(){
        return this.key;
    }
    
    public void setValue(String value){
        this.value = value;
    }
    
    public String getValue(){
        return value;
    }
    
    public void setParent(KeyValueList parent){
        this.parent = parent;
    }
    
    public KeyValueList getParent(){
        return this.parent;
    }
    
    public void setPrimaryKey(boolean b){
        this.primaryKey = b;
    }
    
    public boolean isPrimaryKey(){
        return this.primaryKey;
    }
    
}
