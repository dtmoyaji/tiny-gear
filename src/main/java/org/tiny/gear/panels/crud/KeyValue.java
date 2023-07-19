/*
 * Copyright 2023 bythe.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tiny.gear.panels.crud;

import java.io.Serializable;

/**
 *
 * @author bythe
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
