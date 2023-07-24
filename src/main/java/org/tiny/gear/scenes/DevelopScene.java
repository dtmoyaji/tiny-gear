package org.tiny.gear.scenes;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.tiny.datawrapper.IJdbcSupplier;
import org.tiny.gear.RoleController;
import org.tiny.gear.view.SimpleDevelopmentView;

public class DevelopScene extends AbstractScene {

    public static final long serialVersionUID = -1L;

    public DevelopScene(Roles allowed, IJdbcSupplier supplier) {
        super(allowed, supplier);

    }

    @Override
    public String getSceneName() {
        return "開発";
    }

    @Override
    public void defineMenu() {
        this.putMenu(
                "Groovy編集", SimpleDevelopmentView.class,
                RoleController.getDevelopmentRoles(), true
        );
    }

}
