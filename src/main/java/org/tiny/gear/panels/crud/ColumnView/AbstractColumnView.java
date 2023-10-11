package org.tiny.gear.panels.crud.ColumnView;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.tiny.datawrapper.Column;
import org.tiny.gear.GearApplication;

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
        this.copyColumnValueToConrtol();
    }

    public String getColumnValue() {
        this.copyControlValueToColumn();
        Column col = (Column) this.getDefaultModelObject();
        return String.valueOf(col.getValue());
    }

    public Column getColumn() {
        return (Column) this.getDefaultModelObject();
    }
    
    /**
     * カラムのデータをコントロールにコピーする。
     */
    public abstract void copyColumnValueToConrtol();

    /**
     * コントロール上のデータをcolumnに格納する。
     */
    public abstract void copyControlValueToColumn();

    public GearApplication getGearApplication() {
        return (GearApplication) this.getApplication();
    }

}
