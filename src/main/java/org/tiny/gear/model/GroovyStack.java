package org.tiny.gear.model;

import org.tiny.datawrapper.Column;
import org.tiny.datawrapper.CurrentTimestamp;
import org.tiny.datawrapper.Table;
import org.tiny.datawrapper.annotations.LogicalName;

/**
 * Groovyスクリプトを格納するテーブル
 * @author dtmoyaji
 */
@LogicalName("GroovyScript")
public class GroovyStack extends Table {

    @LogicalName("スクリプト名")
    public Column<String> ScriptName;
    
    @LogicalName("更新日")
    public CurrentTimestamp LastAccess;
    
    @LogicalName("引数")
    public Column<String> VariableClasses;
    
    @LogicalName("GroovyScript")
    public Column<String> ScriptBody;
    
    @LogicalName("説明")
    public Column<String> Description;
    
    @Override
    public void defineColumns() {
        this.ScriptName.setAllowNull(false)
                .setLength(Column.SIZE_512)
                .setPrimaryKey(true);
        
        this.VariableClasses.setLength(Column.SIZE_2048);
        
        this.ScriptBody.setLength(Column.SIZE_4096)
                .setAllowNull(false).setVisibleType(Column.VISIBLE_TYPE_TEXTAREA);
        
        this.Description.setLength(Column.SIZE_2048);
    }
}

