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

import java.util.ArrayList;

/**
 *
 * @author bythe
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
