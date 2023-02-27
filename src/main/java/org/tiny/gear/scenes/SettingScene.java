package org.tiny.gear.scenes;

import java.util.ArrayList;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.tiny.gear.RoleController;
import org.tiny.gear.model.MenuItem;
import org.tiny.gear.view.AbstractView;
import org.tiny.gear.view.ConnectionView;
import org.tiny.gear.view.UserInfoView;

/**
 *
 * @author bythe
 */
public class SettingScene extends AbstractScene {
    
    public SettingScene(Roles allowed) {
        super(allowed);

        Roles generalRoles = RoleController.getUserRoles();
        Roles adminRoles = RoleController.getAdminRoles();

        this.putMenu("マイアカウント", UserInfoView.class, generalRoles, true);
        this.getMenus().add(new MenuItem("ユーザー同期", "?menu=menu1", adminRoles));
        this.putMenu("接続設定", ConnectionView.class,  adminRoles, false);
    }

    @Override
    public String getSceneName() {
        return "設定";
    }
    
    /**
     * ユーザーが表示権限を持っているかどうかをチェックする。
     * 
     * @param target
     * @param userRole
     * @return 
     */
    public boolean isAuthenticated(AbstractView target, Roles userRole){
        boolean rvalue = false;
        ArrayList<MenuItem> menus = this.getMenus();
        for(MenuItem menu : menus){
            if(menu.isMatchedUrl(this.getClass(), target.getClass())){
                rvalue = menu.isAllowed(userRole);
                break;
            }
        }
        return rvalue;
    }

}
