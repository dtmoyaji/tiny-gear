package org.tiny.gear.panels;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.tiny.datawrapper.Table;
import org.tiny.gear.GearApplication;
import org.tiny.gear.GroovyExecuteButton;
import org.tiny.gear.panels.crud.DataTableView;

/**
 *
 * @author dtmoyaji
 */
public class TableExportController extends Panel {

    private Form controllerForm;

    private GroovyExecuteButton exportAll;
    
    private DataTableView dataTableView;

    public TableExportController(String id, DataTableView dataTableView) {
        super(id);
        
        this.dataTableView = dataTableView;

        this.controllerForm = new Form("controllerForm");
        this.add(this.controllerForm);

        this.exportAll = new GroovyExecuteButton("exportAll",
                Model.of("全データ"),
                this.getGearApplication(),
                this.dataTableView,
                "_DataTableView"
        );
        this.controllerForm.add(this.exportAll);

    }
    
    public Table getTable(){
        return this.dataTableView.getTable();
    }

    public GearApplication getGearApplication() {
        return (GearApplication) this.getApplication();
    }

}
