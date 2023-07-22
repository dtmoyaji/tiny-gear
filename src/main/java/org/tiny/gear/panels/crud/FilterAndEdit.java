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
package org.tiny.gear.panels.crud;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.tiny.datawrapper.Column;
import org.tiny.datawrapper.IJdbcSupplier;
import org.tiny.datawrapper.Table;

/**
 * @author bythe
 */
abstract public class FilterAndEdit extends Panel {
    
    private KeyValueList currentKeyValueList;

    private DataTableView dataTableView;

    private RecordEditor recordEditor;

    public FilterAndEdit(String id, Table table, IJdbcSupplier jdbcSupplier) {
        super(id);

        this.dataTableView = new DataTableView("dataTableView", table, jdbcSupplier) {
            @Override
            public Class<? extends Panel> getExtraColumn() {
                return null;
            }

            @Override
            public void onRowClicked(AjaxRequestTarget target, KeyValueList modelObject) {
                FilterAndEdit.this.onRowClicked(target, modelObject);
            }

            @Override
            public void beforeConstructView(Table myTable) {
                FilterAndEdit.this.beforeConstructDataTableView(myTable, FilterAndEdit.this.dataTableView);
            }
        };
        this.dataTableView.setOutputMarkupId(true);
        this.add(this.dataTableView);

        this.recordEditor = new RecordEditor("recordEditor") {

            @Override
            public void onSubmit(AjaxRequestTarget target, Table targetTable) {
                super.onSubmit(target, targetTable);
                FilterAndEdit.this.reloadRecordEditor(target);
                DataTableView dataTableView = FilterAndEdit.this.dataTableView;
                dataTableView.redraw(dataTableView.getConditions());
                target.add(dataTableView);
            }

            @Override
            public void onCancel(AjaxRequestTarget target, Table targetTable) {
            }

            @Override
            public void beforeConstructView(Table myTable) {
                FilterAndEdit.this.beforeConstructRecordEditor(
                        myTable,
                        FilterAndEdit.this.recordEditor
                );
            }
        };
        this.add(this.recordEditor);
        this.recordEditor.setOutputMarkupId(true);
        this.recordEditor.buildForm(table);

    }
    
    public void reloadRecordEditor(AjaxRequestTarget target){
        this.onRowClicked(target, currentKeyValueList);
    }

    private void onRowClicked(AjaxRequestTarget target, KeyValueList modelObject) {
        
        this.currentKeyValueList = modelObject;

        try {
            // キーカラムとキー値の取得
            String primaryKeyValue = "";
            String primaryColumn = "";
            for (KeyValue keyValue : modelObject) {
                if (keyValue.isPrimaryKey()) {
                    primaryColumn = (String) keyValue.getKey();
                    primaryKeyValue = (String) keyValue.getValue();
                    break;
                }
            }

            // キー値からResultSetを取得してレコードエディタに受け渡し
            Table targetTable = (Table) this.dataTableView.getTable().clone();
            targetTable.clearValues();
            Column primaryKeyColumn = targetTable.get(primaryColumn);

            try (ResultSet rs = targetTable.select(primaryKeyColumn.sameValueOf(primaryKeyValue))) {
                rs.next();
                this.recordEditor.clearValues();
                this.recordEditor.setValues(rs);
            }
            target.add(this.recordEditor);
        } catch (SQLException ex) {
            Logger.getLogger(FilterAndEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * データテーブルを構築する前にテーブル設定などに変更を加える時に使用する。
     *
     * @param myTable
     */
    public abstract void beforeConstructDataTableView(Table myTable, DataTableView dataTableView);

    /**
     * データテーブルを構築する前にテーブル設定などに変更を加える時に使用する。
     *
     * @param myTable
     */
    public abstract void beforeConstructRecordEditor(Table myTable, RecordEditor recordEditor);

}
