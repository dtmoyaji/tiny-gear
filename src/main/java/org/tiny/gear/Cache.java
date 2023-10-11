package org.tiny.gear;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tiny.datawrapper.TinyDatabaseException;
import org.tiny.gear.model.ObjectCacheInfo;

/**
 * キャッシュコンテナ GearApplication内で使用するオブジェクトキャッシュのテンプレートクラス。
 *
 * @author dtmoyaji
 * @param <T> 格納するオブジェクト
 */
public abstract class Cache<T> extends HashMap<String, T> {

    private String cachType;

    private GearApplication app;
    private ObjectCacheInfo objectCachInfo;

    private Class[] constParam;

    public Cache() {
        super();
    }

    public void initCache(GearApplication app, String cacheType) {
        this.clear();
        this.app = app;
        this.cachType = cachType;
        this.objectCachInfo = new ObjectCacheInfo();
        this.objectCachInfo.alterOrCreateTable(this.app.getJdbc());
    }

    /**
     * アプリ起動時にデータベースからキャッシュ情報を読み込んで、オブジェクトを復元格納する。
     *
     * @param app GearApplication
     * @param cachType キャッシュの種別: View | Table
     * @param constParam オブジェクトのコンストラクタ引数で使われるクラス
     */
    public void sync(GearApplication app, String cachType, Class... constParam) {
        this.initCache(app, cachType);
        this.constParam = constParam;

        HashMap<String, Short> instanceResult = new HashMap<>();

        String key = "";
        try (ResultSet rs = this.objectCachInfo.getTypeOf(this.cachType)) {
            while (rs.next()) {
                key = this.objectCachInfo.ObjectName.of(rs);
                T data = this.initializeObject(key, this.constParam);
                if (data == null) {
                    instanceResult.put(key, (short) 1);
                } else {
                    instanceResult.put(key, (short) 0);
                    super.put(key, data);
                }
            }
        } catch (SQLException | SecurityException ex) {
            instanceResult.put(key, (short) 1);
        }

        ObjectCacheInfo oci = new ObjectCacheInfo();
        oci.setJdbc(this.app.getJdbc());
        instanceResult.forEach((String szkey, Short result) -> {
            if (result == (short) 1) {
                oci.Disable.setValue(result);
                try {
                    oci.update(
                            oci.ObjectName.sameValueOf(szkey)
                    );
                } catch (TinyDatabaseException ex) {
                    Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

    }

    protected abstract T initializeObject(String key, Class[] constParam);

    @Override
    public T put(String key, T data) {
        T rvalue = super.put(key, data);
        
        if(key.equals(this.objectCachInfo.getClass().getName())){
            return rvalue;
        }

        this.objectCachInfo.clearValues();
        this.objectCachInfo.merge(
                this.objectCachInfo.ObjectName.setValue(key),
                this.objectCachInfo.ObjectType.setValue(this.cachType)
        );

        return rvalue;
    }

    public void removeFromServer() {
        this.objectCachInfo.truncate();
    }

    protected abstract T onNewInstance(Constructor constructor);

}
