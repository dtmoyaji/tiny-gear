package org.tiny.gear;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import org.tiny.gear.model.ObjectCacheInfo;

/**
 * キャッシュコンテナ
 * GearApplication内で使用するオブジェクトキャッシュのテンプレートクラス。
 * @author dtmoyaji
 * @param <T> 格納するオブジェクト
 */
public abstract class Cache<T> extends HashMap<String, T> {

    private String cachType;

    private GearApplication app;
    private ObjectCacheInfo objectCachInfo;

    private Class[] constParam;
    
    public Cache(){
        super();
    }

    /**
     * アプリ起動時にデータベースからキャッシュ情報を読み込んで、オブジェクトを復元格納する。
     * @param app GearApplication
     * @param cachType キャッシュの種別: View | Table
     * @param constParam オブジェクトのコンストラクタ引数で使われるクラス
     */
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
                T data = this.initializeObject(key, this.constParam);
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
