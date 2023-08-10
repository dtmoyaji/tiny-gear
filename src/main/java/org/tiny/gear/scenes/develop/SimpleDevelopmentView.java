/*
 * Copyright 2023 MURAKAMI Takahiro <daianji@gmail.com>.
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
package org.tiny.gear.scenes.develop;

import org.tiny.gear.GearApplication;
import org.tiny.gear.panels.GroovyEditor;
import org.tiny.gear.scenes.AbstractView;

/**
 */
public class SimpleDevelopmentView extends AbstractView {

    public static final long serialVersionUID = -1L;

    private GroovyEditor groovyExec;

    public SimpleDevelopmentView(GearApplication supplier) {
        super(supplier);
    }
    
    public void redraw(){
        this.removeAll();
        this.groovyExec = new GroovyEditor("groovyExecutor");
        this.add(this.groovyExec);
    }

    @Override
    public String getTitle() {
        return "簡易開発ビュー";
    }

}
