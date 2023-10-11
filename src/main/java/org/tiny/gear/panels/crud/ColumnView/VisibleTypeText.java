package org.tiny.gear.panels.crud.ColumnView;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.tiny.datawrapper.Column;

/**
 *
 * @author dtmoyaji
 */
public class VisibleTypeText extends AbstractColumnView {

    private Form controlValueForm;
    private TextField controlValue;

    public VisibleTypeText(String id, IModel<Column> column) {
        super(id, column);
    }

    @Override
    public void onInitialize() {
        super.onInitialize();

        this.controlValueForm = new Form("controlValueForm");
        this.controlValueForm.setOutputMarkupId(true);
        this.add(this.controlValueForm);

        String colvalue = "";
        if (this.getColumn().getValue() != null) {
            colvalue = this.getColumn().getValue().toString();
        }
        this.controlValue = new TextField("controlValue", Model.of(colvalue));
        this.controlValue.setOutputMarkupId(true);
        this.controlValueForm.add(this.controlValue);
    }

    @Override
    public void copyControlValueToColumn() {
        Column col = (Column) this.getDefaultModelObject();
        col.setValue(this.controlValue.getDefaultModelObjectAsString());
    }

    @Override
    public void copyColumnValueToConrtol() {
        this.controlValue.setDefaultModelObject(this.getColumn().getValue());
    }
}
