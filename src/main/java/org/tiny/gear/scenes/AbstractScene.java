package org.tiny.gear.scenes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.tiny.gear.GearApplication;
import org.tiny.gear.IRoleChecker;
import org.tiny.gear.RoleController;
import org.tiny.gear.model.MenuItem;

/**
 * ブラウザに表示するコンテンツの情報を格納するクラス。 １つの作業単位をシーンにまとめて格納する。 シーンには、複数のビューとメニューが格納される。
 * このクラスを継承してシーンを作成することを推奨する。
 *
 * @author dtmoyaji
 */
public abstract class AbstractScene implements Serializable, IRoleChecker {

    public static final long serialVersionUID = -1L;

    private int ordinal = -1;

    private String sceneName;

    private String sceneKey;

    public static String DEFAULT_VIEW = "default_view";

    private ArrayList<MenuItem> menus;

    private HashMap<String, Class<? extends AbstractView>> viewClasses;

    private Roles roles;

    private String defaultPanelName;

    protected GearApplication application;

    public AbstractScene(Roles allowed, GearApplication app) {
        this.setInitializer(allowed, app);
    }

    public AbstractScene() {
    }

    public void setInitializer(Roles allowed, GearApplication app) {
        this.roles = allowed;
        this.application = app;

        this.menus = new ArrayList<>();
        this.viewClasses = new HashMap<>();

        this.defineMenu();

    }

    /**
     * 派生クラスはメニューをここで定義する。 putMenuを呼び出して、メニューの表示テキストと、格納するビューを登録すること。
     */
    public abstract void defineMenu();

    @Override
    public boolean isAuthenticated(Roles role) {
        return RoleController.isRolesMatched(this.roles, role);
    }

    public void setOrdinal(int order) {
        this.ordinal = order;
    }

    public int getOrdinal() {
        return this.ordinal;
    }

    public Roles getRoles() {
        return this.roles;
    }

    public abstract String getSceneName();

    public String getSceneKey() {
        return this.getClass().getName();
    }

    public boolean isSceneKeyMatch(String key) {
        return key.equals(this.getSceneKey());
    }

    /**
     * @return the menus
     */
    public ArrayList<MenuItem> getSubmenuItems() {
        return menus;
    }

    /**
     * @param menus the menus to set
     */
    public void setMenus(ArrayList<MenuItem> menus) {
        this.menus = menus;
    }

    /**
     * @return the viewClasses
     */
    public HashMap<String, Class<? extends AbstractView>> getViewClasses() {
        return viewClasses;
    }

    public Class<? extends AbstractView> getViewClass(String key) {
        return this.viewClasses.get(key);
    }

    /**
     * @param views the viewClasses to set
     */
    public void setViews(HashMap<String, Class<? extends AbstractView>> views) {
        this.viewClasses = views;
    }

    public boolean isPrimary() {
        return false;
    }

    public MenuItem createMenuItem(String caption, Class<? extends AbstractView> viewClass) {
        MenuItem rvalue = new MenuItem()
                .setText(caption)
                .setView(viewClass);
        this.getSubmenuItems().add(rvalue);
        if (viewClass != null) {
            this.getViewClasses().put(viewClass.getName(), viewClass);
        }

        return rvalue;
    }

    public MenuItem putMenu(String menuName, Class<? extends AbstractView> view, Roles roles, boolean primary) {
        MenuItem rvalue = new MenuItem().setText(menuName)
                .setScene(this.getClass())
                .setRoles(roles)
                .setView(view);
        try {
            this.getSubmenuItems().add(rvalue);
            this.getViewClasses().put(view.getName(), view);
            if (primary) {
                this.defaultPanelName = view.getCanonicalName();
            }
        } catch (SecurityException | IllegalArgumentException ex) {
            Logger.getLogger(AbstractScene.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rvalue;
    }

    public MenuItem putMenu(String menuName, Class<? extends AbstractView> view, Class[] refTables, Roles roles, boolean primary) {

        TablesForAbstractView tfav = new TablesForAbstractView();
        tfav.alterOrCreateTable(this.application.getJdbc());

        String viewFullName = view.getCanonicalName();

        for (Class table : refTables) {
            String tableFullName = table.getCanonicalName();
            tfav.clearValues();
            tfav.merge(tfav.ViewClassName.setValue(viewFullName),
                    tfav.TableClassName.setValue(tableFullName));
            this.getGearApplication().getCachedTable(table);
        }

        return this.putMenu(menuName, view, roles, primary);
    }

    public abstract Class getDefaultViewClass();

    public AbstractView createDefaultView() {
        AbstractView rvalue = this.getGearApplication()
                .getCachedView(this.getDefaultViewClass());
        return rvalue;
    }

    /**
     * ユーザーが表示権限を持っているかどうかをチェックする。
     *
     * @param abstractView
     * @param roles
     * @return
     */
    public boolean isAuthenticated(AbstractView abstractView, Roles roles) {
        boolean rvalue = false;
        ArrayList<MenuItem> menuItems = this.getSubmenuItems();
        for (MenuItem menuItem : menuItems) {
            if (menuItem.getView().getName().equals(abstractView.getClass().getCanonicalName())) {
                if (!rvalue) {
                    rvalue = menuItem.isAuthenticated(roles);
                }
            }
        }
        return rvalue;
    }

    public GearApplication getGearApplication() {
        return this.application;
    }
}
