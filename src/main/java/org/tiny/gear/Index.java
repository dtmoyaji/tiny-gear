package org.tiny.gear;

import java.util.HashMap;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.tiny.gear.scene.UserControlView;
import org.tiny.gear.scene.View;
import org.tiny.wicket.SamlMainPage;

public class Index extends SamlMainPage {

    private static final long serialVersionUID = 1L;

    private final Panel currentPanel;
    
    private HashMap<String, View> scenes;
    
    public Index(final PageParameters parameters) {
        super(parameters);
        
        // いずれリゾルバに置換するけど、暫定処理
        this.scenes = getScenes();
        HashMap<String, Panel> panels = this.scenes.get(UserControlView.class.getName()).getPanels();
        this.currentPanel = (Panel) this.scenes.get(UserControlView.class.getName())
                .getPanels()
                .get(View.DEFAULT_VIEW);
        this.add(this.currentPanel);
        
    }
    
    protected HashMap<String, View> getScenes(){
        HashMap<String, View> scenemap = new HashMap<>();
        scenemap.put(UserControlView.class.getName(), new UserControlView());
        return scenemap;
    }

    @Override
    public String getUserAccountKey() {
        return "displayname";
    }
}
