package org.tiny.gear.scenes.develop;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.tiny.gear.GearApplication;
import org.tiny.gear.RoleController;
import org.tiny.gear.scenes.AbstractScene;

public class DevelopScene extends AbstractScene {

    public static final long serialVersionUID = -1L;

    public DevelopScene(Roles allowed, GearApplication supplier) {
        super(allowed, supplier);

    }

    @Override
    public String getSceneName() {
        return "開発";
    }

    @Override
    public void defineMenu() {
        this.createMenuItem("Groovy編集", SimpleDevelopmentView.class)
                .setRoles(RoleController.getDevelopmentRoles())
                .setPrimary(true);
    }

    @Override
    public Class getDefaultViewClass() {
        return SimpleDevelopmentView.class;
    }

}
