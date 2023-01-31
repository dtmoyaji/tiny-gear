package org.tiny.gear;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.tiny.gear.panels.NavigationPanel;
import org.tiny.gear.scene.Scene;
import org.tiny.gear.scene.UserControlScene;
import org.tiny.wicket.SamlMainPage;

public class Index extends SamlMainPage {

    private static final long serialVersionUID = 1L;

    private final Panel currentPanel;

    private final NavigationPanel nav;

    private ArrayList<Scene> scenes;

    public Index(final PageParameters parameters) {
        super(parameters);

        // いずれリゾルバに置換するけど、暫定処理
        this.scenes = getScenes();
        Scene scene = this.scenes.get(0);
        HashMap<String, Panel> panels = scene.getPanels();
        this.currentPanel = (Panel) scene
                .getPanels()
                .get(Scene.DEFAULT_VIEW);
        this.add(this.currentPanel);

        this.nav = new NavigationPanel("menus", this);
        this.add(this.nav);
    }

    public ArrayList<Scene> getScenes() {
        ArrayList<Scene> scenemap = new ArrayList<>();
        scenemap.add(new UserControlScene(RoleController.getUserRoles()));
        return scenemap;
    }

    @Override
    public String getUserAccountKey() {
        return "displayname";
    }
}
