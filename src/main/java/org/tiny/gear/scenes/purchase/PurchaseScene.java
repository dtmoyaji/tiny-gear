package org.tiny.gear.scenes.purchase;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.tiny.gear.GearApplication;
import org.tiny.gear.RoleController;
import org.tiny.gear.scenes.AbstractScene;

/**
 *
 * @author dtmoyaji
 */
public class PurchaseScene extends AbstractScene {

    public PurchaseScene(Roles allowed, GearApplication application) {
        super(allowed, application);
    }

    @Override
    public void defineMenu() {
        this.createMenuItem("仕入", TraderEditView.class)
                .setRoles(RoleController.getUserRoles())
                .setPrimary(true);
    }

    @Override
    public Class getDefaultViewClass() {
        return TraderEditView.class;
    }

    @Override
    public String getSceneName() {
        return "取引先編集";
    }

}
