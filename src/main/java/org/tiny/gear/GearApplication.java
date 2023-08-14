package org.tiny.gear;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.resource.SharedResourceReference;
import org.apache.wicket.resource.FileSystemResource;
import org.tiny.datawrapper.IJdbcSupplier;
import org.tiny.datawrapper.Jdbc;
import org.tiny.datawrapper.Table;
import org.tiny.datawrapper.TinyDatabaseException;
import org.tiny.gear.model.ObjectCachInfo;
import org.tiny.gear.model.SystemVariables;
import org.tiny.gear.scenes.AbstractScene;
import org.tiny.gear.scenes.AbstractView;
import org.tiny.gear.scenes.SceneTable;
import org.tiny.wicket.SamlWicketApplication;
import wicket.util.file.File;

/**
 * @see org.tiny.Start#main(String[])
 */
public class GearApplication extends SamlWicketApplication implements IJdbcSupplier, Serializable {

    public static final long serialVersionUID = -1L;

    private Jdbc jdbc;

    private ObjectCachInfo objectCachInfo;

    private SystemVariables systemVariables;

    private HashMap<String, Properties> environments;

    private HashMap<String, AbstractScene> sceneCach = new HashMap<>();

    private Cache<Table> tableCache;

    private Cache<AbstractView> viewCache;

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

        // 暫定的に実装最終的には消す予定
        //getCspSettings().blocking().disabled();
        this.mountResources();
    }

    public void buildCache() {
        if (this.tableCache == null) {
            this.tableCache = new Cache<>(this.getJdbc(), ObjectCachInfo.TYPE_TALBE) {
                @Override
                protected void afterNewInstance(String key, Table data) {
                    data.alterOrCreateTable(getJdbc());
                    this.put(key, data);
                }

                @Override
                protected Table onNewInstance(Constructor constructor) {
                    Table rvalue = null;
                    try {
                        rvalue = (Table) constructor.newInstance();
                        Logger.getLogger(Cache.class.getCanonicalName())
                                .log(Level.INFO, "TABLE: {0} instance created.", rvalue.getClass().getName());
                    } catch (InstantiationException
                            | IllegalAccessException
                            | IllegalArgumentException
                            | InvocationTargetException ex) {
                        Logger.getLogger(GearApplication.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return rvalue;
                }
            };
        }

        if (this.systemVariables == null) {
            this.systemVariables = (SystemVariables) this.getCachedTable(SystemVariables.class);
        }

        if (this.viewCache == null) {
            this.viewCache = new Cache<>(this.getJdbc(), ObjectCachInfo.TYPE_VIEW, GearApplication.class) {
                @Override
                protected AbstractView onNewInstance(Constructor constructor) {
                    AbstractView rvalue = null;
                    try {
                        rvalue = (AbstractView) constructor.newInstance(GearApplication.this);
                        Logger.getLogger(Cache.class.getCanonicalName())
                                .log(Level.INFO, "VIEW: {0} instance created.", rvalue.getClass().getName());
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        Logger.getLogger(GearApplication.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return rvalue;
                }

                @Override
                protected void afterNewInstance(String key, AbstractView data) {
                    this.put(key, data);
                }
            };
        }
    }

    public void mountResources() {
        String classesPath = this.getServletContext().getRealPath("/");
        // img のパスをurlにマッピングする
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
                String filePath = "/" + PropertyName + ".properties";
                Properties prop = new Properties();
                prop.load(this.getClass().getResourceAsStream(filePath));
                this.environments.put(PropertyName, prop);

            } catch (FileNotFoundException ex) {
                Logger.getLogger(GearApplication.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (IOException ex) {
                Logger.getLogger(GearApplication.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
        return this.environments.get(PropertyName);
    }

    @Override
    public Jdbc getJdbc() {
        if (this.jdbc == null) {

            Properties tinygear = this.getProperties("tiny.gear");

            this.jdbc = new Jdbc();
            this.jdbc.setDriver(tinygear.getProperty("tiny.gear.jdbc.driver"));
            this.jdbc.setUrl(tinygear.getProperty("tiny.gear.jdbc.url"));
            this.jdbc.setUser(tinygear.getProperty("tiny.gear.jdbc.user"));
            this.jdbc.setPassword(tinygear.getProperty("tiny.gear.jdbc.password"));
        }
        return this.jdbc;
    }

    @Override
    public RuntimeConfigurationType getConfigurationType() {
        /* "WICKET AJAX DEBUG" Link を表示させる場合は、
       * Application.DEVELOPMENT を返すこと。
       * Application.DEPLOYMENT を返すことで非表示にする
         */
        return RuntimeConfigurationType.DEVELOPMENT;
        //return Application.DEVELOPMENT;
    }

    public Table getCachedTable(String tableClassName) {
        Table rvalue = null;
        if (this.tableCache.containsKey(tableClassName)) {
            rvalue = this.tableCache.get(tableClassName);
            if (rvalue == null) {
                if (tableClassName.contains(GroovyTableBuilder.CUSTOM_TABLE_PACKAGE)) {
                    GroovyTableBuilder gtb = new GroovyTableBuilder(this);
                    String sqlName = GroovyTableBuilder.toSQLName(tableClassName);
                    rvalue = gtb.createTable(sqlName);
                }
            }
        } else {
            try {
                Class<? extends Table> cls = (Class<? extends Table>) Class.forName(tableClassName);
                rvalue = this.getCachedTable(cls);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(GearApplication.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return rvalue;
    }

    public Table getCachedTable(Class<? extends Table> tableClass) {
        Table rvalue = this.tableCache.get(tableClass.getName());
        return rvalue;
    }

    public boolean isTableCached(String tableClassName) {
        boolean rvalue = this.tableCache.containsKey(tableClassName);
        if (rvalue) {
            rvalue = (this.tableCache.get(tableClassName) != null);
        }
        return rvalue;
    }

    public Table stackTableOnCach(Table table) {
        this.tableCache.put(table.getClass().getName(), table);
        Logger.getLogger(this.getName()).log(Level.INFO, "TABLE: {0} instance created.", table.getClass().getName());
        return table;
    }
    
    public void removeTableCach(String tableClassName){
        this.tableCache.remove(tableClassName);
    }
    
    public void clearViewCach(){
        this.viewCache.clear();
    }

    public AbstractView getCachedView(String viewClassName) {
        AbstractView rvalue = null;
        try {
            Class<? extends AbstractView> cls = (Class<? extends AbstractView>) Class.forName(viewClassName);
            rvalue = this.getCachedView(cls);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GearApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rvalue;
    }

    public AbstractView getCachedView(Class<? extends AbstractView> viewClass) {
        AbstractView rvalue = this.viewCache.get(viewClass.getName());
        return rvalue;
    }

    public AbstractScene getCachedAbstractScene(String sceneClassName) {
        AbstractScene scene = null;
        if (this.sceneCach.containsKey(sceneClassName)) {
            scene = this.sceneCach.get(sceneClassName);
            //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Scene Cache Hit :{0}", sceneClassName);
        } else {
            try {
                SceneTable sceneTable = (SceneTable) this.getCachedTable(SceneTable.class);
                scene = sceneTable.createScene(this, sceneClassName);
                this.sceneCach.put(sceneClassName, scene);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "SCENE: {0} Created.", sceneClassName);
            } catch (IllegalArgumentException | SecurityException ex) {
                Logger.getLogger(GearApplication.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (scene == null) {
            this.sceneCach.remove(sceneClassName);
        }
        return scene;
    }

    public String getSystemVariable(String key, String defaultValue) {
        String rvalue = defaultValue;
        try {
            if (this.systemVariables.getCount(this.systemVariables.Key.sameValueOf(key)) < 1) {
                this.systemVariables.merge(
                        this.systemVariables.Key.setValue(key),
                        this.systemVariables.Value.setValue(defaultValue)
                );
            } else {
                try (ResultSet rs = this.systemVariables.select(this.systemVariables.Key.sameValueOf(key))) {
                    if (rs.next()) {
                        rvalue = this.systemVariables.Value.of(rs);
                        rs.close();
                    }
                }
            }
        } catch (TinyDatabaseException | SQLException ex) {
            Logger.getLogger(GearApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rvalue;
    }
}
