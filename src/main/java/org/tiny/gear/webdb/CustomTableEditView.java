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
package org.tiny.gear.webdb;

import org.tiny.datawrapper.Column;
import org.tiny.datawrapper.IJdbcSupplier;
import org.tiny.datawrapper.Table;
import org.tiny.gear.panels.crud.DataTableView;
import org.tiny.gear.panels.crud.FilterAndEdit;
import org.tiny.gear.panels.crud.RecordEditor;
import org.tiny.gear.view.AbstractView;

/**
 * 
 * @author bythe
 */
public class CustomTableEditView extends AbstractView{
    
    private CustomTable customTable;
    
    private FilterAndEdit filterAndEdit;
    
    public CustomTableEditView(IJdbcSupplier supplier){
        super(supplier);
        this.customTable = new CustomTable();
        this.customTable.alterOrCreateTable(this.supplier.getJdbc());
        
        this.filterAndEdit = new FilterAndEdit("customTableEditor", this.customTable, this.supplier.getJdbc()){
            @Override
            public void beforeConstructDataTableView(Table myTable, DataTableView dataTableView) {
                myTable.get(customTable.TableDef.getName()).setVisibleType(Column.VISIBLE_TYPE_HIDDEN);
            }

            @Override
            public void beforeConstructRecordEditor(Table myTable, RecordEditor recordEditor) {
                myTable.get(customTable.LastAccess.getName()).setVisibleType(Column.VISIBLE_TYPE_LABEL);
            }
        };
        this.add(this.filterAndEdit);
    }
    
  
    @Override
    public String getTitle() {
        return "カスタムテーブル編集";
    }
    
}
