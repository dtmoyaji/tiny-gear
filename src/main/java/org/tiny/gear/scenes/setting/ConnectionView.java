package org.tiny.gear.scenes.setting;

import java.util.Properties;
import org.tiny.gear.GearApplication;
import org.tiny.gear.panels.PropertyPanel;
import org.tiny.gear.scenes.AbstractView;

/**
 *
 * @author dtmoyaji
 */
public class ConnectionView extends AbstractView {

    public static final long serialVersionUID = -1L;

    private PropertyPanel jdbcPanel;
    private PropertyPanel samlPanel;

    public ConnectionView(GearApplication app) {
        super(app);
    }
    
    @Override
    public void redraw(){
        super.redraw();

        this.jdbcPanel = new PropertyPanel("jdbcProperties") {
            public static final long serialVersionUID = -1L;

            @Override
            public String getTitle() {
                return "JDBC接続";
            }

            @Override
            public Properties getProperties() {
                GearApplication app = (GearApplication) this.getApplication();
                Properties props = app.getProperties("tiny.gear");
                return props;
            }

            @Override
            public String getPrefix() {
                return "tiny.gear.jdbc";
            }
        };
        this.add(this.jdbcPanel);

        this.samlPanel = new PropertyPanel("samlProperties") {
            public static final long serialVersionUID = -1L;

            @Override
            public String getTitle() {
                return "SAML設定";
            }

            @Override
            public Properties getProperties() {
                GearApplication app = (GearApplication) this.getApplication();
                Properties props = app.getProperties("onelogin.saml");
                return props;
            }

            @Override
            public String getPrefix() {
                return "onelogin.saml2";
            }
        };
        this.add(this.samlPanel);

    }

    @Override
    public String getTitle() {
        return "各種接続設定";
    }

}
