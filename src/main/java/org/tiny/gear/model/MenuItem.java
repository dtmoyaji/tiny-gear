package org.tiny.gear.model;

import java.io.Serializable;
import java.util.HashMap;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.tiny.gear.IRoleChecker;
import org.tiny.gear.RoleController;
import org.tiny.gear.scenes.AbstractScene;
import org.tiny.gear.scenes.AbstractView;

/**
 *
 * @author dtmoyaji
 */
public class MenuItem implements Serializable, IRoleChecker {

    public static final long serialVersionUID = -1L;

    private String text;

    private Roles allowed;

    private Class<? extends AbstractScene> scene;
    private Class<? extends AbstractView> view;
    private HashMap<String, String> arguments;

    public MenuItem(String text, Roles allowed) {
        this.text = text;
        this.allowed = allowed;
    }

    public MenuItem(String text, Class<? extends AbstractScene> scene, Roles allowed, Class<? extends AbstractView> view) {
        this.text = text;
        this.scene = scene;
        this.view = view;
        this.allowed = allowed;
    }
    
    public boolean isMatchedScene(Class<? extends AbstractScene> scene) {
        return this.scene.getName().equals(scene.getName());
    }

    public boolean isMatchedMainPanel(Class<? extends AbstractView> panel) {
        if (this.view == null) {
            return false;
        }
        return this.view.getName().equals(panel.getName());
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    public String mapUrl(Class scene, Class mainPanel) {
        String urlTemplate = "?scene=%s&view=%s";
        urlTemplate = String.format(
                urlTemplate,
                scene.getName(),
                mainPanel.getName()
        );
        return urlTemplate;
    }

    /**
     * @return the allowed
     */
    public Roles getAllowed() {
        return allowed;
    }

    /**
     * @param allowed the allowed to set
     */
    public void setAllowed(Roles allowed) {
        this.allowed = allowed;
    }

    @Override
    public boolean isAllowed(Roles roles) {
        return RoleController.isRolesMatched(this.allowed, roles);
    }
    
    public String getViewClassName(){
        return this.view.getName();
    }

    public HashMap<String, String> getArguments() {
        if(arguments == null){
            this.arguments = new HashMap<>();
        }
        return arguments;
    }

}
