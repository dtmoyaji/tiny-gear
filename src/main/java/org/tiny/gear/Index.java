package org.tiny.gear;

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
    
    private HashMap<String, Scene> scenes;
    
    public Index(final PageParameters parameters) {
        super(parameters);
        
        // いずれリゾルバに置換するけど、暫定処理
        this.scenes = getScenes();
        Scene scene = this.scenes.get(UserControlScene.class.getName());
        HashMap<String, Panel> panels = scene.getPanels();
        this.currentPanel = (Panel) scene
                .getPanels()
                .get(Scene.DEFAULT_VIEW);
        this.add(this.currentPanel);
        
        this.nav = new NavigationPanel("menus",scene);
        this.add(this.nav);
        
    }
    
    protected HashMap<String, Scene> getScenes(){
        HashMap<String, Scene> scenemap = new HashMap<>();
        scenemap.put(UserControlScene.class.getName(), new UserControlScene());
        return scenemap;
    }

    @Override
    public String getUserAccountKey() {
        return "displayname";
    }
}
