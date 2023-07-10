package org.tiny.gear.scenes;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.tiny.gear.view.AbstractView;
import org.tiny.gear.view.PrimaryView;

public class PrimaryScene extends AbstractScene {

    public static final long serialVersionUID = -1L;

    public PrimaryScene(Roles allowed) {
        super(allowed);

    }

    @Override
    public String getSceneName() {
        return "はじめに";
    }

    @Override
    public AbstractView getDefaultPanel() {
        return new PrimaryView();
    }

    @Override
    public void defineMenu() {
        this.getPanels().put(AbstractScene.DEFAULT_VIEW,
                new PrimaryView()
        );
    }

}
