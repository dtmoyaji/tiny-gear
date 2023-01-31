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

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.tiny.gear.Index;
import org.tiny.gear.RoleController;
import org.tiny.gear.scene.AbstractScene;
import org.tiny.wicket.onelogin.SamlSession;

/**
 *
 * @author bythe
 */
public class NavigationPanel extends Panel {

    //private Label menuMoc;
    private ListView<AbstractScene> scenes;

    private Roles currentRoles;

    public NavigationPanel(String id, Index index) {
        super(id);

        SamlSession session = (SamlSession) this.getSession();
        this.currentRoles = session.getRoles();
        if (this.currentRoles.size() < 1) {
            this.currentRoles = RoleController.getGuestRoles();
        }

        this.scenes = new ListView<AbstractScene>("menus", index.getScenes()) {

            @Override
            protected void populateItem(ListItem<AbstractScene> item) {
                AbstractScene scene = item.getModelObject();

                ExternalLink link = new ExternalLink("menuItem",
                        "?scene="
                        + scene.getClass().getName(), scene.getSceneName());
                item.add(link);
                if (!scene.isAllowed(currentRoles)) {
                    item.setVisible(false);
                }
            }
        };

        this.add(this.scenes);

    }

}
