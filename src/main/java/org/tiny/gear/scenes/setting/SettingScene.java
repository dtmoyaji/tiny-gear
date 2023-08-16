package org.tiny.gear.scenes.setting;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.tiny.gear.GearApplication;
import org.tiny.gear.RoleController;
import org.tiny.gear.scenes.AbstractScene;

/**
 *
 * @author dtmoyaji
 */
public class SettingScene extends AbstractScene {

    public static final long serialVersionUID = -1L;

    public SettingScene(Roles allowed, GearApplication supplier) {
        super(allowed, supplier);
    }

    @Override
    public String getSceneName() {
        return "設定";
    }

    @Override
    public void defineMenu() {

        this.putMenu("マイアカウント", UserInfoView.class, RoleController.getUserRoles(), true);
        this.putMenu("接続設定", ConnectionView.class, RoleController.getAdminRoles(), false);
        this.putMenu("キャッシュ", CacheControlView.class, RoleController.getDevelopmentRoles(), false);
    }

    @Override
    public Class getDefaultViewClass() {
        return UserInfoView.class;
    }

}
