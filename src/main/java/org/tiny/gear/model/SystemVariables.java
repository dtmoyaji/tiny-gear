package org.tiny.gear.model;

import org.tiny.datawrapper.Column;
import org.tiny.datawrapper.CurrentTimestamp;
import org.tiny.datawrapper.IncrementalKey;
import org.tiny.datawrapper.ShortFlagZero;
import org.tiny.datawrapper.Table;
import org.tiny.datawrapper.TinyDatabaseException;
import org.tiny.datawrapper.annotations.LogicalName;

/**
 * システム変数の格納先
 * @author bythe
 */
@LogicalName("システム変数")
public class SystemVariables extends Table{
    
    @LogicalName("ID")
    public IncrementalKey Id;
    
    @LogicalName("作成日")
    public CurrentTimestamp Stamp;
    
    @LogicalName("更新日")
    public CurrentTimestamp LastAccess;
    
    @LogicalName("無効フラグ")
    public ShortFlagZero Disble;
    
    @LogicalName("キー")
    public Column<String> Key;
    
    @LogicalName("値")
    public Column<String> Value;
    
    @LogicalName("備考")
    public Column<String> Memo;

    @Override
    public void defineColumns() throws TinyDatabaseException {
        
        this.Id.setPrimaryKey(false)
                .setVisibleType(Column.VISIBLE_TYPE_LABEL);
        
        this.Stamp.setDefault(Column.DEFAULT_TIMESTAMP)
                .setVisibleType(Column.VISIBLE_TYPE_LABEL);
                
        this.LastAccess.setDefault(Column.DEFAULT_TIMESTAMP)
                .setVisibleType(Column.VISIBLE_TYPE_LABEL);
        
        this.Key.setAllowNull(false)
                .setLength(Column.SIZE_256)
                .setVisibleType(Column.VISIBLE_TYPE_TEXT);
        
        this.Value.setAllowNull(false)
                .setLength(Column.SIZE_512)
                .setVisibleType(Column.VISIBLE_TYPE_TEXT);
    }
    
}
