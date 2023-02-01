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
package org.tiny.gear.model;

import java.io.Serializable;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.tiny.gear.IRoleChecker;
import org.tiny.gear.RoleController;
import org.tiny.gear.panels.AbstractMainPanel;
import org.tiny.gear.scene.AbstractScene;

/**
 *
 * @author bythe
 */
public class MenuItem implements Serializable, IRoleChecker {

    private String text;

    private String url;

    private Roles allowed;

    private Class<? extends AbstractScene> scene;
    private Class<? extends AbstractMainPanel> panel;

    public MenuItem(String text, String url, Roles allowed) {
        this.text = text;
        this.url = url;
        this.allowed = allowed;
    }

    public MenuItem(String text, Class<? extends AbstractScene> scene, Class<? extends AbstractMainPanel> view, Roles allowed) {
        this.text = text;
        this.scene = scene;
        this.panel = view;
        this.setUrl(scene, view);
        this.allowed = allowed;
    }

    public boolean isMatchedScene(Class<? extends AbstractScene> scene) {
        return this.scene.getName().equals(scene.getName());
    }

    public boolean isMatchedMainPanel(Class<? extends AbstractMainPanel> panel) {
        if (this.panel == null) {
            return false;
        }
        return this.panel.getName().equals(panel.getName());
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    public final void setUrl(Class scene, Class mainPanel) {
        String urlTemplate = "?scene=%s&view=%s";
        urlTemplate = String.format(
                urlTemplate,
                scene.getName(),
                mainPanel.getName()
        );
        this.url = urlTemplate;
    }

    /**
     * @return the allowed
     */
    public Roles getAllowed() {
        return allowed;
    }

    /**
     * @param allowed the allowed to set
     */
    public void setAllowed(Roles allowed) {
        this.allowed = allowed;
    }

    @Override
    public boolean isAllowed(Roles roles) {
        return RoleController.isRolesMatched(this.allowed, roles);
    }

}
