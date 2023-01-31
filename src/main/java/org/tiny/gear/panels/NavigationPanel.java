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
import org.tiny.gear.scene.Scene;

/**
 *
 * @author bythe
 */
public class NavigationPanel extends Panel {

    //private Label menuMoc;
    private ListView<Scene> scenes;
    
    private Roles currentRoles;

    public NavigationPanel(String id, Index index) {
        super(id);

        this.scenes = new ListView<Scene>("menus", index.getScenes()) {

            @Override
            protected void populateItem(ListItem<Scene> item) {
                Scene scene = item.getModelObject();
                ExternalLink link = new ExternalLink("menuItem",
                        "?scene=" +
                        scene.getClass().getName(), scene.getTitle());
                item.add(link);
            }
        };

        this.add(this.scenes);

    }

  

}
