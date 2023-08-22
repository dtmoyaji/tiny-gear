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
package org.tiny.gear;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import org.tiny.gear.model.ObjectCacheInfo;

/**
 *
 * @author bythe
 */
public abstract class Cache<T> extends HashMap<String, T> {

    private String cachType;

    private GearApplication app;
    private ObjectCacheInfo objectCachInfo;

    private Class[] constParam;
    
    public Cache(){
        super();
    }

    public void sync(GearApplication app, String cachType, Class... constParam) {
        this.clear();
        this.app = app;
        this.cachType = cachType;
        this.objectCachInfo = new ObjectCacheInfo();
        this.objectCachInfo.alterOrCreateTable(this.app.getJdbc());
        this.constParam = constParam;

        String key = "";
        try (ResultSet rs = this.objectCachInfo.getTypeOf(this.cachType)) {
            while (rs.next()) {
                key = this.objectCachInfo.ObjectName.of(rs);
                T data = this.initializeObject(key, constParam);
                super.put(key, data);
            }
        } catch (SQLException
                | SecurityException ex) {
            super.put(key, null);
        }
    }

    protected abstract T initializeObject(String key, Class[] constParam);

    @Override
    public T put(String key, T data) {
        T rvalue = super.put(key, data);

        this.objectCachInfo.clearValues();
        this.objectCachInfo.merge(
                this.objectCachInfo.ObjectName.setValue(key),
                this.objectCachInfo.ObjectType.setValue(this.cachType)
        );

        return rvalue;
    }
    
    public void removeFromServer(){
        this.objectCachInfo.truncate();
    }

    protected abstract T onNewInstance(Constructor constructor);

}
