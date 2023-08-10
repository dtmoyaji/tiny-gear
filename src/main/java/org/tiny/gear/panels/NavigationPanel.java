/*
 * Copyright 2023 bythe.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
    private ListView<String> scenes;

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

        ArrayList<String> sceneNames = this.getScenes();
        this.scenes = new ListView<String>("menus", sceneNames) {
            public static final long serialVersionUID = -1L;

            @Override
            protected void populateItem(ListItem<String> item) {

                String sceneClass = item.getModelObject();
                AbstractScene scene = ((GearApplication) this.getApplication())
                        .getCachedAbstractScene(sceneClass);

                AjaxLink link = new AjaxLink<>("menuItem") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        target.add(this);
                        // 既定のビューに遷移
                        NavigationPanel.this.onMenuItemClick(
                                target,
                                scene.getSceneKey(),
                                scene.getDefaultViewClass().getCanonicalName()
                        );
                    }

                    @Override
                    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                        super.updateAjaxAttributes(attributes);
                        NavigationPanel.this.addReloadEvent(attributes);
                    }
                };
                link.setOutputMarkupId(true);
                link.add(new Label("menuCaption", Model.of(scene.getSceneName())));
                item.add(link);

                if (!scene.isAllowed(currentRoles)) {
                    item.setVisible(false);
                }

                ListView<MenuItem> submenu = new ListView<MenuItem>("subMenu", scene.getMenus()) {
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
                                        itemObject.getViewClassName()
                                );
                            }

                            @Override
                            protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                                super.updateAjaxAttributes(attributes);
                                NavigationPanel.this.addReloadEvent(attributes);
                            }
                        };
                        link.add(new Label("subMenuCaption", Model.of(itemObject.getText())));
                        item.add(link);

                        if (itemObject.isMatchedMainPanel(currentPanel.getClass())) {
                            link.add(AttributeModifier.append("current", "true"));
                            item.add(AttributeModifier.append("current", "true"));
                        }

                        if (!itemObject.isAllowed(currentRoles)) {
                            item.setVisible(false);
                        }
                    }
                };

                item.add(submenu);

                if (!currentScene.getClass()
                        .getName().equals(scene.getClass().getName())) {
                    submenu.setVisible(false);
                } else {
                    item.add(AttributeModifier.append("current", "true"));
                }
            }
        };

        this.add(this.scenes);

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

    public final ArrayList<String> getScenes() {
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

    public abstract void onMenuItemClick(AjaxRequestTarget target, String sceneName, String panelName);

}
