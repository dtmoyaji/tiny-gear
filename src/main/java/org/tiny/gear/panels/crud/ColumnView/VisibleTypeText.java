
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
public class VisibleTypeText extends AbstractColumnView{
    
    private Form columnValueForm;
    private TextField columnValue;
    
    public VisibleTypeText(String id, IModel<Column> column) {
        super(id, column);

        this.columnValueForm = new Form("columnValueForm");
        this.columnValueForm.setOutputMarkupId(true);
        this.add(this.columnValueForm);

        String colvalue = "";
        if (column.getObject().getValue() != null) {
            colvalue = column.getObject().getValue().toString();
        }
        this.columnValue = new TextField("columnValue", Model.of(colvalue));
        this.columnValue.setOutputMarkupId(true);
        this.columnValueForm.add(this.columnValue);
    }
    
    @Override
    public void updateColumnValue() {
        Column col = (Column) this.getDefaultModelObject();
        col.setValue(this.columnValue.getDefaultModelObjectAsString());
    }
    
}
