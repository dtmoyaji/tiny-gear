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
package org.tiny.gear.panels;

import java.util.ArrayList;
import java.util.Properties;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;

/**
 *
 * @author bythe
 */
public abstract class PropertyPanel extends AbstractPanel {

    public static final long serialVersionUID = -1L;

    private Properties properties;
    private ListView<String> listView;

    public PropertyPanel(String id) {
        super(id);

        String filter = this.getPrefix();

        this.properties = this.getProperties();
        ArrayList<String> keys = new ArrayList(this.properties.keySet());

        this.listView = new ListView<>("properties", keys) {
            public static final long serialVersionUID = -1L;

            @Override
            protected void populateItem(ListItem<String> item) {
                String key = item.getModelObject();
                String value = properties.getProperty(key);

                Label keyLabel = new Label("key", Model.of(key));
                Label valueLabel = new Label("value", Model.of(value));

                item.add(keyLabel);
                item.add(valueLabel);

                if (!key.startsWith(filter)) {
                    item.setVisible(false);
                }

            }
        };
        this.add(this.listView);
    }

    @Override
    public abstract String getTitle();

    public abstract String getPrefix();

    public abstract Properties getProperties();

}
