package org.tiny.gear.scenes;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.tiny.gear.RoleController;
import org.tiny.gear.view.SimpleDevelopmentView;

public class DevelopScene extends AbstractScene {

    public DevelopScene(Roles allowed) {
        super(allowed);

    }

    @Override
    public String getSceneName() {
        return "開発";
    }

    @Override
    public void defineMenu() {
        this.putMenu(
                "Groovy", SimpleDevelopmentView.class,
                RoleController.getDevelopmentRoles(), true
        );
    }

}
