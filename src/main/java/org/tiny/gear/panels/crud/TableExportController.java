package org.tiny.gear.panels.crud;

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
public final class TableExportController extends Panel {

    private Form controllerForm;

    private GroovyExecuteButton exportAll;

    private GroovyExecuteButton exportFilterd;

    private GroovyExecuteButton exportCurrentRows;

    private DataTableView dataTableView;

    public TableExportController(String id, DataTableView dataTableView) {
        super(id);

        this.dataTableView = dataTableView;

        this.controllerForm = new Form("controllerForm");
        this.add(this.controllerForm);

        this.exportAll = new GroovyExecuteButton("exportAll", Model.of("全データ"),
                this.getGearApplication(), dataTableView, "_DataTableView");
        this.controllerForm.add(this.exportAll);

        this.exportFilterd = new GroovyExecuteButton("exportFilterd", Model.of("絞込"),
                this.getGearApplication(), dataTableView, "_DataTableView");
        this.controllerForm.add(this.exportFilterd);

        this.exportCurrentRows = new GroovyExecuteButton("exportCurrentRows", Model.of("現在の行"),
                this.getGearApplication(), dataTableView, "_DataTableView");
        this.controllerForm.add(this.exportCurrentRows);
    }

    public Table getTable() {
        return this.dataTableView.getTable();
    }

    public GearApplication getGearApplication() {
        return (GearApplication) this.getApplication();
    }

}
