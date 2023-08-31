package org.tiny.gear.panels;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.attributes.IAjaxCallListener;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.tiny.gear.GearApplication;
import org.tiny.gear.Index;
import org.tiny.gear.RoleController;
import org.tiny.gear.model.MenuItem;
import org.tiny.gear.scenes.AbstractScene;
import org.tiny.gear.scenes.AbstractView;
import org.tiny.gear.scenes.SceneTable;
import org.tiny.wicket.onelogin.SamlSession;

/**
 * @author dtmoyaji
 */
public abstract class NavigationPanel extends Panel {

    public static final long serialVersionUID = -1L;

    //private Label menuMoc;
    private ListView<String> sceneNameList;

    private SceneTable sceneTable;

    private Roles currentRoles;

    private AbstractView currentPanel;
    private AbstractScene currentScene;

    private Index parent;

    public NavigationPanel(String id, Index index) {
        super(id);

        this.sceneTable = (SceneTable) index.getGearApplication().getCachedTable(SceneTable.class);
        this.sceneTable.setJdbc(index.getJdbc());

        this.parent = index;

        SamlSession session = (SamlSession) this.getSession();
        this.currentRoles = session.getRoles();
        if (this.currentRoles.size() < 1) {
            this.currentRoles = RoleController.getGuestRoles();
        }

        this.resolve(index);

        ArrayList<String> sceneClassNames = this.getSceneClassNames();
        this.sceneNameList = new ListView<String>("menus", sceneClassNames) {
            public static final long serialVersionUID = -1L;

            @Override
            protected void populateItem(ListItem<String> sceneClassNameItem) {

                String sceneClassName = sceneClassNameItem.getModelObject();
                AbstractScene scene = ((GearApplication) this.getApplication())
                        .getCachedScene(sceneClassName);

                AjaxLink menuLink = new AjaxLink<>("menuItem") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        target.add(this);
                        // 既定のビューに遷移
                        NavigationPanel.this.currentScene = scene;
                        NavigationPanel.this.onMenuItemClick(
                                target,
                                scene.getSceneKey(),
                                scene.getDefaultViewClass().getCanonicalName(),
                                null
                        );
                        ArrayList<MenuItem> items = scene.getSubmenuItems();
                        if (items.size() > 0) {
                            for (int i = 0; i < items.size(); i++) {
                                if(i==0){
                                    items.get(i).setPrimary(true);
                                }else{
                                    items.get(i).setPrimary(false);
                                }
                            }
                        }
                    }

                    @Override
                    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                        super.updateAjaxAttributes(attributes);
                        NavigationPanel.this.addReloadEvent(attributes);
                    }
                };
                menuLink.setOutputMarkupId(true);
                menuLink.add(new Label("menuCaption", Model.of(scene.getSceneName())));
                sceneClassNameItem.add(menuLink);

                if (!scene.isAuthenticated(NavigationPanel.this.currentRoles)) {
                    sceneClassNameItem.setVisible(false);
                }

                ArrayList<MenuItem> submenuItems = scene.getSubmenuItems();
                ListView<MenuItem> submenu = new ListView<MenuItem>("subMenu", submenuItems) {
                    public static final long serialVersionUID = -1L;

                    @Override
                    protected void populateItem(ListItem<MenuItem> item) {
                        MenuItem itemObject = item.getModelObject();

                        AjaxLink link = new AjaxLink<>("subMenuItem") {
                            @Override
                            public void onClick(AjaxRequestTarget target) {
                                NavigationPanel.this.onMenuItemClick(
                                        target,
                                        scene.getSceneKey(),
                                        itemObject.getView().getName(),
                                        itemObject
                                );

                                ArrayList<MenuItem> menuitems = scene.getSubmenuItems();
                                for (MenuItem item : menuitems) {
                                    if (item.equals(itemObject)) {
                                        itemObject.setPrimary(true);
                                    } else {
                                        item.setPrimary(false);
                                    }
                                }
                            }

                            @Override
                            protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                                super.updateAjaxAttributes(attributes);
                                NavigationPanel.this.addReloadEvent(attributes);
                            }
                        };
                        link.add(new Label("subMenuCaption", Model.of(itemObject.getText())));
                        item.add(link);

                        if (itemObject.isPrimary()) {
                            link.add(AttributeModifier.append("current", "true"));
                            item.add(AttributeModifier.append("current", "true"));
                        } else {
                            link.add(AttributeModifier.append("current", "false"));
                            item.add(AttributeModifier.append("current", "false"));
                        }

                        if (!itemObject.isAuthenticated(currentRoles)) {
                            item.setVisible(false);
                        }
                    }
                };

                sceneClassNameItem.add(submenu);

                if (!currentScene.getClass()
                        .getName().equals(scene.getClass().getName())) {
                    submenu.setVisible(false);
                } else {
                    submenu.setVisible(true);
                    sceneClassNameItem.add(AttributeModifier.append("current", "true"));
                }
            }
        };

        this.add(this.sceneNameList);

    }

    public final void resolve(Index index) {
        this.currentPanel = index.getCurrentView();
        this.currentScene = index.getCurrentScene();
    }

    public void addReloadEvent(AjaxRequestAttributes attributes) {
        AjaxCallListener listener = new AjaxCallListener();
        listener.onBeforeSend("document.getElementById('loading').style.visibility = 'visible';")
                .onComplete("document.getElementById('loading').style.visibility = 'hidden';");

        // AjaxCallListenerのリストを取得
        List<IAjaxCallListener> listeners = attributes.getAjaxCallListeners();
        // AjaxCallListenerのインスタンスをリストに追加
        listeners.add(listener);
    }

    public final ArrayList<String> getSceneClassNames() {
        ArrayList<String> rvalue = new ArrayList<>();
        try {
            SceneTable sr = new SceneTable();
            sr.setJdbc(this.parent.getJdbc());
            try (ResultSet rs = sr.select(sr.Ordinal.asc())) {
                while (rs.next()) {
                    rvalue.add(sr.SceneClassName.of(rs));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(NavigationPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rvalue;
    }

    public abstract void onMenuItemClick(AjaxRequestTarget target, String sceneName, String panelName, MenuItem menuItem);

}
