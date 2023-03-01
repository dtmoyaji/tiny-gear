package org.tiny.gear;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.tiny.datawrapper.IJdbcSupplier;
import org.tiny.datawrapper.Jdbc;
import org.tiny.gear.panels.HumbergerIcon;
import org.tiny.gear.panels.NavigationPanel;
import org.tiny.gear.scenes.AbstractScene;
import org.tiny.gear.scenes.DevelopScene;
import org.tiny.gear.scenes.PrimaryScene;
import org.tiny.gear.scenes.SettingScene;
import org.tiny.gear.view.AbstractView;
import org.tiny.wicket.SamlMainPage;
import org.tiny.wicket.onelogin.SamlSession;

public class Index extends SamlMainPage implements IJdbcSupplier {

    private static final long serialVersionUID = 1L;

    private final Label serviceTitle;

    private AbstractScene currentScene;
    private AbstractView currentPanel;

    private final NavigationPanel nav;
    private final HumbergerIcon humbergerIcon;

    private ArrayList<AbstractScene> scenes;

    public Index(final PageParameters parameters) {
        super(parameters);

        GearApplication app = (GearApplication) this.getApplication();
        String svtitle = (String) app.getProperties("tiny.gear").get("tiny.gear.service.title");

        this.serviceTitle = new Label("serviceTitle", Model.of(svtitle));
        this.add(this.serviceTitle);

        // いずれリゾルバに置換するけど、暫定処理
        this.currentScene = new PrimaryScene(RoleController.getUserRoles());
        this.scenes = this.getScenes();

        // 指定された状態に応じてシーンを切り換える処理
        String sceneName = parameters.get("scene").toString();
        if (sceneName != null) {
            for (AbstractScene scene : this.scenes) {
                if (scene.isSceneKeyMatch(sceneName)) {
                    this.currentScene = scene;
                    break;
                }
            }
        } else {
            for (AbstractScene scene : this.scenes) {
                if (scene.isPrimary()) {
                    this.currentScene = scene;
                    break;
                }
            }
        }

        HashMap<String, AbstractView> panels = currentScene.getPanels();
        String panelName = parameters.get("view").toString();
        if (panelName != null) {
            this.currentPanel = panels.get(panelName);
        } else {
            this.currentPanel = currentScene.getDefaultPanel();
        }
        // ロールをチェックし、権限が無い場合は初期ページに強制遷移する
        Roles role = ((SamlSession) this.getSession()).getRoles();
        if (!this.currentScene.isAuthenticated(this.currentPanel, role)) {
            this.currentScene = new PrimaryScene(RoleController.getUserRoles());
            this.currentPanel = this.currentScene.getDefaultPanel();
        }

        this.add(this.currentPanel);

        this.nav = new NavigationPanel("menus", this);
        this.nav.setOutputMarkupId(true);
        this.add(this.nav);

        this.humbergerIcon = new HumbergerIcon("humbergerIcon", "humbergerTarget");
        this.add(this.humbergerIcon);
    }

    public ArrayList<AbstractScene> getScenes() {
        ArrayList<AbstractScene> scenemap = new ArrayList<>();
        scenemap.add(new PrimaryScene(RoleController.getAllRoles()));
        scenemap.add(new SettingScene(RoleController.getUserRoles()));
        scenemap.add(new DevelopScene(RoleController.getDevelopmentRoles()));
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
