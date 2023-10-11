package org.tiny.gear.panels.crud.ColumnView;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.tiny.datawrapper.Column;

/**
 *
 * @author dtmoyaji
 */
public class VisibleTypeTextArea extends AbstractColumnView {

    private Form controlValueForm;
    private TextArea controlValue;

    public VisibleTypeTextArea(String id, IModel<Column> column) {
        super(id, column);

        this.controlValueForm = new Form("controlValueForm");
        this.controlValueForm.setOutputMarkupId(true);
        this.add(this.controlValueForm);

        String colvalue = "";
        if (column.getObject().getValue() != null) {
            colvalue = column.getObject().getValue().toString();
        }
        this.controlValue = new TextArea("controlValue", Model.of(colvalue));
        this.controlValue.setEscapeModelStrings(false);
        this.controlValue.setOutputMarkupId(true);
        this.controlValueForm.add(this.controlValue);
    }

    @Override
    public void copyControlValueToColumn() {
        Column col = (Column) this.getDefaultModelObject();
        String colValue = this.controlValue.getDefaultModelObjectAsString();
        col.setValue(colValue);
    }

    @Override
    public void copyColumnValueToConrtol() {
        this.controlValue.setDefaultModelObject(this.getColumn().getValue());
    }
}
