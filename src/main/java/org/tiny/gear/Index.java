package org.tiny.gear;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.tiny.datawrapper.IJdbcSupplier;
import org.tiny.datawrapper.Jdbc;
import org.tiny.gear.view.AbstractView;
import org.tiny.gear.panels.NavigationPanel;
import org.tiny.gear.scenes.AbstractScene;
import org.tiny.gear.scenes.PrimaryScene;
import org.tiny.gear.scenes.SettingScene;
import org.tiny.wicket.SamlMainPage;

public class Index extends SamlMainPage implements IJdbcSupplier{

    private static final long serialVersionUID = 1L;

    private final AbstractScene currentScene;
    private final AbstractView currentPanel;

    private final NavigationPanel nav;

    private ArrayList<AbstractScene> scenes;

    public Index(final PageParameters parameters) {
        super(parameters);

        // いずれリゾルバに置換するけど、暫定処理
        AbstractScene currentScene = new PrimaryScene(RoleController.getUserRoles());
        this.scenes = getScenes();

        // 指定された状態に応じてシーンを切り換える処理
        String sceneName = parameters.get("scene").toString();
        if (sceneName != null) {
            for (AbstractScene scene : this.scenes) {
                if (scene.isSceneKeyMatch(sceneName)) {
                    currentScene = scene;
                    break;
                }
            }
        } else {
            for (AbstractScene scene : this.scenes) {
                if (scene.isPrimary()) {
                    currentScene = scene;
                    break;
                }
            }
        }

        HashMap<String, AbstractView> panels = currentScene.getPanels();
        String panelName = parameters.get("view").toString();
        if(panelName!=null){
            this.currentPanel = panels.get(panelName);
        }else{
            this.currentPanel = currentScene.getDefaultPanel();
        }
        this.add(this.currentPanel);

        this.currentScene = currentScene;

        this.nav = new NavigationPanel("menus", this);
        this.add(this.nav);
    }

    public ArrayList<AbstractScene> getScenes() {
        ArrayList<AbstractScene> scenemap = new ArrayList<>();
        scenemap.add(new PrimaryScene(RoleController.getAllRoles()));
        scenemap.add(new SettingScene(RoleController.getUserRoles()));
        return scenemap;
    }

    @Override
    public String getUserAccountKey() {
        return "displayname";
    }

    public AbstractScene getCurrentScene() {
        return this.currentScene;
    }

    public AbstractView getCurrentPanel() {
        return this.currentPanel;
    }

    @Override
    public Jdbc getJdbc() {
        GearApplication app = (GearApplication) this.getApplication();
        return app.getJdbc();
    }

}
