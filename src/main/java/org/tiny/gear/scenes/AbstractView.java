package org.tiny.gear.scenes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.tiny.datawrapper.Table;
import org.tiny.gear.GearApplication;
import org.tiny.gear.panels.AbstractPanel;
import org.tiny.gear.panels.BlankPanel;
import org.tiny.gear.panels.IPanelPopupper;
import org.tiny.gear.panels.PopupPanel;

/**
 * シーンに含まれるビューの抽象クラス
 *
 * @author dtmoyaji
 */
public abstract class AbstractView extends AbstractPanel implements IPanelPopupper {

    public static final long serialVersionUID = -1L;

    private Label title;

    protected GearApplication app;

    private HashMap<String, Class<? extends Table>> tableClasses = null;
    private Class<? extends Table> prmaryTableClass = null;

    private HashMap<String, String> arguments = null;

    private PopupPanel popupPanel;

    public AbstractView(GearApplication app) {
        super("scenePanel");
        this.tableClasses = new HashMap<>();
        this.arguments = new HashMap<>();
        this.app = app;

        TablesForAbstractView tfav = (TablesForAbstractView) this.getTable(TablesForAbstractView.class);

        try (ResultSet tableref = tfav.select(tfav.ViewClassName.sameValueOf(this.getClass().getName()))) {
            if (tableref != null) {
                this.prmaryTableClass = null;
                while (tableref.next()) {
                    String tableName = tfav.TableClassName.of(tableref);
                    Class cls = this.app.getCachedClass(tableName);
                    this.getTable(cls);
                    if (this.prmaryTableClass == null) {
                        this.prmaryTableClass = cls;
                    }
                }
            }
        } catch (SQLException
                | SecurityException
                | IllegalArgumentException ex) {
            Logger.getLogger(AbstractView.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public Table getPrimaryTargetTable() {
        return this.getTable(this.prmaryTableClass);
    }

    public Table getTable(String tableClassName) {
        if(this.tableClasses==null){
            this.tableClasses = new HashMap();
        }
        Table rvalue = this.getGearApplication().getCachedTable(tableClassName);
        this.tableClasses.put(rvalue.getClass().getCanonicalName(), rvalue.getClass());
        return rvalue;
    }

    public Table getTable(Class<? extends Table> tableClass) {
        if (this.tableClasses == null) {
            this.tableClasses = new HashMap<>();
        }
        Table rvalue = this.getGearApplication().getCachedTable(tableClass);
        this.tableClasses.put(tableClass.getCanonicalName(), tableClass);
        return rvalue;
    }

    public HashMap<String, Class<? extends Table>> getTables() {
        return this.tableClasses;
    }

    public abstract String getTitle();

    public void redraw() {
        this.removeAll();

        this.app = app;
        this.title = new Label("panelTitle", Model.of(this.getTitle()));
        this.add(this.title);

        this.popupPanel = new PopupPanel("popupPanel", new BlankPanel("childPanel"));
        this.popupPanel.setOutputMarkupId(true);
        this.add(this.popupPanel);

    }

    public GearApplication getGearApplication() {
        return (GearApplication) this.getApplication();
    }

    @Override
    public void setPopupPanel(PopupPanel panel) {
        this.popupPanel = panel;
    }

    @Override
    public PopupPanel getPopupPanel() {
        return this.popupPanel;
    }

    public void setArguments(HashMap<String, String> hashMap) {
        this.arguments = hashMap;
    }

    public HashMap<String, String> getArguments() {
        return this.arguments;
    }

}
