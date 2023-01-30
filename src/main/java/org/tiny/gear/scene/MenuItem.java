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


/**
 *
 * @author bythe
 */
public class MenuItem {
    
    private String text;
    
    private String url;
    
    private Roles allowed;
    
    public MenuItem(String text, String url, Roles allowed){
        this.text = text;
        this.url = url;
        this.allowed = allowed;
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
    
}
