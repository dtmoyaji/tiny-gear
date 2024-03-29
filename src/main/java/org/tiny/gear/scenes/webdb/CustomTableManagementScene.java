package org.tiny.gear.scenes.webdb;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.tiny.datawrapper.entity.TableInfo;
import org.tiny.gear.GearApplication;
import org.tiny.gear.RoleController;
import org.tiny.gear.model.SystemVariables;
import org.tiny.gear.scenes.AbstractScene;

/**
 *
 * @author dtmoyaji
 */
public class CustomTableManagementScene extends AbstractScene {
    
    public CustomTableManagementScene(Roles allowed, GearApplication application) {
        super(allowed, application);
        
    }
    
    @Override
    public Class getDefaultViewClass() {
        return CustomTableEditView.class;
    }    
    
    @Override
    public String getSceneName(){
        return "WEB DB";
    }

    @Override
    public void defineMenu() {
        
        this.createMenuItem("カスタムテーブル定義", CustomTableEditView.class)
                .setRoles(RoleController.getAllRoles())
                .setPrimary(true);
        
        this.putMenu("汎用テーブル編集", SelectableGenericTableEditView.class,
                new Class[]{SystemVariables.class, TableInfo.class},
                RoleController.getAllRoles(),
                false);

    }

}
