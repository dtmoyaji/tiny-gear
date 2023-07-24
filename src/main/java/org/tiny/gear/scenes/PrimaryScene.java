package org.tiny.gear.scenes;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.tiny.datawrapper.IJdbcSupplier;
import org.tiny.gear.view.AbstractView;
import org.tiny.gear.view.PrimaryView;

public class PrimaryScene extends AbstractScene {

    public static final long serialVersionUID = -1L;

    public PrimaryScene(Roles allowed, IJdbcSupplier supplier) {
        super(allowed, supplier);
    }

    @Override
    public String getSceneName() {
        return "はじめに";
    }

    @Override
    public AbstractView getDefaultPanel() {
        return new PrimaryView(this.supplier);
    }

    @Override
    public void defineMenu() {
        this.getPanels().put(AbstractScene.DEFAULT_VIEW,
                new PrimaryView(this.supplier)
        );
    }

}
