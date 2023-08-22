package org.tiny.gear.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.tiny.gear.GearApplication;

/**
 *
 * @author dtmoyaji
 */
public abstract class AbstractPanel extends Panel {

    public static final long serialVersionUID = -1L;

    private Label titleLabel;

    public AbstractPanel(String id) {
        super(id);

        this.titleLabel = new Label("title", Model.of(this.getTitle()));
        this.add(this.titleLabel);
    }

    public String getTitle() {
        return "AbstractPanel";
    }
    
    public GearApplication getGearApplication(){
        return (GearApplication) this.getApplication();
    }

}
