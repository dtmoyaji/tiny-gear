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
package org.tiny.gear.scenes.webdb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.tiny.datawrapper.Column;
import org.tiny.datawrapper.Table;
import org.tiny.datawrapper.entity.TableInfo;
import org.tiny.gear.GearApplication;
import org.tiny.gear.GroovyTableBuilder;
import org.tiny.gear.model.SystemVariables;
import org.tiny.gear.panels.crud.DataControl;
import org.tiny.gear.panels.crud.DataTableView;
import org.tiny.gear.panels.crud.FilterAndEdit;
import org.tiny.gear.panels.crud.KeyValueList;
import org.tiny.gear.panels.crud.RecordEditor;
import org.tiny.gear.scenes.AbstractView;

/**
 *
 * @author bythe
 */
public class GenericTableEditView extends AbstractView {

    public static final String KEY_DEFAULT_ROWS_PER_PAGE = "GenericTableEditView.DefaultRowsPerPage";
    public static final String KEY_DEFAULT_ALLOW_DELETE = "GenericTableEditView.AllowDelete";

    private FilterAndEdit filterAndEdit;

    private DataTableView tableSelector;

    private SystemVariables systemVariables;

    public GenericTableEditView(GearApplication app) {
        super(app);
    }

    @Override
    public void redraw() {
        super.redraw();

        GroovyTableBuilder groovyTableBuilder = new GroovyTableBuilder(this.getGearApplication());
        Table table = groovyTableBuilder.createTable("TEST_TABLE");

        this.systemVariables = (SystemVariables) this.getTable(SystemVariables.class);

        this.filterAndEdit = new FilterAndEdit("genericTableEditor", table) {
            @Override
            public void beforeConstructDataTableView(Table myTable, DataTableView dataTableView) {
            }

            @Override
            public void afterConstructDataTableView(Table myTable, DataTableView dataTableView) {
            }

            @Override
            public void beforeConstructRecordEditor(Table myTable, RecordEditor recordEditor) {
            }

            @Override
            public void afterConstructRecordEditor(Table myTable, RecordEditor recordEditor) {
            }

            @Override
            public boolean beforeSubmit(AjaxRequestTarget target, Table targetTable, ArrayList<DataControl> dataControls) {
                return true;
            }
        };
        this.filterAndEdit.setOutputMarkupId(true);

        this.tableSelector = new DataTableView("tableSelector", this.getTable(TableInfo.class)) {
            @Override
            public Class<? extends Panel> getExtraColumn() {
                return null;
            }

            @Override
            public void onRowClicked(AjaxRequestTarget target, KeyValueList modelObject) {
                GenericTableEditView.this.onTableSelectorRowClicked(target, modelObject);
            }

            @Override
            public void beforeConstructView(Table myTable) {
                TableInfo tinfo = (TableInfo) myTable;
                for (Column column : tinfo) {
                    column.setVisibleType(Column.VISIBLE_TYPE_HIDDEN);
                }
                tinfo.TablePhisicalName.setVisibleType(Column.VISIBLE_TYPE_LABEL);
                tinfo.TableLogicalName.setVisibleType(Column.VISIBLE_TYPE_LABEL);
            }

            @Override
            public void afterConstructView(Table myTable) {
            }
        };
        this.tableSelector.setOutputMarkupId(true);
        this.add(this.tableSelector);

        // 表示する既定データ行数を取得する
        int defaultRowsPerPage = Integer.parseInt(
                this.getGearApplication().getSystemVariable(
                        GenericTableEditView.KEY_DEFAULT_ROWS_PER_PAGE,
                        "2"
                )
        );
        this.filterAndEdit.getDataTableView().setRowsPerPage(defaultRowsPerPage);
        this.filterAndEdit.getDataTableView().redraw();

        boolean allowDelete = Boolean.parseBoolean(
                this.getGearApplication().getSystemVariable(
                        GenericTableEditView.KEY_DEFAULT_ALLOW_DELETE,
                        "false"
                )
        );
        this.filterAndEdit.getRecordEditor().getTable().setAllowDeleteRow(allowDelete);
        this.filterAndEdit.getRecordEditor().buildForm();

        this.add(this.filterAndEdit);
    }

    @Override
    public String getTitle() {
        return "データ編集";
    }

    private void onTableSelectorRowClicked(AjaxRequestTarget target, KeyValueList modelObject) {
        TableInfo tinfo = (TableInfo) this.getGearApplication().getCachedTable(TableInfo.class);
        String tableName = modelObject.get(tinfo.TablePhisicalName.getSplitedName()).getValue();
        try (ResultSet rs = tinfo.select(tinfo.TablePhisicalName.sameValueOf(tableName))) {
            if (rs.next()) {
                String tableClassName = tinfo.TableClassName.of(rs);
                rs.close();
                Table newTarget = this.getGearApplication().getCachedTable(tableClassName);
                DataTableView dtv = this.filterAndEdit.getDataTableView();
                dtv.setTable(newTarget);
                dtv.redraw();
                RecordEditor re = this.filterAndEdit.getRecordEditor();
                re.removeAll();
                re.buildForm(newTarget);
                target.add(dtv);
                target.add(re);
                target.add(this.filterAndEdit);
            }
        } catch (SQLException ex) {
            Logger.getLogger(GenericTableEditView.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
