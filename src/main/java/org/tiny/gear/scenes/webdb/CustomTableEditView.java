package org.tiny.gear.scenes.webdb;

import java.util.ArrayList;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.tiny.datawrapper.Column;
import org.tiny.datawrapper.Table;
import org.tiny.gear.CustomTableBuilder;
import org.tiny.gear.GearApplication;
import org.tiny.gear.panels.crud.DataControl;
import org.tiny.gear.panels.crud.DataTableView;
import org.tiny.gear.panels.crud.FilterAndEdit;
import org.tiny.gear.panels.crud.RecordEditor;
import org.tiny.gear.scenes.AbstractView;

/**
 *
 * @author dtmoyaji
 */
public class CustomTableEditView extends AbstractView {

    private CustomTable customTable;

    private FilterAndEdit filterAndEdit;

    private Label groovyMsg;

    public CustomTableEditView(GearApplication app) {
        super(app);
    }

    @Override
    public void redraw() {
        super.redraw();

        this.customTable = (CustomTable) this.getTable(CustomTable.class);
        this.customTable.setAllowDeleteRow(true); // TODO: あとでfalseに変える。

        this.groovyMsg = new Label("groovyMsg", Model.of(""));
        this.groovyMsg.setOutputMarkupId(true);
        this.add(this.groovyMsg);

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
                CustomTableEditView.this.groovyMsg.setDefaultModelObject("");

                String tableName = "";
                String tableDef = "";
                String jdbcStackName = "";
                DataControl defControl = null;
                for (DataControl control : dataControls) {
                    if (control.getColumn().getName().equals(customTable.TableDef.getName())) {
                        tableDef = control.getValue(DataControl.UNESCAPE);
                        defControl = control;
                    }
                    if (control.getColumn().getName().equals(customTable.TableName.getName())) {
                        tableName = control.getValue(DataControl.UNESCAPE);
                    }
                    if (control.getColumn().getName().equals(customTable.JdbcStackName.getName())) {
                        jdbcStackName = control.getValue(DataControl.UNESCAPE);
                    }
                }
                GearApplication ga = getGearApplication();
                ga.removeTableCach(tableName);
                ga.clearViewCach();

                CustomTableBuilder gtb = new CustomTableBuilder(ga);

                Table dummy = null;
                try {
                    if (tableDef.isEmpty()) {
                        tableDef = gtb.getExternalTableGoovySource(tableName, jdbcStackName);
                        System.out.println(tableDef);
                        defControl.setDefaultModel(Model.of(tableDef));
                        defControl.setOutputMarkupId(true);
                        target.add(defControl);
                    } else {
                        dummy = gtb.createTable(tableName, tableDef, jdbcStackName);
                    }
                } catch (Exception ex) {
                    CustomTableEditView.this.groovyMsg.setDefaultModelObject(ex.toString());
                }
                target.add(CustomTableEditView.this.groovyMsg);
                return dummy instanceof Table;
            }
        };
        this.filterAndEdit.setPopupPanel(this.getPopupPanel());
        this.filterAndEdit.getRecordEditor().buildForm(this);
        this.add(this.filterAndEdit);
    }

    @Override
    public String getTitle() {
        return "カスタムテーブル編集";
    }

}
