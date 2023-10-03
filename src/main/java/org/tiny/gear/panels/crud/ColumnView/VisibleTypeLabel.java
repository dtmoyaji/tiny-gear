package org.tiny.gear.panels.crud.ColumnView;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.tiny.datawrapper.Column;

/**
 *
 * @author dtmoyaji
 */
public class VisibleTypeLabel extends AbstractColumnView {

    private Label columnValue;

    public VisibleTypeLabel(String id, IModel<Column> column) {
        super(id, column);

        String colvalue = "";
        if (column.getObject().getValue() != null) {
            colvalue = column.getObject().getValue().toString();
        }
        this.columnValue = new Label("columnValue", Model.of(colvalue));
        this.columnValue.setOutputMarkupId(true);
        this.add(this.columnValue);
    }

    @Override
    public void updateColumnValue() {
        Column col = (Column) this.getDefaultModelObject();
        col.setValue(this.columnValue.getDefaultModelObjectAsString());
    }

}
