package org.tiny.gear.model;

import java.sql.ResultSet;
import org.tiny.datawrapper.Column;
import org.tiny.datawrapper.Table;
import org.tiny.datawrapper.TinyDatabaseException;
import org.tiny.datawrapper.annotations.LogicalName;

/**
 *
 * @author dtmoyaji
 */
@LogicalName("キャッシュ情報")
public class ObjectCacheInfo extends Table {

    public static final String TYPE_CLASS = "class";
    public static final String TYPE_TALBE = "table";
    public static final String TYPE_SCENE = "scene";
    public static final String TYPE_VIEW = "view";

    @LogicalName("オブジェクト名")
    public Column<String> ObjectName;

    @LogicalName("オブジェクト種別")
    public Column<String> ObjectType;
    
    @LogicalName("初期化パラメータ")
    public Column<String> Initializer;

    @Override
    public void defineColumns() throws TinyDatabaseException {

        this.ObjectName.setLength(Column.SIZE_512)
                .setPrimaryKey(true)
                .setAllowNull(false);

        this.ObjectType.setLength(Column.SIZE_64)
                .setAllowNull(false);
        
    }

    public ResultSet getTypeOf(String typeName) {
        return this.select(this.ObjectType.sameValueOf(typeName));
    }

}
