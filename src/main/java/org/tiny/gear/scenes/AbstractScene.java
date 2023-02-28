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
package org.tiny.gear.scenes;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.tiny.gear.IRoleChecker;
import org.tiny.gear.RoleController;
import org.tiny.gear.model.MenuItem;
import org.tiny.gear.view.AbstractView;

/**
 *
 * @author bythe
 */
public abstract class AbstractScene implements Serializable, IRoleChecker {

    private int ordinal = -1;

    private String sceneName;

    private String sceneKey;

    public static String DEFAULT_VIEW = "default_view";

    private ArrayList<MenuItem> menus;

    private HashMap<String, AbstractView> panels;

    private final Roles allowed;

    private AbstractView defaultPanel;

    public AbstractScene(Roles allowed) {

        this.allowed = allowed;

        this.menus = new ArrayList<>();
        this.panels = new HashMap<>();

    }

    @Override
    public boolean isAllowed(Roles role) {
        return RoleController.isRolesMatched(this.allowed, role);
    }

    public void setOrdinal(int order) {
        this.ordinal = order;
    }

    public int getOrdinal() {
        return this.ordinal;
    }

    public String getSceneName() {
        return this.sceneName;
    }

    public String getSceneKey() {
        return this.getClass().getName();
    }

    public boolean isSceneKeyMatch(String key) {
        return key.equals(this.getSceneKey());
    }

    /**
     * @return the menus
     */
    public ArrayList<MenuItem> getMenus() {
        return menus;
    }

    /**
     * @param menus the menus to set
     */
    public void setMenus(ArrayList<MenuItem> menus) {
        this.menus = menus;
    }

    /**
     * @return the panels
     */
    public HashMap<String, AbstractView> getPanels() {
        return panels;
    }

    public AbstractView getPanel(String key) {
        return this.panels.get(key);
    }

    /**
     * @param panels the panels to set
     */
    public void setPanels(HashMap<String, AbstractView> panels) {
        this.panels = panels;
    }

    public boolean isPrimary() {
        return false;
    }

    public void putMenu(String menuName, Class<? extends AbstractView> view, Roles roles, boolean primary) {
        try {
            this.getMenus().add(new MenuItem(menuName, this.getClass(), view, roles));
            AbstractView newView = view.getConstructor().newInstance();
            this.getPanels().put(view.getName(), newView);
            if (primary) {
                this.defaultPanel = newView;
            }
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(AbstractScene.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public AbstractView getDefaultPanel() {
        return this.defaultPanel;
    }

    /**
     * ユーザーが表示権限を持っているかどうかをチェックする。
     *
     * @param target
     * @param userRole
     * @return
     */
    public boolean isAuthenticated(AbstractView target, Roles userRole) {
        boolean rvalue = false;
        ArrayList<MenuItem> menus = this.getMenus();
        for (MenuItem menu : menus) {
            if (menu.isMatchedUrl(this.getClass(), target.getClass())) {
                rvalue = menu.isAllowed(userRole);
                break;
            }
        }
        return rvalue;
    }
}
