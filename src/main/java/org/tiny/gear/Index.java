package org.tiny.gear;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.tiny.datawrapper.IJdbcSupplier;
import org.tiny.datawrapper.Jdbc;
import org.tiny.gear.model.UserInfo;
import org.tiny.gear.panels.HumbergerIcon;
import org.tiny.gear.panels.NavigationPanel;
import org.tiny.gear.scenes.AbstractScene;
import org.tiny.gear.scenes.AbstractView;
import org.tiny.gear.scenes.primary.PrimaryScene;
import org.tiny.wicket.SamlMainPage;
import org.tiny.wicket.onelogin.SamlAuthInfo;
import org.tiny.wicket.onelogin.SamlSession;

/**
 * メインページ。 ここで、画面表示を全部制御する。
 *
 * @author bythe
 */
public class Index extends SamlMainPage implements IJdbcSupplier {

    private static final long serialVersionUID = 1L;

    private final Label serviceTitle;

    private AbstractScene currentScene;
    private AbstractView currentView;

    private final NavigationPanel nav;
    private final HumbergerIcon humbergerIcon;

    private ArrayList<Class> scenes;
    //private SceneTable sceneTable;

    public Index(final PageParameters parameters) {
        super(parameters);

        this.getGearApplication().buildCache();
        this.getGearApplication().initScenes();

        String svtitle = (String) this.getGearApplication()
                .getProperties("tiny.gear")
                .get("tiny.gear.service.title");

        this.serviceTitle = new Label("serviceTitle", Model.of(svtitle));
        this.add(this.serviceTitle);

        // 初期ページの取得
        this.currentScene = this.getGearApplication().getCachedScene(PrimaryScene.class.getName());
        this.currentView = this.currentScene.createDefaultView();

        String sceneName = parameters.get("scene").toString();
        String panelName = parameters.get("view").toString();
        this.resolvePage(sceneName, panelName);

        this.nav = new NavigationPanel("menus", this) {
            @Override
            public void onMenuItemClick(AjaxRequestTarget target, String sceneName, String panelName) {
                Index.this.resolvePage(sceneName, panelName);
                target.add(Index.this.currentView);
                target.add(Index.this.nav);
            }
        };
        this.nav.setOutputMarkupId(true);
        this.add(this.nav);

        this.humbergerIcon = new HumbergerIcon("humbergerIcon", "humbergerTarget");
        this.add(this.humbergerIcon);

        this.currentView.setOutputMarkupId(true);
        this.add(this.currentView);

    }

    /**
     * ページリゾルバ
     *
     * @param parameters
     */
    private void resolvePage(String sceneName, String viewName) {

        // 指定された状態に応じたシーンを表示する処理
        if (sceneName == null) {
            sceneName = PrimaryScene.class.getCanonicalName();
        }
        this.currentScene = this.getGearApplication().getCachedScene(sceneName);

        if (viewName == null) {
            this.currentView = this.currentScene.createDefaultView();
        } else {
            HashMap<String, String> panels = this.currentScene.getPanelNames();
            if (panels.containsValue(viewName)) {
                try {
                    this.currentView = (AbstractView) this.getGearApplication().getCachedView(viewName);
                } catch (SecurityException | IllegalArgumentException ex) {
                    Logger.getLogger(Index.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        // ロールをチェックし、権限が無い場合は初期ページに強制遷移する
        Roles role = ((SamlSession) this.getSession()).getRoles();

        if (role.size() < 1) {
            role.add("guest");
        }

        if (!this.currentScene.isAuthenticated(this.currentView, role)) {
            this.currentScene = new PrimaryScene(RoleController.getGuestRoles(), this.getGearApplication());
            this.currentView = this.currentScene.createDefaultView();
        } else { //表示に問題がないときは、ユーザー情報を上書きに行く
            SamlAuthInfo ainfo = ((SamlSession) this.getSession()).getSamlAuthInfo();
            if (ainfo != null) {
                UserInfo uinfo = (UserInfo) this.getGearApplication().getCachedTable(UserInfo.class);
                uinfo.UserId.setValue(ainfo.getAttributeString("externalKey"));
                uinfo.UserName.setValue(ainfo.getAttributeString("username"));
                uinfo.LastAccess.setValue(new Timestamp(System.currentTimeMillis()));
                uinfo.AttributeJson.setValue(ainfo.toJson()); // TODO: JSONの生成ロジックを作ること。
                uinfo.merge();
            }
        }
        
        this.currentView.redraw();
        this.currentView.setOutputMarkupId(true);

        this.addOrReplace(this.currentView);

        if (this.nav != null) {
            this.nav.resolve(this);
        }
    }

    public final GearApplication getGearApplication() {
        return (GearApplication) this.getApplication();
    }
    @Override
    public String getUserAccountKey() {
        return "displayname";
    }

    public AbstractScene getCurrentScene() {
        return this.currentScene;
    }

    public AbstractView getCurrentView() {
        return this.currentView;
    }

    @Override
    public Jdbc getJdbc() {
        GearApplication app = (GearApplication) this.getApplication();
        return app.getJdbc();
    }

    public void onMenuItemClick(AjaxRequestTarget target, String sceneName, String panelName) {
        this.resolvePage(sceneName, panelName);
        target.add(this.currentView);
        target.add(this.nav);
    }

}
