package org.tiny.gear.scenes.setting;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.tiny.gear.GearApplication;
import org.tiny.gear.scenes.AbstractView;

/**
 * GearApplication内のキャッシュをクリアするためのビュー
 *
 * @author dtmoyaji
 */
public class CacheControlView extends AbstractView {

    private Form form;
    private Label caption;
    private AjaxButton reloadAll;

    public CacheControlView(GearApplication app) {
        super(app);
    }
    
    @Override
    public void redraw(){
        super.redraw();
        
        this.form = new Form("form");
        this.add(this.form);
        
        this.caption = new Label("reloadAllCaption", Model.of("キャッシュをクリアする"));
        this.form.add(this.caption);

        this.reloadAll = new AjaxButton("reloadAll", Model.of("実行")) {
            @Override
            public void onSubmit(AjaxRequestTarget target) {
                CacheControlView.this.reloadTableCache(target);
            }
        };

        this.form.add(this.reloadAll);
        this.add(this.form);
    }

    @Override
    public String getTitle() {
        return "システムキャッシュ";
    }

    public void reloadTableCache(AjaxRequestTarget target) {
        this.getGearApplication().clearCache();
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "CACHE CLEAR!!");
    }

}
