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
 * レコードを検索し、編集する。
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

            @Override
            public void afterConstructView(Table myTable) {
                FilterAndEdit.this.afterConstructDataTableView(myTable, FilterAndEdit.this.dataTableView);
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

            @Override
            public void afterConstructView(Table myTable) {
                FilterAndEdit.this.afterConstructRecordEditor(myTable, FilterAndEdit.this.recordEditor);
            }
        };
        this.add(this.recordEditor);
        this.recordEditor.setOutputMarkupId(true);
        this.recordEditor.buildForm(table);

    }
    
    /**
     * 
     * @param target 
     */
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
     * @param dataTableView
     */
    public abstract void beforeConstructDataTableView(Table myTable, DataTableView dataTableView);

    /**
     * データテーブルを構築した後にテーブル設定などに変更を加える時に使用する。
     *
     * @param myTable
     * @param dataTableView
     */
    public abstract void afterConstructDataTableView(Table myTable, DataTableView dataTableView);

    /**
     * レコードエディタを構築する前にテーブル設定などに変更を加える時に使用する。
     *
     * @param myTable
     * @param recordEditor
     */
    public abstract void beforeConstructRecordEditor(Table myTable, RecordEditor recordEditor);

    /**
     * レコードエディタを構築した後にテーブル設定などに変更を加える時に使用する。
     *
     * @param myTable
     * @param recordEditor
     */
    public abstract void afterConstructRecordEditor(Table myTable, RecordEditor recordEditor);

}
