package org.tiny.gear.panels.crud;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.tiny.datawrapper.Table;
import org.tiny.datawrapper.entity.TableInfo;
import org.tiny.gear.ExcelFileImporter;
import org.tiny.gear.GearApplication;

/**
 *
 * @author dtmoyaji
 */
public class TableImportController extends Panel {

    private Form form;

    private DataTableView dataTableView;

    private Table targetTable;

    private Label targetTableName;

    private TextField matchedTable;

    private FileUploadField fileUploadField;

    private AjaxButton importButton;
    
    private Label messageArea;
    
    private TableInfo tableInfo;

    public TableImportController(String id, DataTableView dataTableView) {
        super(id);

        this.dataTableView = dataTableView;
        this.targetTable = this.dataTableView.getTable();

        this.form = new Form("importForm");
        this.form.setMultiPart(true);
        this.add(this.form);

        this.targetTableName = new Label("targetTableName", Model.of(this.targetTable.getName()));
        this.form.add(this.targetTableName);

        this.fileUploadField = new FileUploadField("fileUploadField");
        this.form.add(this.fileUploadField);

        this.importButton = new AjaxButton("startImport", Model.of("開始")) {
            @Override
            public void onSubmit(AjaxRequestTarget target) {
                TableImportController.this.startImport();
                target.add(TableImportController.this.messageArea);
            }
        };
        this.form.add(this.importButton);
        
        this.messageArea = new Label("massageArea", Model.of(""));
        this.messageArea.setOutputMarkupId(true);
        this.add(this.messageArea);
    }

    public GearApplication getGearApplication() {
        return (GearApplication) this.getApplication();
    }

    // インポートの開始
    public void startImport() {
        File file = this.uploadFile();
        ExcelFileImporter efi = new ExcelFileImporter();
        if(efi.importTableData(this.targetTable, file)){
            this.messageArea.setDefaultModelObject("インポートが終了しました。");
        }
    }

    /**
     * ファイルをサーバーにアップロードする
     * @return 
     */
    public File uploadFile() {
        File rvalue = null;
        FileUpload fileUpload = this.fileUploadField.getFileUpload();
        if (fileUpload != null) {
            try {
                String fileName = fileUpload.getClientFileName();
                rvalue = new File(this.getGearApplication().getRealPath("files"), fileName);
                fileUpload.writeTo(rvalue);
            } catch (Exception ex) {
                Logger.getLogger(TableImportController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return rvalue;
    }

}
