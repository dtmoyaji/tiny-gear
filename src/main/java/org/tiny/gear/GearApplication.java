package org.tiny.gear;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.resource.FileSystemResource;
import org.tiny.datawrapper.IJdbcSupplier;
import org.tiny.datawrapper.Jdbc;
import org.tiny.wicket.SamlWicketApplication;

/**
 * @see org.tiny.Start#main(String[])
 */
public class GearApplication extends SamlWicketApplication implements IJdbcSupplier {

    private HashMap<String, Properties> environments;

    private Jdbc jdbc;

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

    public Properties getProperties(String PropertyName) {
        if (this.environments == null) {
            this.environments = new HashMap<>();
        }
        if (this.environments.get(PropertyName) == null) {
            try {
//                File currdir = new File(".");
//                System.out.println(currdir.getAbsolutePath());
                String filePath = "/" + PropertyName + ".properties";
                Properties prop = new Properties();
                prop.load(this.getClass().getResourceAsStream(filePath));
                this.environments.put(PropertyName, prop);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(GearApplication.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(GearApplication.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return this.environments.get(PropertyName);
    }

    @Override
    public Jdbc getJdbc() {
        if (this.jdbc == null) {
            
            Properties tinygear = this.getProperties("org.tiny.gear");
            
            this.jdbc = new Jdbc();
            this.jdbc.setDriver(tinygear.getProperty("org.tiny.gear.Jdbc.driver"));
            this.jdbc.setUrl(tinygear.getProperty("org.tiny.gear.Jdbc.url"));
            this.jdbc.setUser(tinygear.getProperty("org.tiny.gear.Jdbc.user"));
            this.jdbc.setPassword(tinygear.getProperty("org.tiny.gear.Jdbc.password"));
        }
        return this.jdbc;
    }
}
