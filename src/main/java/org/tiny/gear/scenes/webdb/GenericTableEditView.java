package org.tiny.gear.scenes.webdb;

import java.util.ArrayList;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.tiny.datawrapper.Table;
import org.tiny.gear.GearApplication;
import org.tiny.gear.model.MenuItem;
import org.tiny.gear.panels.crud.DataControl;
import org.tiny.gear.panels.crud.DataTableView;
import org.tiny.gear.panels.crud.FilterAndEdit;
import org.tiny.gear.panels.crud.RecordEditor;
import org.tiny.gear.scenes.AbstractView;

/**
 *
 * @author dtmoyaji
 */
public class GenericTableEditView extends AbstractView {

    public static String KEY_DEFAULT_ROWS_PER_PAGE = "CustomTableEditView.DefaultRowsPerPage";

    private Table targetTable;
    
    private FilterAndEdit filterAndEdit;

    private Label groovyMsg;
    
    private String generatedTableClassName;

    public GenericTableEditView(GearApplication app) {
        super(app);
    }
    
    @Override
    public void redraw() {
        super.redraw();

        MenuItem menuItem = (MenuItem) this.getSession().getAttribute("menuItem");
        String tblClass = menuItem.getArguments().get(CustomTableRecordScene.ARG_TARGET_TABLECLASS);
        this.targetTable = this.getTable(tblClass);
        this.targetTable.setAllowDeleteRow(true); // TODO: あとでfalseに変える。

        this.groovyMsg = new Label("groovyMsg", Model.of(""));
        this.groovyMsg.setOutputMarkupId(true);
        this.add(this.groovyMsg);

        this.filterAndEdit = new FilterAndEdit("customTableEditor", this.targetTable) {

            public static final long serialVersionUID = -1L;

            @Override
            public void beforeConstructDataTableView(Table myTable, DataTableView dataTableView) {
            }

            @Override
            public void afterConstructDataTableView(Table myTable, DataTableView dataTableView) {
            }

            @Override
            public void beforeConstructRecordEditor(Table myTable, RecordEditor recordEditor) {
            }

            @Override
            public void afterConstructRecordEditor(Table myTable, RecordEditor recordEditor) {
            }

            @Override
            public boolean beforeSubmit(AjaxRequestTarget target, Table targetTable, ArrayList<DataControl> dataControls) {
                return true;
            }
            
            @Override
            public void afterSubmit(AjaxRequestTarget target, Table targetTable, ArrayList<DataControl> dataControls){
            }
        };
        this.filterAndEdit.setPopupPanel(this.getPopupPanel());
        this.filterAndEdit.getRecordEditor().buildForm(this);
        this.add(this.filterAndEdit);

        int defaultRowsPerPage = Integer.parseInt(
                GenericTableEditView.this.getGearApplication().getSystemVariable(
                        GenericTableEditView.KEY_DEFAULT_ROWS_PER_PAGE,
                        "2"
                )
        );
        this.filterAndEdit.getDataTableView().setRowsPerPage(defaultRowsPerPage);
        this.filterAndEdit.getDataTableView().redraw();

    }

    @Override
    public String getTitle() {
        return "データ編集";
    }

}
