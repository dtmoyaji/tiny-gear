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
package org.tiny.gear.scene;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.tiny.gear.RoleController;
import org.tiny.gear.panels.UserInfoPanel;

/**
 *
 * @author bythe
 */
public class UserControlScene extends AbstractScene {

    public UserControlScene(Roles allowed) {
        super(allowed);

        Roles generalRoles = RoleController.getUserRoles();
        Roles adminRoles = RoleController.getAdminRoles();

        this.getPanels().put(AbstractScene.DEFAULT_VIEW, new UserInfoPanel("scenePanel"));
        this.getMenus().add(new MenuItem("ユーザー情報", UserControlScene.class, UserInfoPanel.class, generalRoles));
        this.getMenus().add(new MenuItem("KeyCloak同期設定", "?menu=menu1", adminRoles));
//        this.getMenus().add(new MenuItem("ユーザー一覧", "?menu=menu2", adminRoles));
    }

    @Override
    public String getSceneName() {
        return "アカウント";
    }

}
