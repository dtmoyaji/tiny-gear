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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tiny.datawrapper.Jdbc;
import org.tiny.gear.model.ObjectCachInfo;

/**
 *
 * @author bythe
 */
public abstract class Cache<T> extends HashMap<String, T> {

    private String cachType;

    private Jdbc jdbc;
    private ObjectCachInfo objectCachInfo;
    
    private Class[] constParam;
    
    public Cache(Jdbc jdbc, String cachType, Class... constParam) {
        super();
        this.jdbc = jdbc;
        this.cachType = cachType;
        this.objectCachInfo = new ObjectCachInfo();
        this.objectCachInfo.alterOrCreateTable(this.jdbc);
        this.constParam = constParam;

        String key = "";
        try (ResultSet rs = this.objectCachInfo.getTypeOf(this.cachType)) {
            while (rs.next()) {
                key = this.objectCachInfo.ObjectName.of(rs);
                Class cls = Class.forName(key);
                Constructor constructor = cls.getConstructor(constParam);
                T data = (T) this.onNewInstance(constructor);
                super.put(key, data);
                afterNewInstance(key, data);
            }
        } catch (SQLException
                | ClassNotFoundException
                | NoSuchMethodException
                | SecurityException ex) {
                super.put(key, null);
        }
    }

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

    public T get(String key) {
        T rvalue = null;
        if (this.containsKey(key)) {
            //Logger.getLogger(this.getClass().getName())
            //        .log(Level.INFO, "Cache Name: {0} Type: {1} Hit.",
            //                new Object[]{key, this.cachType});
            rvalue = super.get(key);
        } else {
            try {
                Class<? extends T> cls = (Class<? extends T>) Class.forName(key);
                Constructor cst = cls.getConstructor(this.constParam);
                rvalue = (T) this.onNewInstance(cst);
                this.put(key, rvalue);
                afterNewInstance(key, rvalue);
            } catch (ClassNotFoundException
                    | NoSuchMethodException
                    | SecurityException
                    | IllegalArgumentException ex) {
                Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return rvalue;
    }
    
    protected abstract T onNewInstance(Constructor constructor);

    protected abstract void afterNewInstance(String key, T data);

}
