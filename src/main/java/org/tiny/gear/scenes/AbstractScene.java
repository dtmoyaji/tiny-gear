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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.tiny.gear.GearApplication;
import org.tiny.gear.IRoleChecker;
import org.tiny.gear.RoleController;
import org.tiny.gear.model.MenuItem;

/**
 * ブラウザに表示するコンテンツの情報を格納するクラス。 １つの作業単位をシーンにまとめて格納する。 シーンには、複数のビューとメニューが格納される。
 * このクラスを継承してシーンを作成することを推奨する。
 *
 * @author dtmoyaji
 */
public abstract class AbstractScene implements Serializable, IRoleChecker {

    public static final long serialVersionUID = -1L;

    private int ordinal = -1;

    private String sceneName;

    private String sceneKey;

    public static String DEFAULT_VIEW = "default_view";

    private ArrayList<MenuItem> menus;

    private HashMap<String, String> panels;

    private Roles allowed;

    private String defaultPanelName;

    protected GearApplication application;

    public AbstractScene(Roles allowed, GearApplication app) {
        this.setInitializer(allowed, app);
    }
    
    public AbstractScene(){
    }
    
    public void setInitializer(Roles allowed, GearApplication app){
        this.allowed = allowed;
        this.application = app;

        this.menus = new ArrayList<>();
        this.panels = new HashMap<>();

        this.defineMenu();

    }

    /**
     * 派生クラスはメニューをここで定義する。 putMenuを呼び出して、メニューの表示テキストと、格納するビューを登録すること。
     */
    public abstract void defineMenu();

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

    public abstract String getSceneName();

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
    public HashMap<String, String> getPanelNames() {
        return panels;
    }

    public String getPanel(String key) {
        return this.panels.get(key);
    }

    /**
     * @param panels the panels to set
     */
    public void setPanels(HashMap<String, String> panels) {
        this.panels = panels;
    }

    public boolean isPrimary() {
        return false;
    }

    public void putMenu(String menuName, Class<? extends AbstractView> view, Roles roles, boolean primary) {
        try {
            this.getMenus().add(new MenuItem(menuName, this.getClass(), roles, view));
            this.getPanelNames().put(view.getName(), view.getCanonicalName());
            if (primary) {
                this.defaultPanelName = view.getCanonicalName();
            }
        } catch (SecurityException | IllegalArgumentException ex) {
            Logger.getLogger(AbstractScene.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void putMenu(String menuName, Class<? extends AbstractView> view, Class[] refTables, Roles roles, boolean primary) {

        TablesForAbstractView tfav = new TablesForAbstractView();
        tfav.alterOrCreateTable(this.application.getJdbc());

        String viewFullName = view.getCanonicalName();

        for (Class table : refTables) {
            String tableFullName = table.getCanonicalName();
            tfav.clearValues();
            tfav.merge(tfav.ViewClassName.setValue(viewFullName),
                    tfav.TableClassName.setValue(tableFullName));
            this.getGearApplication().getCachedTable(table);
        }

        this.putMenu(menuName, view, roles, primary);
    }
    
    public abstract Class getDefaultViewClass();

    public AbstractView createDefaultView() {
        AbstractView rvalue = this.getGearApplication().getCachedView(this.getDefaultViewClass());
        return rvalue;
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
            if(menu.getViewClassName().equals(target.getClass().getCanonicalName())){
                rvalue = menu.isAllowed(userRole);
                break;
            }
        }
        return rvalue;
    }
    
    public GearApplication getGearApplication(){
        return this.application;
    }
}
