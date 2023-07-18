package org.tiny.gear.panels.crud;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.tiny.datawrapper.Column;

/**
 * カラムの編集用。
 *
 * @author bythe
 */
public class DataControl extends Panel {

    private Label columnCaption;

    private TextField columnValueTextField;

    private TextArea columnValueTextArea;

    private Label columnValueLabel;

    private Component visibleComponent;
    private Model<String> fieldData;

    private Column targetColumn;

    public DataControl(String id, Column column) {
        super(id);

        this.targetColumn = column;

        String caption = this.targetColumn.getTable().getColumnLogicalName(this.targetColumn);
        this.columnCaption = new Label("columnCaption", Model.of(caption));
        this.add(this.columnCaption);

        // 必要となりそうなコントロールは全て格納しデータモデルを割り当てる。
        this.fieldData = new Model("");
        if (this.targetColumn.getValue() != null) {
            this.fieldData = new Model(this.targetColumn.getValue().toString());
        }
        this.columnValueLabel = new Label("columnValueLabel", this.fieldData);
        this.add(this.columnValueLabel);
        this.columnValueTextField = new TextField("columnValueTextField", this.fieldData);
        this.add(this.columnValueTextField);
        this.columnValueTextArea = new TextArea("columnValueTextArea", this.fieldData);
        this.add(this.columnValueTextArea);

        // 一旦全部不可視に設定する。
        this.columnValueLabel.setVisible(false);
        this.columnValueTextField.setVisible(false);
        this.columnValueTextArea.setVisible(false);

        // データの入出力に対応するコントロールを抽象型のvisibleComponentでポイントする。
        // データIOはこれを使って行う。
        switch (this.targetColumn.getVisibleType()) {
            case Column.VISIBLE_TYPE_LABEL:
                this.visibleComponent = this.columnValueLabel;
                this.columnValueLabel.setVisible(true);
                break;
            case Column.VISIBLE_TYPE_TEXT:
                this.visibleComponent = this.columnValueTextField;
                this.columnValueTextField.setVisible(true);
                break;
            case Column.VISIBLE_TYPE_TEXTAREA:
                this.visibleComponent = this.columnValueTextArea;
                this.columnValueTextArea.setVisible(true);
                break;
        }
    }

    public Column getColumn() {
        return this.targetColumn;
    }

    public void setValue(String value) {
        this.visibleComponent.setDefaultModelObject(value);
    }

    public String getValue() {
        return this.fieldData.getObject();
    }

}
