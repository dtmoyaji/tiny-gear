package org.tiny.gear.panels;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

/**
 *
 * @author dtmoyaji
 */
public class PopUpPanel extends AbstractPanel {

    private String title = "popUp";
    private Form popupControl;
    private AjaxButton closeButton;
    private Panel childPanel;

    public PopUpPanel(String id, Panel childPanel) {
        super(id);

        this.redraw(childPanel);
        this.setOutputMarkupId(true);
    }

    @Override
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title){
        this.title = title;
    }

    public void redraw(Panel childPanel) {
        super.redraw();

        this.popupControl = new Form("popupControl");
        this.add(this.popupControl);

        this.closeButton = new AjaxButton("closeButton", Model.of("")) {
            @Override
            public void onSubmit(AjaxRequestTarget target) {
                PopUpPanel.this.hide();
                target.add(PopUpPanel.this);
            }
        };
        this.popupControl.add(this.closeButton);

        this.childPanel = childPanel;
        this.add(this.childPanel);

    }
    
    public Panel getChildPanel(){
        return this.childPanel;
    }

    public void popUp() {
        this.add(AttributeModifier.replace("popup", "true"));
    }

    public void hide() {
        this.add(AttributeModifier.replace("popup", "false"));
    }
}
