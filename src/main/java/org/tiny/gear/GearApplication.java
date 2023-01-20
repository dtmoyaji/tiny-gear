package org.tiny.gear;

import java.io.File;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.resource.FileSystemResource;
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
        this.mountResources();
    }

    public void mountResources() {
        String classesPath = this.getServletContext().getRealPath("/");
        // Webアプリの CLASSPATH の下、img の下に画像ファイルが存在する場合、、 
        this.getSharedResources().add("img/kkrn_icon_menu_11.png", new FileSystemResource(new File(classesPath + "img/kkrn_icon_menu_11.png").toPath()));
        this.mountResource("img/kkrn_icon_menu_11.png", new SharedResourceReference("img/kkrn_icon_menu_11.png"));
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
