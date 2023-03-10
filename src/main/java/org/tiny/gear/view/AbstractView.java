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
package org.tiny.gear.view;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 * シーンに含まれるビューの抽象クラス
 * @author dtmoyaji
 */
public abstract class AbstractView extends Panel {
   
    private final Label title;
    
    public AbstractView(){
        super("scenePanel");
        this.title = new Label("panelTitle", Model.of(this.getTitle()));
        this.add(this.title);
    }
    
    public abstract String getTitle();
    
    
}
