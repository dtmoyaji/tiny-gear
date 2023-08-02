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

import groovy.lang.GroovyShell;
import java.util.ArrayList;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.tiny.datawrapper.Column;
import org.tiny.datawrapper.IJdbcSupplier;
import org.tiny.datawrapper.Table;
import org.tiny.gear.panels.crud.DataControl;
import org.tiny.gear.panels.crud.DataTableView;
import org.tiny.gear.panels.crud.FilterAndEdit;
import org.tiny.gear.panels.crud.RecordEditor;
import org.tiny.gear.view.AbstractView;

/**
 *
 * @author bythe
 */
public class CustomTableEditView extends AbstractView {

    private CustomTable customTable;

    private FilterAndEdit filterAndEdit;

    public CustomTableEditView(IJdbcSupplier supplier) {
        super(supplier);
        this.customTable = new CustomTable();
        this.customTable.setAllowDeleteRow(true); // TODO: あとでfalseに変える。
        this.customTable.alterOrCreateTable(this.supplier.getJdbc());

        this.filterAndEdit = new FilterAndEdit("customTableEditor", this.customTable, this.supplier.getJdbc()) {
            @Override
            public void beforeConstructDataTableView(Table myTable, DataTableView dataTableView) {
                myTable.get(customTable.TableDef.getName()).setVisibleType(Column.VISIBLE_TYPE_HIDDEN);
            }

            @Override
            public void afterConstructDataTableView(Table myTable, DataTableView dataTableView) {
            }

            @Override
            public void beforeConstructRecordEditor(Table myTable, RecordEditor recordEditor) {
                myTable.get(customTable.LastAccess.getName()).setVisibleType(Column.VISIBLE_TYPE_LABEL);
            }

            @Override
            public void afterConstructRecordEditor(Table myTable, RecordEditor recordEditor) {
                DataControl control = recordEditor.getDataControl(customTable.TableDef.getSplitedName());
                if (control != null) {
                    control.getVisibleComponent().add(new AttributeModifier("style", Model.of("height: 12em;")));
                }
            }

            @Override
            public boolean beforeSubmit(AjaxRequestTarget target, Table targetTable, ArrayList<DataControl> dataControls) {
                boolean rvalue = false;
                for(DataControl control: dataControls){
                    if(control.getColumn().getName().equals(customTable.TableDef.getName())){
                        String def = control.getValue(DataControl.UNESCAPE);
                        GroovyShell shell = new GroovyShell();
                        Object obj = shell.evaluate(def);
                        if(obj instanceof Table){
                            ((Table)obj).alterOrCreateTable(CustomTableEditView.this.supplier.getJdbc());
                        }
                        rvalue = true;
                    }
                }
                return rvalue;
            }
        };
        this.add(this.filterAndEdit);
    }

    @Override
    public String getTitle() {
        return "カスタムテーブル編集";
    }

}
