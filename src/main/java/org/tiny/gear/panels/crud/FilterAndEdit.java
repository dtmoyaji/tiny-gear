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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.tiny.datawrapper.IJdbcSupplier;
import org.tiny.datawrapper.Table;

/**
 * @author bythe
 */
public class FilterAndEdit extends Panel{
    
    private DataTableView dataTableView;
    
    private RecordEditor recordEditor;
    
    public FilterAndEdit(String id, Table table,  IJdbcSupplier jdbcSupplier) {
        super(id);
        
        this.dataTableView = new DataTableView("dataTableView", table, jdbcSupplier){
            @Override
            public Class<? extends Panel> getExtraColumn() {
                return null;
            }

            @Override
            public void onRowClicked(AjaxRequestTarget target, KeyValueList modelObject) {
                String primaryKey = "";
                String primaryColumn = "";
                for (KeyValue keyValue : modelObject) {
                    if (keyValue.isPrimaryKey()) {
                        primaryColumn = (String) keyValue.getKey();
                        primaryKey = (String) keyValue.getValue();
                        break;
                    }
                }
                System.out.println("PRIMARY: " + primaryColumn + " = " + primaryKey);
            }
        };
        this.add(this.dataTableView);
        
        this.recordEditor = new RecordEditor("recordEditor"){
         
            @Override
            public void beforeFormBuild() {
            }

            @Override
            public void afterFormBuild() {
            }

            @Override
            public void onCancel(AjaxRequestTarget target, Table targetTable) {
            }
        };
        this.add(this.recordEditor);
        this.recordEditor.buildForm(table);
        
    }
    
}
