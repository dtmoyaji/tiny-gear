package org.tiny.gear;

import java.util.ArrayList;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

/**
 *
 * @author dtmoyaji
 */
public class GroovyExecuteButton extends AjaxButton {

    private GearApplication application;

    private Panel panel;
    private String panelName;
    
    private GroovyExecutor groovyExecutor;
    
    public GroovyExecuteButton(String id, IModel<String> model, GearApplication app, Panel panel){
        super(id, model);
        this.application = app;
        this.panel = panel;
        this.panelName = panel.getClass().getName();
        this.groovyExecutor = new GroovyExecutor(this.application);
    }

    public GroovyExecuteButton(String id, IModel<String> model, GearApplication app, Panel panel, String panelName) {
        super(id, model);
        this.application = app;
        this.panel = panel;
        this.panelName = panelName;
        this.groovyExecutor = new GroovyExecutor(this.application);
    }
    
    @Override
    public void onSubmit(AjaxRequestTarget target){
        this.groovyExecutor.execute(
                this.getClassRelativePath(),
                this.panel,
                this.panelName
                );
        ArrayList<Component> updateComponents = this.groovyExecutor.getUpdateComponents();
        for(Component cmp: updateComponents){
            target.add(cmp);
        }
    }

}
