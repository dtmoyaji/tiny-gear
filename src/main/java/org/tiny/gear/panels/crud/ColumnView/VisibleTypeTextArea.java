
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
public class VisibleTypeTextArea extends AbstractColumnView{
    
    private Form columnValueForm;
    private TextArea columnValue;
    
    public VisibleTypeTextArea(String id, IModel<Column> column) {
        super(id, column);

        this.columnValueForm = new Form("columnValueForm");
        this.columnValueForm.setOutputMarkupId(true);
        this.add(this.columnValueForm);

        String colvalue = "";
        if (column.getObject().getValue() != null) {
            colvalue = column.getObject().getValue().toString();
        }
        this.columnValue = new TextArea("columnValue", Model.of(colvalue));
        this.columnValue.setEscapeModelStrings(false);
        this.columnValue.setOutputMarkupId(true);
        this.columnValueForm.add(this.columnValue);
    }
    
    @Override
    public void updateColumnValue() {
        Column col = (Column) this.getDefaultModelObject();
        String colValue = this.columnValue.getDefaultModelObjectAsString();
        col.setValue(colValue);
    }
    
}
