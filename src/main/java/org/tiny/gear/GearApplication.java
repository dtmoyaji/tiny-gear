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
import org.tiny.gear.model.ObjectCacheInfo;
import org.tiny.gear.model.SystemVariables;
import org.tiny.gear.scenes.AbstractScene;
import org.tiny.gear.scenes.AbstractView;
import org.tiny.gear.scenes.SceneTable;
import org.tiny.gear.scenes.develop.DevelopScene;
import org.tiny.gear.scenes.primary.PrimaryScene;
import org.tiny.gear.scenes.purchase.PurchaseScene;
import org.tiny.gear.scenes.setting.SettingScene;
import org.tiny.gear.scenes.webdb.CustomTableManagementScene;
import org.tiny.wicket.SamlWicketApplication;
import wicket.util.file.File;

/**
 * @see org.tiny.Start#main(String[])
 */
public class GearApplication extends SamlWicketApplication implements IJdbcSupplier, Serializable {

    public static final long serialVersionUID = -1L;

    private Jdbc jdbc;

    private ObjectCacheInfo objectCachInfo;

    private SystemVariables systemVariables;
    private HashMap<String, String> systemVariableCache;

    private HashMap<String, Properties> environments;

    private HashMap<String, AbstractScene> sceneCach = new HashMap<>();
    private SceneTable sceneTable;

    private Cache<Class> classCache;

    private Cache<Table> tableCache;

    private Cache<AbstractView> viewCache;

    private GroovyExecutor groovyExecutor;

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

    public void clearCache() {

        this.classCache.removeFromServer();
        this.classCache = null;

        this.tableCache.removeFromServer();
        this.tableCache = null;

        this.systemVariableCache = null;

        this.viewCache.removeFromServer();
        this.viewCache = null;
        //this.sceneCach = null;

        this.buildCache();
    }

    public void buildCache() {

        if (this.classCache == null) {
            this.classCache = new Cache<>() {
                @Override
                protected Class initializeObject(String key, Class[] constParam) {
                    Class cls = GearApplication.this.getCachedClass(key);
                    Logger.getLogger(Cache.class.getCanonicalName())
                            .log(Level.INFO, "CLASS: {0} created.", cls.getName());
                    return cls;
                }

                @Override
                protected Class onNewInstance(Constructor constructor) { // スタブ
                    return null;
                }

            };
            this.classCache.sync(this, ObjectCacheInfo.TYPE_CLASS);
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Class cache created.");
        }

        if (this.tableCache == null) {
            this.tableCache = new Cache<>() {
                @Override
                protected Table initializeObject(String key, Class[] constParam) {
                    Table data = null;
                    try {
                        data = GearApplication.this.getCachedTable(key);
                        Logger.getLogger(Cache.class.getCanonicalName())
                                .log(Level.INFO, "TABLE: {0} instance created.", data.getClass().getName());
                    } catch (SecurityException ex) {
                        Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalArgumentException ex) {
                        Logger.getLogger(GearApplication.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return data;
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
            this.tableCache.sync(this, ObjectCacheInfo.TYPE_TALBE);
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Table cache created.");
        }

        if (this.systemVariableCache == null) {

            this.systemVariables = new SystemVariables();
            this.systemVariables.alterOrCreateTable(this.getJdbc());

            this.systemVariableCache = new HashMap<>();
            try (ResultSet rs = this.systemVariables.select()) {
                while (rs.next()) {
                    String key = this.systemVariables.Key.of(rs);
                    this.systemVariableCache.put(
                            key, this.systemVariables.Value.of(rs)
                    );
                    Logger.getLogger(this.systemVariableCache.getClass().getName())
                            .log(Level.INFO, "VARIABLE: {0} cached.", key);
                }
            } catch (SQLException ex) {
                Logger.getLogger(GearApplication.class.getName()).log(Level.SEVERE, null, ex);
            }
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "System variable cache created.");
        }

        if (this.viewCache == null) {
            this.viewCache = new Cache<>() {
                @Override
                protected AbstractView onNewInstance(Constructor constructor) {
                    AbstractView rvalue = null;
                    try {
                        rvalue = (AbstractView) constructor.newInstance(GearApplication.this);
                        if (rvalue != null) {
                            Logger.getLogger(Cache.class.getCanonicalName())
                                    .log(Level.INFO, "VIEW: {0} instance created.", rvalue.getClass().getName());
                        } else {
                            Logger.getLogger(Cache.class.getCanonicalName())
                                    .log(Level.INFO, "ERROR VIEW: {0} instance can't create.", constructor.getDeclaringClass().getName());
                        }
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        Logger.getLogger(GearApplication.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return rvalue;
                }

                @Override
                protected AbstractView initializeObject(String key, Class[] constParam) {
                    AbstractView data = null;
                    try {
                        Class cls = GearApplication.this.getCachedClass(key);
                        Constructor constructor = cls.getConstructor(constParam);
                        data = (AbstractView) this.onNewInstance(constructor);
                        //this.put(key, data);
                    } catch (NoSuchMethodException | SecurityException ex) {
                        Logger.getLogger(Cache.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    return data;
                }

            };
            this.viewCache.sync(this, ObjectCacheInfo.TYPE_VIEW, GearApplication.class);
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "View cache created.");

        }
        this.groovyExecutor = new GroovyExecutor(this);
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

    public Table getCachedCustomTable(String tableClassName) {
        Table rvalue = null;
        if (CustomTableBuilder.isCustomTable(tableClassName)) {
            if (this.tableCache.containsKey(tableClassName)) {
                rvalue = this.tableCache.get(tableClassName);
            }
            if (rvalue == null) {
                CustomTableBuilder gtb = new CustomTableBuilder(this);
                String sqlName = CustomTableBuilder.toSQLName(tableClassName);
                rvalue = gtb.createTable(sqlName);
                this.tableCache.put(tableClassName, rvalue);
            }
        }
        return rvalue;
    }

    public Table getCachedSystemTable(String tableClassName) {
        Table rvalue = null;
        if (this.tableCache.containsKey(tableClassName)) {
            rvalue = this.tableCache.get(tableClassName);
        } else {
            try {
                Class<? extends Table> cls = (Class<? extends Table>) this.getCachedClass(tableClassName);
                rvalue = cls.getConstructor().newInstance();
                rvalue.alterOrCreateTable(this.getJdbc());
                this.tableCache.put(tableClassName, rvalue);
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(GearApplication.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
        return rvalue;
    }

    public Class getCachedClass(String className) {
        Class rvalue = null;
        if (this.classCache.containsKey(className)) {
            rvalue = this.classCache.get(className);
        } else {
            try {
                rvalue = Class.forName(className);
                this.classCache.put(className, rvalue);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(GearApplication.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return rvalue;
    }

    public Table getCachedTable(String tableClassName) {
        Table rvalue = this.getCachedCustomTable(tableClassName);
        if (rvalue == null) {
            rvalue = this.getCachedSystemTable(tableClassName);
        }
        return rvalue;
    }

    public Table getCachedTable(Class<? extends Table> tableClass) {
        if (this.tableCache == null) {
            return null;
        }
        Table rvalue = this.getCachedTable(tableClass.getName());
        return rvalue;
    }

    public boolean isTableCached(String tableClassName) {
        if (this.tableCache == null) {
            return false;
        }
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

    public void removeTableCach(String tableClassName) {
        this.tableCache.remove(tableClassName);
    }

    public void clearViewCach() {
        this.viewCache.clear();
    }

    public AbstractView getCachedView(String viewClassName) {
        AbstractView rvalue = null;
        Class<? extends AbstractView> cls = 
                (Class<? extends AbstractView>) this.getCachedClass(viewClassName);
        rvalue = this.getCachedView(cls);
        return rvalue;
    }

    public AbstractView getCachedView(Class<? extends AbstractView> viewClass) {
        AbstractView rvalue = this.viewCache.get(viewClass.getName());
        if (rvalue == null) {
            try {
                rvalue = viewClass.getConstructor(GearApplication.class)
                        .newInstance(this);
                this.viewCache.put(viewClass.getName(), rvalue);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "VIEW : {0} instance created.", viewClass.getName());
            } catch (NoSuchMethodException
                    | SecurityException
                    | InstantiationException
                    | IllegalAccessException
                    | IllegalArgumentException
                    | InvocationTargetException ex) {
                Logger.getLogger(GearApplication.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return rvalue;

    }

    protected void initScenes() {
        this.sceneTable = (SceneTable) this.getCachedTable(SceneTable.class);

        this.sceneTable.registScene(this, PrimaryScene.class,
                0, RoleController.ROLE_ALL);

        this.sceneTable.registScene(this, SettingScene.class,
                1, RoleController.ROLE_USER);

        this.sceneTable.registScene(this, DevelopScene.class,
                2, RoleController.ROLE_DEVELOPER);

        this.sceneTable.registScene(this, CustomTableManagementScene.class,
                3, RoleController.ROLE_DEVELOPER);

        this.sceneTable.registScene(this, PurchaseScene.class,
                4, RoleController.ROLE_USER);
    }

    public AbstractScene getCachedScene(String sceneClassName) {
        AbstractScene scene = null;
        if (this.sceneCach.containsKey(sceneClassName)) {
            scene = this.sceneCach.get(sceneClassName);
            //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Scene Cache Hit :{0}", sceneClassName);

        } else {
            try {
                SceneTable sceneTable = (SceneTable) this.getCachedTable(SceneTable.class
                );
                scene = this.sceneTable.createScene(this, sceneClassName);
                this.sceneCach.put(sceneClassName, scene);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "SCENE: {0} Created.", sceneClassName);

            } catch (IllegalArgumentException | SecurityException ex) {
                Logger.getLogger(GearApplication.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (scene == null) {
            this.sceneCach.remove(sceneClassName);
        }
        return scene;
    }

    public String getSystemVariable(String key, String defaultValue) {
        String rvalue = defaultValue;
        if (this.systemVariableCache.containsKey(key)) {
            rvalue = this.systemVariableCache.get(key);
            if (rvalue == null) {
                rvalue = defaultValue;
            }
        } else {
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
                Logger.getLogger(GearApplication.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.systemVariableCache.put(key, rvalue);
        return rvalue;
    }

    public GroovyExecutor getGroovyExecutor() {
        return this.groovyExecutor;
    }

}
