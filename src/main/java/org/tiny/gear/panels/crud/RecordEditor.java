package org.tiny.gear.panels.crud;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.tiny.datawrapper.Column;
import org.tiny.datawrapper.CurrentTimestamp;
import org.tiny.datawrapper.Table;

/**
 *
 * @author bythe
 */
public abstract class RecordEditor extends DataTableInfoPanel {

    public static final int MODE_EDIT = 1;
    public static final int MODE_READONLY = 1;

    private ListView<Column> controls;
    private ArrayList<DataControl> dataControls = new ArrayList<>();

    /**
     * 新規
     */
    private AjaxButton btnNew;

    /**
     * 複製
     */
    private AjaxButton clone;

    /**
     * 更新
     */
    private AjaxButton submit;

    /**
     * キャンセル
     */
    private AjaxButton cancel;

    private Form editorForm;

    public RecordEditor(String id) {
        super(id);
    }

    /**
     * テーブルの設計に基づいて、フォームを自動生成する。
     *
     * @param targetTable
     */
    public void buildForm(Table targetTable) {

        this.beforeConstructView(targetTable);

        this.setTable(targetTable);
        this.dataControls.clear();

        this.editorForm = new Form("editorForm");
        this.add(this.editorForm);

        this.controls = new ListView<Column>("controls", this.targetTable) {

            @Override
            protected void populateItem(ListItem<Column> item) {
                Column col = item.getModelObject();
                DataControl dcol = new DataControl("dataControl", col);
                item.add(dcol);
                RecordEditor.this.dataControls.add(dcol);
                RecordEditor.this.afterConstructView(targetTable);
            }

        };
        this.editorForm.add(this.controls);

        this.btnNew = new AjaxButton("btnNew") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                RecordEditor.this.onBtnNew(target, targetTable);
            }
        };
        this.editorForm.add(this.btnNew);

        this.clone = new AjaxButton("clone") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                RecordEditor.this.onClone(target, targetTable);
            }
        };
        this.editorForm.add(this.clone);

        // 登録ボタン
        this.submit = new AjaxButton("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                RecordEditor.this.onSubmit(target, targetTable);
            }
        };
        this.editorForm.add(this.submit);

        // キャンセルボタン
        this.cancel = new AjaxButton("cancel") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                RecordEditor.this.onCancel(target, targetTable);
            }
        };
        this.editorForm.add(this.cancel);

        this.afterConstructView(targetTable);
    }

    /**
     * SQLの結果をResultSetで渡し、コントロールに表示する。
     *
     * @param rs
     */
    public void setValues(ResultSet rs) {
        for (Column col : this.targetTable) {
            col.setValue(col.of(rs));
        }
    }

    /**
     * コントロールに表示している値を消去する。
     */
    public void clearValues() {
        this.targetTable.clearValues();
    }

    public ArrayList<DataControl> getDataControls() {
        return this.dataControls;
    }

    /**
     * 更新ボタンを押したとき
     *
     * @param target
     */
    public void onSubmit(AjaxRequestTarget target, Table targetTable) {
        ArrayList<DataControl> controls = this.getDataControls();
        targetTable.clearValues();
        boolean insert = false;
        for (DataControl control : controls) {
            String data = "";
            Column col = control.getColumn();
            if (col.getClass().getName().equals(CurrentTimestamp.class.getName())) {
                control.setValue(
                        LocalDateTime.now().format(
                                DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        )
                );
            }
            if (col.isPrimaryKey() && col.getValue() == null) { // 主キーに値がないときは、インサート文
                insert = true;
                targetTable.get(control.getColumn().getName()).setValue(null);
            } else {
                data = control.getValue();
                targetTable.get(control.getColumn().getName()).setValue(data);
            }
        }
        targetTable.setDebugMode(true);
        if (insert) {
            targetTable.insert();
        } else {
            targetTable.merge();
        }
        target.add(this);
    }

    /**
     * キャンセルボタンを押したとき
     *
     * @param target
     */
    public abstract void onCancel(AjaxRequestTarget target, Table targetTable);

    /**
     * 新規ボタンが押された。 フォームのデータを全部空にする。 defaultValueを持つフィールドにはDefault値を入れる。
     *
     * @param target
     * @param targetTable
     */
    private void onBtnNew(AjaxRequestTarget target, Table targetTable) {
        this.targetTable.clearValues();
        target.add(this);
    }
    

    /**
     * 複製ボタンが押された。 primaryKeyだけ空欄にする。
     *
     * @param target
     * @param targetTable
     */
    private void onClone(AjaxRequestTarget target, Table targetTable) {
        for(Column column: this.targetTable){
            if(column.isPrimaryKey()){
                column.clearValue();
            }
        }
        target.add(this);
    }

    public DataControl getDataControl(String columnName) {
        DataControl rvalue = null;
        for (DataControl control : this.dataControls) {
            if (control.getColumn().getSplitedName().equals(columnName)) {
                rvalue = control;
                break;
            }
        }
        return rvalue;
    }

}
