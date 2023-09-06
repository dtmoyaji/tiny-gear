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
import org.tiny.datawrapper.Condition;
import org.tiny.datawrapper.CurrentTimestamp;
import org.tiny.datawrapper.Table;
import org.tiny.gear.scenes.AbstractView;

/**
 *
 * @author dtmoyaji
 */
public abstract class RecordEditor extends DataTableInfoPanel {

    public static final int MODE_EDIT = 1;
    public static final int MODE_READONLY = 1;

    public static final String ALLOW_DELETE = "AllowDelete";

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
    private AjaxButton delete;

    private Form editorForm;

    private AbstractView parentView;

    public RecordEditor(String id) {
        super(id);
    }

    public void buildForm(AbstractView parentView) {
        this.parentView = parentView;
        this.removeAll();
        this.buildForm(this.getTable());
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
                RecordEditor.this.onNew(target, targetTable);
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

        // 削除ボタン
        this.delete = new AjaxButton("delete") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                RecordEditor.this.onDelete(target, targetTable);
            }
        };
        if (this.parentView != null) {
            this.delete.setVisible(this.isAllowDelete());
        }
        this.editorForm.add(this.delete);

        this.afterConstructView(targetTable);
    }

    /**
     * SQLの結果をResultSetで渡し、コントロールに表示する。
     *
     * @param rs
     */
    public void setValues(ResultSet rs) {
        this.dataControls.clear();
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
     * @param targetTable
     */
    public void onSubmit(AjaxRequestTarget target, Table targetTable) {
        if (this.beforeSubmit(target, targetTable, this.getDataControls())) {
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
                if ((col.isPrimaryKey() && control.getValue() == null)
                        || col.isPrimaryKey() && control.getValue().length() < 1) { // 主キーに値がないときは、インサート文
                    insert = true;
                    targetTable.get(control.getColumn().getName()).setValue(null);
                } else {
                    data = control.getValue();
                    targetTable.get(control.getColumn().getName()).setValue(data);
                }
            }

            targetTable.setDebugMode(false);
            if (insert) {
                if (!targetTable.insert()) {
                    System.out.println("INSERT ERROR");
                }
            } else {
                targetTable.merge();
            }

            this.delete.setVisible(this.isAllowDelete());
            target.add(this.delete);
        }
        target.add(this);
        this.afterSubmit(target, targetTable, dataControls);
    }

    /**
     * 更新前のチェックなどに使用する。
     *
     * @param target
     * @param targetTable
     * @return
     */
    public boolean beforeSubmit(AjaxRequestTarget target, Table targetTable, ArrayList<DataControl> dataControls1) {
        return true;
    }
    
    public void afterSubmit(AjaxRequestTarget target, Table targetTable, ArrayList<DataControl> dataControls){
    }

    /**
     * キャンセルボタンを押したとき
     *
     * @param target
     */
    public void onDelete(AjaxRequestTarget target, Table targetTable) {
        ArrayList<DataControl> controls = this.getDataControls();
        ArrayList<Condition> conds = new ArrayList<>();
        for (DataControl cntl : controls) {
            if (targetTable.get(cntl.getColumn().getName()).isPrimaryKey()) {
                System.out.println(cntl.getColumn().getName() + " " + cntl.getValue());
                conds.add(cntl.getColumn().sameValueOf(cntl.getValue()));
            }
        }
        Condition[] cnd = new Condition[conds.size()];
        targetTable.delete(conds.toArray(cnd));
        this.targetTable.clearValues();
        target.add(this);
    }

    /**
     * 新規ボタンが押された。 フォームのデータを全部空にする。 defaultValueを持つフィールドにはDefault値を入れる。
     *
     * @param target
     * @param targetTable
     */
    public void onNew(AjaxRequestTarget target, Table targetTable) {
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
        for (Column column : this.targetTable) {
            if (column.isPrimaryKey()) {
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

    @Override
    protected void onAfterRender() {
        super.onAfterRender();
    }

    public boolean isAllowDelete() {
        String parentClassName = this.parentView.getClass().getSimpleName();
        String paramName = parentClassName + "." + RecordEditor.ALLOW_DELETE;
        Boolean rvalue = Boolean.valueOf(
                this.getGearApplication().getSystemVariable(paramName, "false")
        );
        return rvalue;
    }

}
