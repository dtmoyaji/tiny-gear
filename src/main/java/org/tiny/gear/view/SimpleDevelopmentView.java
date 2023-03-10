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
package org.tiny.gear.view;

import org.tiny.gear.panels.GroovyEditor;

/**
 */
public class SimpleDevelopmentView extends AbstractView {

    private final GroovyEditor groovyExec;

    public SimpleDevelopmentView() {
        super();

        this.groovyExec = new GroovyEditor("groovyExecutor");
        this.add(this.groovyExec);
    }

    @Override
    public String getTitle() {
        return "簡易開発ビュー";
    }

}
