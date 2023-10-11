package org.tiny.gear.panels.crud.ColumnView;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.tiny.datawrapper.Column;

/**
 *
 * @author dtmoyaji
 */
public class VisibleTypeLabel extends AbstractColumnView {

    private Form controlValueForm;
    private Label controlValue;

    public VisibleTypeLabel(String id, IModel<Column> column) {
        super(id, column);

        this.controlValueForm = new Form("controlValueForm");
        this.add(this.controlValueForm);

        String colvalue = "";
        if (column.getObject().getValue() != null) {
            colvalue = column.getObject().getValue().toString();
        }
        this.controlValue = new Label("controlValue", Model.of(colvalue));
        this.controlValue.setOutputMarkupId(true);
        this.controlValueForm.add(this.controlValue);
    }

    @Override
    public void copyControlValueToColumn() {
        this.getColumn().setValue(this.controlValue.getDefaultModelObjectAsString());
    }

    @Override
    public void copyColumnValueToConrtol() {
        this.controlValue.setDefaultModelObject(
                this.getColumn().getValue()
        );
    }

}
