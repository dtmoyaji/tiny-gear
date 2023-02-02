package org.tiny.gear.scenes;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.tiny.gear.RoleController;
import org.tiny.gear.view.PrimaryView;

public class DevelopScene extends AbstractScene {

    public DevelopScene(Roles allowed) {
        super(allowed);

        this.putMenu(
                "Groovy", PrimaryView.class,
                RoleController.getDevelopmentRoles(), true
        );
    }

    @Override
    public String getSceneName() {
        return "開発";
    }

}
