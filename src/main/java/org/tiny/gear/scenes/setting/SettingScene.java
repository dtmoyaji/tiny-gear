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
        
        this.createMenuItem("マイアカウント", UserInfoView.class)
                .setRoles(RoleController.getUserRoles())
                .setPrimary(true);
        
        this.createMenuItem("接続設定", ConnectionView.class)
                .setRoles(RoleController.getAdminRoles())
                .setPrimary(false);
        
        this.createMenuItem("キャッシュ", CacheControlView.class)
                .setRoles(RoleController.getDevelopmentRoles())
                .setPrimary(false);
    }

    @Override
    public Class getDefaultViewClass() {
        return UserInfoView.class;
    }

}
