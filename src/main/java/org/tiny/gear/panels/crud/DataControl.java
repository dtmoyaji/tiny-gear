package org.tiny.gear.panels.crud;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.tiny.datawrapper.Column;
import org.tiny.datawrapper.RelationInfo;
import org.tiny.datawrapper.Table;
import org.tiny.gear.GearApplication;
import org.tiny.gear.model.Attribute;
import org.tiny.gear.panels.crud.ColumnView.AbstractColumnView;
import org.tiny.gear.panels.crud.ColumnView.SimpleRelationSelector;
import org.tiny.gear.panels.crud.ColumnView.VisibleTypeDate;
import org.tiny.gear.panels.crud.ColumnView.VisibleTypeLabel;
import org.tiny.gear.panels.crud.ColumnView.VisibleTypeText;
import org.tiny.gear.panels.crud.ColumnView.VisibleTypeTextArea;
import org.wicketstuff.datetime.markup.html.form.DateTextField;

/**
 * カラムの編集用。
 *
 * @author dtmoyaji
 */
public class DataControl extends Panel {

    public static final int ESCAPE = 0;
    public static final int UNESCAPE = 1;

    private Label columnCaption;

    private TextField columnValueTextField;

    private TextArea columnValueTextArea;

//    private Label columnValueLabel;
    private DateTextField columnDateTextField;

    private Component component;
    private Model<String> fieldData;

    private AbstractColumnView colView;

    private Column targetColumn;

    public DataControl(String id, Column column) {
        super(id);

        this.setOutputMarkupId(true);
        this.targetColumn = column;
        //System.out.println("SERVER_TYPE: " + column.getTable().getServerType());

        String caption = this.targetColumn.getTable().getColumnLogicalName(this.targetColumn);
        this.columnCaption = new Label("columnCaption", Model.of(caption));
        this.add(this.columnCaption);

        // AbstractColumnView 系のコントロール
        this.colView = null;
        boolean defaultControl;
        switch (this.targetColumn.getVisibleType()) {
            case Column.VISIBLE_TYPE_LABEL:
                this.colView = new VisibleTypeLabel("columnValuePanel", new Model(this.targetColumn));
                break;
            case Column.VISIBLE_TYPE_DATE:
                this.colView = new VisibleTypeDate("columnValuePanel", new Model(this.targetColumn));
                break;
            case Column.VISIBLE_TYPE_TEXT:
                defaultControl = true;
                if (this.targetColumn.getType().equals(Integer.class.getSimpleName())) {
                    if (this.targetColumn.hasRelation()) {//リレーションがあるとき
                        RelationInfo rinfo = (RelationInfo) this.targetColumn.get(0);
                        Table relTable = ((GearApplication) this.getApplication()).getCachedTable(rinfo.getTableClass());
                        for (Column relcol : relTable) {
                            if (relcol.getAttributes().containsKey(Attribute.COLUMN_FOR_SEARCH)) {
                                System.out.println(relTable.getClass().getSimpleName() + " - " + relcol.getJavaName());
                            }
                        }
                        this.colView = new SimpleRelationSelector("columnValuePanel", new Model(this.targetColumn));
                        defaultControl = false;
                    }
                }
                if (defaultControl) {
                    this.colView = new VisibleTypeText("columnValuePanel", new Model(this.targetColumn));
                }
                break;
            case Column.VISIBLE_TYPE_TEXTAREA:
                defaultControl = true;
                if (this.targetColumn.getType().equals(Integer.class.getSimpleName())) {
                    if (this.targetColumn.hasRelation()) {//リレーションがあるとき
                        RelationInfo rinfo = (RelationInfo) this.targetColumn.get(0);
                        Table relTable = ((GearApplication) this.getApplication()).getCachedTable(rinfo.getTableClass());
                        for (Column relcol : relTable) {
                            if (relcol.getAttributes().containsKey(Attribute.COLUMN_FOR_SEARCH)) {
                                System.out.println(relTable.getClass().getSimpleName() + " - " + relcol.getJavaName());
                            }
                        }
                    }
                }
                if (defaultControl) {
                    this.colView = new VisibleTypeTextArea("columnValuePanel", new Model(this.targetColumn));
                }
                break;
            default:
                this.colView = new VisibleTypeLabel("columnValuePanel", new Model(this.targetColumn));
                break;
        }
        this.add(this.colView);

        // 必要となりそうなコントロールは全て格納しデータモデルを割り当てる。
        this.fieldData = new Model("");
        if (this.targetColumn.getValue() != null) {
            this.fieldData = new Model(this.targetColumn.getValue().toString());
        }
        if (this.component == null) {
            this.component = this.colView;
        }
        this.component.setVisible(true);
        this.component.setOutputMarkupId(true);
    }

    public Column getColumn() {
        return this.targetColumn;
    }

    public void setValue(String value) {
        if (this.component instanceof AbstractColumnView) {
            AbstractColumnView cview = (AbstractColumnView) this.component;
            cview.setColumnValue(value);
        } else {
            this.component.setDefaultModelObject(value);
        }
    }

    public String getValue(int escaping) {
        String rvalue = null;

        if (this.component instanceof AbstractColumnView) {
            AbstractColumnView cview = (AbstractColumnView) this.component;
            rvalue = cview.getColumnValue();
        } else {
            switch (escaping) {
                case DataControl.ESCAPE:
                    this.component.setEscapeModelStrings(true);
                    rvalue = this.component.getDefaultModelObjectAsString();
                    break;
                case DataControl.UNESCAPE:
                    this.component.setEscapeModelStrings(false);
                    rvalue = this.component.getDefaultModelObjectAsString();
                    break;
            }
            this.component.setEscapeModelStrings(false);
        }
        return rvalue;
    }

    public String getValue() {
        return this.getValue(DataControl.UNESCAPE);
    }

    public Component getVisibleComponent() {
        return this.component;
    }

}
