package org.tiny.gear;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
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
import org.tiny.gear.panels.crud.DataControl;
import org.tiny.gear.panels.crud.DataTableView;
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

    private DataTableView dataListView;
    private RecordEditor recordEditor;

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
            uinfo.UserId.setValue(ainfo.getAttributeString("user_id"));
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
        uinfo.AttributeJson.setVisibleType(Column.VISIBLE_TYPE_HIDDEN);
        this.dataListView = new DataTableView("dataListView", uinfo, this) {
            @Override
            public Class<? extends Panel> getExtraColumn() {
                return Panel.class;
            }
        };
        this.add(this.dataListView);

        uinfo = new UserInfo();
        uinfo.setJdbc(this.getJdbc());
        this.recordEditor = new RecordEditor("recordEditor") {
            @Override
            public void beforeFormBuild() {
            }

            @Override
            public void afterFormBuild() {
            }

            @Override
            public void onSubmit(AjaxRequestTarget target, Table targetTable) {
                ArrayList<DataControl> controls = this.getDataControls();
                targetTable.clearValues();
                for (DataControl control : controls) {
                    String data = "";
                    if (control.getColumn().getSplitedName().equals("LAST_ACCESS")) {
                        control.setValue(
                                LocalDateTime.now().format(
                                        DateTimeFormatter.ISO_LOCAL_DATE_TIME
                                )
                        );
                    }
                    data = control.getValue();
                    targetTable.get(control.getColumn().getName()).setValue(data);
                }
                targetTable.setDebugMode(true);
                targetTable.merge();
                target.add(this);
                Index.this.dataListView.redraw();
                target.add(Index.this.dataListView);
            }

            @Override
            public void onCancel(AjaxRequestTarget target, Table targetTable) {
            }

        };
        this.recordEditor.buildForm(uinfo);
        this.recordEditor.setOutputMarkupId(true);
        ResultSet rs = uinfo.select();
        try {
            if (rs.next()) {
                this.recordEditor.stackData(rs);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Index.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.add(this.recordEditor);
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
