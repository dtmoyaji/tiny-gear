package org.tiny.gear.scenes.webdb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.tiny.datawrapper.ConditionForOrder;
import org.tiny.datawrapper.NameDescriptor;
import org.tiny.datawrapper.Table;
import org.tiny.gear.CustomTableBuilder;
import org.tiny.gear.GearApplication;
import org.tiny.gear.RoleController;
import org.tiny.gear.model.MenuItem;
import org.tiny.gear.scenes.AbstractScene;

/**
 * カスタムテーブルのレコード編集用
 *
 * @author dtmoyaji
 */
public class CustomTableRecordScene extends AbstractScene {

    public static final String ARG_TARGET_TABLECLASS = "TargetTableClass";

    private CustomTable customTable;

    public CustomTableRecordScene(Roles allowed, GearApplication app) {
        super(allowed, app);
    }

    @Override
    public void defineMenu() {
        this.customTable = (CustomTable) this.getGearApplication()
                .getCachedTable(CustomTable.class);

        try (ResultSet rs = this.customTable.select(
                new ConditionForOrder(
                        this.customTable.Ordinal,
                         ConditionForOrder.ORDER_ASC
                ))) {
            boolean primary = true;
            while (rs.next()) {
                String menuCaption = this.customTable.LogicalName.of(rs);
                String tableName = this.customTable.TableName.of(rs);
                tableName = NameDescriptor.toJavaName(tableName);
                Table tables = this.getGearApplication().getCachedTable(
                        CustomTableBuilder.CUSTOM_TABLE_PACKAGE
                        + "."
                        + tableName);
                String roleString = this.customTable.AuthLimit.of(rs);
                MenuItem item = this.createMenuItem(menuCaption, GenericTableEditView.class)
                        .setRoles(RoleController.of(roleString))
                        .setPrimary(primary);

                Logger.getLogger(this.getClass().getName())
                        .log(Level.INFO, "RECORD EDIT {0} - {1} - {2}", new Object[]{menuCaption, primary, roleString});
                HashMap<String, String> args = item.getArguments();
                args.put(ARG_TARGET_TABLECLASS, tables.getClass().getName());
                item.setArguments(args);
                primary = false;
            }
            rs.close();

        } catch (SQLException ex) {
            Logger.getLogger(CustomTableRecordScene.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getSceneName() {
        return this.getGearApplication().getSystemVariable("CustomTableRecordScene.Name", "マスターメンテ");
    }

    @Override
    public Class getDefaultViewClass() {
        return GenericTableEditView.class; // 後で直す
    }

}
