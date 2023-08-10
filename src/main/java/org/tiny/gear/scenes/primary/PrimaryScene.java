package org.tiny.gear.scenes.primary;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.tiny.gear.GearApplication;
import org.tiny.gear.scenes.AbstractScene;

public class PrimaryScene extends AbstractScene {

    public static final long serialVersionUID = -1L;

    public PrimaryScene(Roles allowed, GearApplication supplier) {
        super(allowed, supplier);
    }

    @Override
    public String getSceneName() {
        return "はじめに";
    }

    @Override
    public Class getDefaultViewClass() {
        return PrimaryView.class;
    }

    @Override
    public void defineMenu() {
        this.getPanelNames().put(AbstractScene.DEFAULT_VIEW,
                PrimaryView.class.getCanonicalName()
        );
    }

}
