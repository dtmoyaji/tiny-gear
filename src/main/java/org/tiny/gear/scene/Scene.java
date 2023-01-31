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

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.panel.Panel;
import org.tiny.gear.RoleController;

/**
 *
 * @author bythe
 */
public class Scene {
    
    private int ordinal = -1;
    
    private String title;

    public static String DEFAULT_VIEW = "default_view";

    private ArrayList<MenuItem> menus;

    private HashMap<String, Panel> panels;
    
    private final Roles allowed;

    public Scene(Roles allowed) {
        
        this.allowed = allowed;
        
        this.menus = new ArrayList<>();
        this.panels = new HashMap<>();

    }
    
    public boolean isAllowed(Roles role){
        return RoleController.isRolesMatched(this.allowed, role);
    }
    
    public void setOrdinal(int order){
        this.ordinal = order;
    }
    
    public int getOrdinal(){
        return this.ordinal;
    }
    
    public String getTitle(){
        return this.title;
    }
    
    public void setTitle(String title){
        this.title = title;
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
    public HashMap<String, Panel> getPanels() {
        return panels;
    }

    public Panel getPanel(String key) {
        return this.panels.get(key);
    }

    /**
     * @param panels the panels to set
     */
    public void setPanels(HashMap<String, Panel> panels) {
        this.panels = panels;
    }

}
