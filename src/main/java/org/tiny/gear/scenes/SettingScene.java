package org.tiny.gear.scenes;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.tiny.gear.RoleController;
import org.tiny.gear.model.MenuItem;
import org.tiny.gear.view.ConnectionView;
import org.tiny.gear.view.UserInfoView;

/**
 *
 * @author bythe
 */
public class SettingScene extends AbstractScene {

    public static final long serialVersionUID = -1L;

    public SettingScene(Roles allowed) {
        super(allowed);

    }

    @Override
    public String getSceneName() {
        return "設定";
    }

    @Override
    public void defineMenu() {
        Roles generalRoles = RoleController.getUserRoles();
        Roles adminRoles = RoleController.getAdminRoles();

        this.putMenu("マイアカウント", UserInfoView.class, generalRoles, true);
        this.getMenus().add(new MenuItem("ユーザー同期", "?menu=menu1", adminRoles));
        this.putMenu("接続設定", ConnectionView.class, adminRoles, false);
    }

}
