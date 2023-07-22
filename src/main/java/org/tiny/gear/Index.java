package org.tiny.gear;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.tiny.datawrapper.Column;
import org.tiny.datawrapper.IJdbcSupplier;
import org.tiny.datawrapper.Jdbc;
import org.tiny.datawrapper.Table;
import org.tiny.gear.model.SystemVariables;
import org.tiny.gear.model.UserInfo;
import org.tiny.gear.panels.HumbergerIcon;
import org.tiny.gear.panels.NavigationPanel;
import org.tiny.gear.panels.crud.DataTableView;
import org.tiny.gear.panels.crud.FilterAndEdit;
import org.tiny.gear.panels.crud.RecordEditor;
import org.tiny.gear.scenes.AbstractScene;
import org.tiny.gear.scenes.DevelopScene;
import org.tiny.gear.scenes.PrimaryScene;
import org.tiny.gear.scenes.SettingScene;
import org.tiny.gear.view.AbstractView;
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
    private AbstractView currentPanel;

    private final NavigationPanel nav;
    private final HumbergerIcon humbergerIcon;

    private ArrayList<AbstractScene> scenes;

    private FilterAndEdit filterAndEdit;

    public Index(final PageParameters parameters) {
        super(parameters);

        this.initTable();

        GearApplication app = (GearApplication) this.getApplication();
        String svtitle = (String) app.getProperties("tiny.gear").get("tiny.gear.service.title");

        this.serviceTitle = new Label("serviceTitle", Model.of(svtitle));
        this.add(this.serviceTitle);

        // 初期ページの取得
        this.currentScene = new PrimaryScene(RoleController.getUserRoles());
        this.scenes = this.getScenes();

        // 指定された状態に応じたシーンを表示する処理
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
        } else { //表示に問題がないときは、ユーザー情報を上書きに行く
            SamlAuthInfo ainfo = ((SamlSession) this.getSession()).getSamlAuthInfo();
            UserInfo uinfo = new UserInfo();
            uinfo.alterOrCreateTable(this.getJdbc());
            uinfo.setDebugMode(true);
            uinfo.UserId.setValue(ainfo.getAttributeString("externalKey"));
            uinfo.UserName.setValue(ainfo.getAttributeString("username"));
            uinfo.LastAccess.setValue(new Timestamp(System.currentTimeMillis()));
            uinfo.AttributeJson.setValue(ainfo.toJson()); // TODO: JSONの生成ロジックを作ること。
            uinfo.merge();
        }

        this.add(this.currentPanel);

        this.nav = new NavigationPanel("menus", this);
        this.nav.setOutputMarkupId(true);
        this.add(this.nav);

        this.humbergerIcon = new HumbergerIcon("humbergerIcon", "humbergerTarget");
        this.add(this.humbergerIcon);

        UserInfo uinfo = new UserInfo();
        uinfo.setJdbc(this.getJdbc());

        this.filterAndEdit = new FilterAndEdit("filterAndEdit", uinfo, this) {
            @Override
            public void beforeConstructDataTableView(Table myTable, DataTableView dataTableView) {
                myTable.get(uinfo.AttributeJson.getName()).setVisibleType(Column.VISIBLE_TYPE_HIDDEN);
            }

            @Override
            public void beforeConstructRecordEditor(Table myTable, RecordEditor recordEditor) {
                myTable.get(uinfo.AttributeJson.getName()).setVisibleType(Column.VISIBLE_TYPE_TEXTAREA);
            }
        };
        this.add(this.filterAndEdit);

    }

    private void initTable() {
        UserInfo uinfo = new UserInfo();
        SystemVariables sysvar = new SystemVariables();

        uinfo.alterOrCreateTable(this.getJdbc());
        sysvar.alterOrCreateTable(this.getJdbc());
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
