package org.tiny.gear.model;

import org.tiny.datawrapper.Column;
import org.tiny.datawrapper.CurrentTimestamp;
import org.tiny.datawrapper.IncrementalKey;
import org.tiny.datawrapper.ShortFlagZero;
import org.tiny.datawrapper.Table;
import org.tiny.datawrapper.TinyDatabaseException;

/**
 * システム変数の格納先
 * @author bythe
 */
public class SystemVariables extends Table{
    
    public IncrementalKey Id;
    
    public CurrentTimestamp Stamp;
    
    public CurrentTimestamp Mdate;
    
    public ShortFlagZero Disble;
    
    public Column<String> Key;
    
    public Column<String> Value;

    @Override
    public void defineColumns() throws TinyDatabaseException {
        
        this.Id.setPrimaryKey(false);
        
        this.Stamp.setDefault(Column.DEFAULT_TIMESTAMP);
        this.Mdate.setDefault(Column.DEFAULT_TIMESTAMP);
        
        this.Key.setAllowNull(false)
                .setLength(Column.SIZE_256);
        
        this.Value.setAllowNull(false)
                .setLength(Column.SIZE_512);
    }
    
}
