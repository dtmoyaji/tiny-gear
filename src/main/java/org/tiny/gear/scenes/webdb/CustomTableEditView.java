package org.tiny.gear.scenes.webdb;

import groovy.lang.GroovyShell;
import java.util.ArrayList;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.Model;
import org.tiny.datawrapper.Column;
import org.tiny.datawrapper.Table;
import org.tiny.gear.GearApplication;
import org.tiny.gear.panels.crud.DataControl;
import org.tiny.gear.panels.crud.DataTableView;
import org.tiny.gear.panels.crud.FilterAndEdit;
import org.tiny.gear.panels.crud.RecordEditor;
import org.tiny.gear.scenes.AbstractView;

/**
 *
 * @author bythe
 */
public class CustomTableEditView extends AbstractView {

    private CustomTable customTable;

    private FilterAndEdit filterAndEdit;

    public CustomTableEditView(GearApplication app) {
        super(app);
    }
    
    @Override
    public void redraw(){
        this.removeAll();
        
        this.customTable = (CustomTable) this.getTable(CustomTable.class);
        this.customTable.setAllowDeleteRow(true); // TODO: あとでfalseに変える。

        this.filterAndEdit = new FilterAndEdit("customTableEditor", this.customTable) {
            
            public static final long serialVersionUID = -1L;
            
            @Override
            public void beforeConstructDataTableView(Table myTable, DataTableView dataTableView) {
                myTable.get(customTable.TableDef.getName()).setVisibleType(Column.VISIBLE_TYPE_HIDDEN);
            }

            @Override
            public void afterConstructDataTableView(Table myTable, DataTableView dataTableView) {
            }

            @Override
            public void beforeConstructRecordEditor(Table myTable, RecordEditor recordEditor) {
                myTable.get(customTable.LastAccess.getName()).setVisibleType(Column.VISIBLE_TYPE_LABEL);
            }

            @Override
            public void afterConstructRecordEditor(Table myTable, RecordEditor recordEditor) {
                DataControl control = recordEditor.getDataControl(customTable.TableDef.getSplitedName());
                if (control != null) {
                    control.getVisibleComponent().add(new AttributeModifier("style", Model.of("height: 12em;")));
                }
            }

            @Override
            public boolean beforeSubmit(AjaxRequestTarget target, Table targetTable, ArrayList<DataControl> dataControls) {
                boolean rvalue = false;
                for(DataControl control: dataControls){
                    if(control.getColumn().getName().equals(customTable.TableDef.getName())){
                        String def = control.getValue(DataControl.UNESCAPE);
                        GroovyShell shell = new GroovyShell();
                        Object obj = shell.evaluate(def);
                        if(obj instanceof Table){
                            ((Table)obj).alterOrCreateTable(CustomTableEditView.this.app.getJdbc());
                        }
                        rvalue = true;
                    }
                }
                return rvalue;
            }
        };
        this.add(this.filterAndEdit);
    }

    @Override
    public String getTitle() {
        return "カスタムテーブル編集";
    }

}
