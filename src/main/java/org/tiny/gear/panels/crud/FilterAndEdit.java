package org.tiny.gear.panels.crud;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.Panel;
import org.tiny.datawrapper.Column;
import org.tiny.datawrapper.Table;
import org.tiny.gear.panels.IPanelPopupper;
import org.tiny.gear.panels.PopUpPanel;

/**
 * レコードを検索し、編集する。
 */
abstract public class FilterAndEdit extends Panel implements IPanelPopupper{
    
    private KeyValueList currentKeyValueList;
    
    private DataTableView dataTableView;

    private RecordEditor recordEditor;
    
    public FilterAndEdit(String id, Table table) {
        super(id);
        
        this.dataTableView = new DataTableView("dataTableView", table) {
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
            public boolean beforeSubmit(AjaxRequestTarget target, Table targetTable, ArrayList<DataControl> dataControls){
                return FilterAndEdit.this.beforeSubmit(target, targetTable, dataControls);
            }

            @Override
            public void onSubmit(AjaxRequestTarget target, Table targetTable) {
                super.onSubmit(target, targetTable);
                DataTableView dataTableView = FilterAndEdit.this.dataTableView;
                dataTableView.redraw(dataTableView.getConditions());
                target.add(dataTableView);
                FilterAndEdit.this.reloadRecordEditor(target);
            }

            @Override
            public void onDelete(AjaxRequestTarget target, Table targetTable) {
                super.onDelete(target, targetTable);
                FilterAndEdit.this.reloadRecordEditor(target);
                DataTableView dataTableView = FilterAndEdit.this.dataTableView;
                dataTableView.redraw(dataTableView.getConditions());
                target.add(dataTableView);
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
        // 最初の行を取得する
        if(this.currentKeyValueList==null){
            this.currentKeyValueList = this.dataTableView.getFirstKeyValueList();
        }
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
                rs.close();
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

    /**
     * 更新ボタンを押した直後の処理（確認などに使う）
     * @param target
     * @param targetTable
     * @return 
     */
    public abstract boolean beforeSubmit(AjaxRequestTarget target, Table targetTable, ArrayList<DataControl> dataControls);
    
    public DataTableView getDataTableView(){
        return this.dataTableView;
    }
    
    public RecordEditor getRecordEditor(){
        return this.recordEditor;
    }

    @Override
    public void setPopUpPanel(PopUpPanel panel) {
        this.dataTableView.setPopUpPanel(panel);
    }

    @Override
    public PopUpPanel getPopUpPanel() {
        return this.dataTableView.getPopUpPanel();
    }

}
