package org.tiny.gear.scenes;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.tiny.gear.RoleController;
import org.tiny.gear.model.MenuItem;
import org.tiny.gear.panels.ConnectionView;
import org.tiny.gear.panels.UserInfoPanel;

/**
 *
 * @author bythe
 */
public class SettingScene extends AbstractScene {
    
    public SettingScene(Roles allowed) {
        super(allowed);

        Roles generalRoles = RoleController.getUserRoles();
        Roles adminRoles = RoleController.getAdminRoles();

        this.putMenu("マイアカウント", UserInfoPanel.class, generalRoles, true);
        this.putMenu("接続設定", ConnectionView.class,  adminRoles, false);
        this.getMenus().add(new MenuItem("ユーザー同期", "?menu=menu1", adminRoles));
    }

    @Override
    public String getSceneName() {
        return "設定";
    }

}
