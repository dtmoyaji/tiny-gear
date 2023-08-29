
package org.tiny.gear.panels.crud;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.tiny.datawrapper.Table;
import org.tiny.datawrapper.entity.TableInfo;
import org.tiny.gear.GearApplication;

/**
 *
 * @author dtmoyaji
 */
public class TableImportController extends Panel{
    
    private Form form;
    
    private DataTableView dataTableView;
    
    private Table targetTable;
    
    private Label targetTableName;
    
    private TextField matchedTable;
    
    private FileUploadField fileUploadField;
    
    private TableInfo tableInfo;

    public TableImportController(String id, DataTableView dataTableView) {
        super(id);
        
        this.dataTableView = dataTableView;
        this.targetTable = this.dataTableView.getTable();
        
        this.form = new Form("importForm");
        this.add(this.form);
        
        this.targetTableName = new Label("targetTableName", Model.of(this.targetTable.getName()));
        this.form.add(this.targetTableName);
        
        this.fileUploadField = new FileUploadField("fileUploadField");
        this.form.add(this.fileUploadField);
    }
    
    public GearApplication getGearApplication(){
        return (GearApplication) this.getApplication();
    }

}
