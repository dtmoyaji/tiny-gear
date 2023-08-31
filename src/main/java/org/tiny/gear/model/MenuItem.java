package org.tiny.gear.model;

import java.io.Serializable;
import java.util.HashMap;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.tiny.gear.IRoleChecker;
import org.tiny.gear.RoleController;
import org.tiny.gear.scenes.AbstractScene;
import org.tiny.gear.scenes.AbstractView;

/**
 * @author dtmoyaji
 */
public class MenuItem implements Serializable, IRoleChecker {

    public static final long serialVersionUID = -1L;

    private String text = "";

    private Roles roles;

    private Class<? extends AbstractScene> scene;
    private Class<? extends AbstractView> view;
    private HashMap<String, String> arguments;

    private boolean primary = false;

    public MenuItem() {
        this.arguments = new HashMap();
    }

    public MenuItem setPrimary(boolean primary) {
        this.primary = primary;
        return this;
    }

    public boolean isPrimary() {
        return this.primary;
    }

    public MenuItem setText(String text) {
        this.text = text;
        return this;
    }

    public String getText() {
        return this.text;
    }

    public MenuItem setRoles(Roles roles) {
        this.roles = roles;
        return this;
    }

    public Roles getRoles() {
        return this.roles;
    }

    @Override
    public boolean isAuthenticated(Roles roles) {
        return RoleController.isRolesMatched(this.roles, roles);
    }

    public MenuItem setScene(Class<? extends AbstractScene> scene) {
        this.scene = scene;
        return this;
    }

    public Class<? extends AbstractScene> getScene() {
        return this.scene;
    }

    public boolean isMatchedScene(Class<? extends AbstractScene> scene) {
        return this.scene.getName().equals(scene.getName());
    }

    public MenuItem setView(Class<? extends AbstractView> view) {
        this.view = view;
        return this;
    }

    public Class<? extends AbstractView> getView() {
        return this.view;
    }

    public boolean isMatchedView(Class<? extends AbstractView> view) {
        if (this.view == null) {
            return false;
        }
        return this.view.getName().equals(view.getName());
    }

    public MenuItem setArguments(HashMap<String, String> args) {
        this.arguments = args;
        return this;
    }

    public HashMap<String, String> getArguments() {
        if (arguments == null) {
            this.arguments = new HashMap<>();
        }
        return arguments;
    }

}
