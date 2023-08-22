package org.tiny.gear.scenes.primary;

import org.tiny.gear.GearApplication;
import org.tiny.gear.scenes.AbstractView;

/**
 *
 * @author dtmoyaji
 */
public class PrimaryView extends AbstractView {

    public static final long serialVersionUID = -1L;
    
    public PrimaryView(GearApplication supplier){
        super(supplier);
    }

    @Override
    public String getTitle() {
        return "はじめに";
    }

    @Override
    public void redraw() {
        super.redraw();
    }

}
