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

import java.util.ArrayList;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.tiny.gear.scene.MenuItem;
import org.tiny.gear.scene.Scene;
import org.tiny.wicket.onelogin.SamlSession;

/**
 *
 * @author bythe
 */
public class NavigationPanel extends Panel {

    private Label menuMoc;

    public NavigationPanel(String id) {
        super(id);

        this.menuMoc = new Label("menus", Model.of(""));
        this.add(this.menuMoc);
    }

    public void showMenus(Scene view) {

        Roles currentRoles = ((SamlSession) this.getSession()).getRoles();

        ArrayList<MenuItem> menus = view.getMenus();
        String list = "";
        for (MenuItem menu : menus) {
            if (this.isRolesMatched(currentRoles, menu.getAllowed())) {
                list += "," + menu.getText();
            }
        }
        if (list.length() > 0) {
            list = list.substring(1);
        }
        this.menuMoc.setDefaultModelObject(list);
    }

    public boolean isRolesMatched(Roles userRole, Roles menuRole) {
        boolean rvalue = false;
        for (String usrrole : userRole) {
            for (String menurole : menuRole) {
                if (menurole.equals(usrrole)) {
                    rvalue = true;
                    break;
                }
            }
        }
        return rvalue;
    }

}
