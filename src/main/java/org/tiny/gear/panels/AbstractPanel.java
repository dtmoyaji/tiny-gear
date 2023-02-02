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
package org.tiny.gear.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 *
 * @author MURAKAMI Takahiro <daianji@gmail.com>
 */
public abstract class AbstractPanel extends Panel {

    private Label titleLabel;

    public AbstractPanel(String id) {
        super(id);

        this.titleLabel = new Label("title", Model.of(this.getTitle()));
        this.add(this.titleLabel);
    }

    public String getTitle() {
        return "AbstractPanel";
    }

}
