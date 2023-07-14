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
        this.fieldData = new Model(this.targetColumn.getValue().toString());
        this.columnValueLabel = new Label("columnValueLabel", fieldData);
        this.add(this.columnValueLabel);
        this.columnValueTextField = new TextField("columnValueTextField", fieldData);
        this.add(this.columnValueTextField);
        this.columnValueTextArea = new TextArea("columnValueTextArea", fieldData);
        this.add(this.columnValueTextArea);

        // テーブルの設定で使用しないコントロールを不可視に設定する。
        this.visibleComponent = this.columnValueTextField;
        this.columnValueLabel.setVisible(false);
        this.columnValueTextField.setVisible(true);
        this.columnValueTextArea.setVisible(false);

        // データの入出力に対応するコントロールを抽象型のvisibleComponentでポイントする。データIOはこれを使って行う。
        if (this.targetColumn.getVisibleType() == Column.VISIBLE_TYPE_LABEL) {
            this.visibleComponent = this.columnValueLabel;
            this.columnValueLabel.setVisible(true);
            this.columnValueTextField.setVisible(false);
            this.columnValueTextArea.setVisible(false);
        } else if (this.targetColumn.getVisibleType() == Column.VISIBLE_TYPE_TEXTAREA) {
            this.visibleComponent = this.columnValueTextArea;
            this.columnValueLabel.setVisible(false);
            this.columnValueTextField.setVisible(false);
            this.columnValueTextArea.setVisible(true);
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
