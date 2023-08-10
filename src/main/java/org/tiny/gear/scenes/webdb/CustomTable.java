package org.tiny.gear.scenes.webdb;

import org.tiny.datawrapper.Column;
import org.tiny.datawrapper.CurrentTimestamp;
import org.tiny.datawrapper.ShortFlagZero;
import org.tiny.datawrapper.Table;
import org.tiny.datawrapper.annotations.LogicalName;

/**
 * Groovyで定義するカスタムテーブル
 *
 * @author bythe
 */
@LogicalName("カスタムテーブル")
public class CustomTable extends Table {

    @LogicalName("テーブル名")
    public Column<String> TableName;

    @LogicalName("バージョン")
    public Column<String> Version;
    
    @LogicalName("カスタムテーブル")
    public ShortFlagZero CustomTable;

    @LogicalName("テーブル定義")
    public Column<String> TableDef;

    @LogicalName("備考")
    public Column<String> Memo;

    @LogicalName("無効フラグ")
    public ShortFlagZero Disable;

    @LogicalName("更新日")
    public CurrentTimestamp LastAccess;

    @Override
    public void defineColumns() {
        
        this.TableName.setAllowNull(false).setLength(Column.SIZE_256)
                .setPrimaryKey(true);
        
        this.CustomTable.setDefault("0")
                .setAllowNull(false);
        
        this.TableDef.setLength(Column.SIZE_4096)
        .setAllowNull(false)
        .setVisibleType(Column.VISIBLE_TYPE_TEXTAREA);

        this.Version.setLength(Column.SIZE_32);

        this.Memo.setLength(Column.SIZE_2048);

    }
}
