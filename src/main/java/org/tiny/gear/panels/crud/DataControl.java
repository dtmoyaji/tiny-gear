package org.tiny.gear.panels.crud;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.tiny.datawrapper.Column;

/**
 *
 * @author bythe
 */
public class DataControl extends Panel {

    private Label columnCaption;

    private TextField columnValueTextField;

    private Label columnValueLabel;
    
    private Component visibleComponent;

    private Column targetColumn;

    public DataControl(String id, Column column) {
        super(id);

        this.targetColumn = column;

        String caption = this.targetColumn.getTable().getColumnLogicalName(this.targetColumn);
        this.columnCaption = new Label("columnCaption", Model.of(caption));
        this.add(this.columnCaption);

        // 対象のコントロールを全て格納する。
        this.columnValueLabel = new Label("columnValueLabel", Model.of(this.targetColumn.getValue().toString()));
        this.add(this.columnValueLabel);
        this.columnValueTextField = new TextField("columnValueTextField", Model.of(this.targetColumn.getValue().toString()));
        this.add(this.columnValueTextField);

        // 表示対象でないコントロールを不可視に設定する。
        this.visibleComponent = this.columnValueTextField;
        this.columnValueLabel.setVisible(false);

        if (this.targetColumn.getVisibleType() == Column.VISIBLE_TYPE_LABEL) {
            this.columnValueLabel.setVisible(true);
            this.visibleComponent = this.columnValueLabel;
            this.columnValueTextField.setVisible(false);
        }
    }

    public Column getColumn() {
        return this.targetColumn;
    }

    public void setValue(String value) {
        this.visibleComponent.setDefaultModelObject(value);
    }

    public String getValue() {
        return this.visibleComponent.getDefaultModelObjectAsString();
    }

}
