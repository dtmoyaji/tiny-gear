package org.tiny.gear.panels;

import java.util.ArrayList;
import java.util.Properties;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;

/**
 *
 * @author dtmoyaji
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
