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
package org.tiny.gear.scenes;

import org.tiny.datawrapper.Column;
import org.tiny.datawrapper.CurrentTimestamp;
import org.tiny.datawrapper.IncrementalKey;
import org.tiny.datawrapper.ShortFlagZero;
import org.tiny.datawrapper.Table;
import org.tiny.datawrapper.TinyDatabaseException;
import org.tiny.datawrapper.annotations.LogicalName;

/**
 * AbstractTableのインスタンス化する際に参照するためのテーブル。
 * @author dtmoyaji
 */
@LogicalName("AbstractTable用テーブル")
public class TablesForAbstractView extends Table{
    
    @LogicalName("ID")
    public IncrementalKey Id;
    
    @LogicalName("最終アクセス")
    public CurrentTimestamp LastAccess;
    
    @LogicalName("無効フラグ")
    public ShortFlagZero Disable;
    
    @LogicalName("ビュークラス名")
    public Column<String> ViewClassName;
    
    @LogicalName("テーブルクラス名")
    public Column<String> TableClassName;

    @Override
    public void defineColumns() throws TinyDatabaseException {
        
        this.Id.setPrimaryKey(false);
        
        this.ViewClassName.setLength(Column.SIZE_512)
                .setAllowNull(false)
                .setPrimaryKey(true);
        
        this.TableClassName.setLength(Column.SIZE_512)
                .setAllowNull(false)
                .setPrimaryKey(true);
    }
    
}
