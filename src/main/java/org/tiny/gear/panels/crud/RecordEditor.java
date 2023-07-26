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

    private AjaxButton submit;

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
            }

        };
        this.editorForm.add(this.controls);

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
            if (col.getSplitedName().equals("LAST_ACCESS")) {
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
}
