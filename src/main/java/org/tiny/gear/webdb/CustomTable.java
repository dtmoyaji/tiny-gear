package org.tiny.gear.webdb;

import org.tiny.datawrapper.Column;
import org.tiny.datawrapper.CurrentTimestamp;
import org.tiny.datawrapper.IncrementalKey;
import org.tiny.datawrapper.ShortFlagZero;
import org.tiny.datawrapper.Table;
import org.tiny.datawrapper.annotations.LogicalName;

/**
 * Groovyで定義するカスタムテーブル
 *
 * @author bythe
 */
@LogicalName("カスタムテーブル")
class CustomTable extends Table {

    @LogicalName("テーブルID")
    public IncrementalKey tableId;

    @LogicalName("テーブル名")
    public Column<String> tableName;

    @LogicalName("バージョン")
    public Column<String> version;

    @LogicalName("テーブル定義")
    public Column<String> tableDef;

    @LogicalName("備考")
    public Column<String> memo;

    @LogicalName("無効フラグ")
    public ShortFlagZero disable;

    @LogicalName("更新日")
    public CurrentTimestamp updateDate;

    @Override
    public void defineColumns() {

        this.tableName.setAllowNull(false)
                .setLength(Column.SIZE_256);

        this.version.setLength(Column.SIZE_32);

        this.memo.setLength(Column.SIZE_2048);

    }
}
