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
import org.apache.wicket.markup.html.panel.Panel;
import org.tiny.datawrapper.Column;
import org.tiny.datawrapper.Table;

/**
 *
 * @author bythe
 */
public abstract class RecordEditor extends Panel {

    public static int MODE_NEW = 0;

    public static int MODE_UPDATE = 1;

    private Table targetTable;

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

        this.targetTable = targetTable;
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

    public void setResultSet(ResultSet rs) {
        for (Column col : this.targetTable) {
            col.setValue(col.of(rs));
        }
    }

    public ArrayList<DataControl> getDataControls() {
        return this.dataControls;
    }

    /**
     * フォームを生成する前の処理を定義する。
     */
    public abstract void beforeFormBuild();

    /**
     * フォームを生成した後の処理を定義する。
     */
    public abstract void afterFormBuild();

    /**
     * 更新ボタンを押したとき
     *
     * @param target
     */
    public void onSubmit(AjaxRequestTarget target, Table targetTable) {
        ArrayList<DataControl> controls = this.getDataControls();
        targetTable.clearValues();
        for (DataControl control : controls) {
            String data = "";
            if (control.getColumn().getSplitedName().equals("LAST_ACCESS")) {
                control.setValue(
                        LocalDateTime.now().format(
                                DateTimeFormatter.ISO_LOCAL_DATE_TIME
                        )
                );
            }
            data = control.getValue();
            targetTable.get(control.getColumn().getName()).setValue(data);
        }
        //targetTable.setDebugMode(true);
        targetTable.merge();
        target.add(this);
    }

    /**
     * キャンセルボタンを押したとき
     *
     * @param target
     */
    public abstract void onCancel(AjaxRequestTarget target, Table targetTable);
}
