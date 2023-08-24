package org.tiny.gear.panels;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * @author dtmoyaji
 */
public class PopUpPanel extends AbstractPanel {

    Panel childPanel;

    public PopUpPanel(String id, Panel childPanel) {
        super(id);
        
        this.redraw(childPanel);
        this.setOutputMarkupId(true);
    }

    @Override
    public String getTitle() {
        return "popUP";
    }

    public void redraw(Panel childPanel) {
        super.redraw();
        this.childPanel = childPanel;
        this.add(this.childPanel);
    }
    
    public void popUp(){
        this.add(AttributeModifier.replace("popup", "true"));
    }
    
    public void hide(){
        this.add(AttributeModifier.replace("popup", "false"));
    }
}
