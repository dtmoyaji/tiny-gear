package org.tiny.gear.panels.crud.ColumnView;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.tiny.datawrapper.Column;

/**
 * 
 * @author dtmoyaji
 */
public abstract class AbstractColumnView extends Panel {

    public AbstractColumnView(String id, IModel<Column> model) {
        super(id, model);
    }

    public void setColumnValue(String value) {
        Column col = (Column) this.getDefaultModelObject();
        col.setValue(value);
    }

    public String getColumnValue() {
        this.updateColumnValue();
        Column col = (Column) this.getDefaultModelObject();
        return String.valueOf(col.getValue());
    }

    /**
     * コントロール上のデータをcolumnValueに格納する。
     */
    public abstract void updateColumnValue();

}
