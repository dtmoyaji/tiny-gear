package org.tiny.gear.scene;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.tiny.gear.RoleController;
import org.tiny.gear.model.MenuItem;
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

        this.getPanels().put(AbstractScene.DEFAULT_VIEW, new UserInfoPanel("scenePanel"));
        this.getMenus().add(new MenuItem("マイアカウント", SettingScene.class, UserInfoPanel.class, generalRoles));
        this.getMenus().add(new MenuItem("ユーザー同期", "?menu=menu1", adminRoles));
        this.getMenus().add(new MenuItem("接続", "?menu=menu1", adminRoles));
    }

    @Override
    public String getSceneName() {
        return "設定";
    }

}
