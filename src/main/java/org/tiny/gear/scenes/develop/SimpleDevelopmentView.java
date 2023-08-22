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
        super.redraw();
        this.groovyExec = new GroovyEditor("groovyExecutor");
        this.add(this.groovyExec);
    }

    @Override
    public String getTitle() {
        return "簡易開発ビュー";
    }

}
