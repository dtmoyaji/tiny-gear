package org.tiny.gear;

import org.apache.wicket.markup.html.WebPage;
import org.tiny.wicket.SamlWicketApplication;

/**
 * @see org.tiny.Start#main(String[])
 */
public class GearApplication extends SamlWicketApplication {

    /**
     * @return WebPage
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class<? extends WebPage> getHomePage() {
        return Index.class;
    }

    /**
     * @see org.apache.wicket.Application#init()
     */
    @Override
    public void init() {
        super.init();

    }

    @Override
    public String getSSOPageMountPoint() {
        return "SamlLogin";
    }

    @Override
    public String getSLOPageMountPoint() {
        return "SamlLogout";
    }
}
