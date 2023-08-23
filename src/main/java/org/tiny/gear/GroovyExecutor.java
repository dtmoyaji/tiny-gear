package org.tiny.gear;

import groovy.lang.GroovyShell;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.panel.Panel;
import org.tiny.datawrapper.Table;
import org.tiny.gear.model.GroovyStack;

/**
 *
 * @author dtmoyaji
 */
public class GroovyExecutor implements Serializable {

    public static final long serialVersionUID = -1L;
    /**
     * Groovyスクリプト格納テーブル
     */
    private GroovyStack gstack;

    private Panel panel;

    private GearApplication application;

    private String script;

    private Object returnValue;
    
    private ArrayList<Component> updateComponents;

    public GroovyExecutor(GearApplication app) {
        this.application = app;
        this.gstack = (GroovyStack) this.application.getCachedTable(GroovyStack.class);
    }

    public boolean execute(String scriptName, Panel panel) {
        this.panel = panel;
        GroovyShell shell = new GroovyShell();
        this.updateComponents = new ArrayList<>();
        boolean rvalue = false;
        this.script = "";

        try (ResultSet rs = this.gstack.select(this.gstack.ScriptName.sameValueOf(scriptName))) {
            if (rs.next()) {
                this.buildScriptPrefix(shell, this.gstack.VariableClasses.of(rs));
                this.script = this.gstack.ScriptBody.of(rs);
                this.returnValue = shell.evaluate(this.script);
                rvalue = true;
                rs.close();
            } else {
                this.gstack.clearValues();
                this.gstack.merge(
                        this.gstack.ScriptName.setValue(scriptName),
                        this.gstack.ScriptBody.setValue("println \"Script is not defined.\"\n"),
                        this.gstack.VariableClasses.setValue("")
                );
            }
        } catch (SQLException ex) {
            Logger.getLogger(GroovyExecutor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rvalue;
    }

    public void buildScriptPrefix(GroovyShell shell, String classes) {
        shell.setVariable(
                "_GearApplication",
                this.application
        );
        shell.setVariable(
                this.getParameterName(this.panel.getClass()),
                this.panel
        );
        shell.setVariable("_UpdateComponent", this.updateComponents);
        if (classes == null) {
            return;
        }
        if (classes.length() < 1) {
            return;
        }
        String[] cls = classes.split(",");
        for (String target : cls) {
            if (this.isTableClass(target)) {
                String varName = this.getParameterName(target);
                Table table = this.application.getCachedTable(target);
                shell.setProperty(varName, table);
            }
        }
    }

    private boolean isTableClass(String classname) {
        try {
            Class cls = Class.forName(classname);
            return cls.isAssignableFrom(Table.class);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GroovyExecutor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private String getParameterName(Class cls) {
        String simpleName = cls.getSimpleName();
        return "_" + simpleName;
    }

    private String getParameterName(String clsName) {
        String rvalue = clsName;
        try {
            Class cls = Class.forName(clsName);
            rvalue = getParameterName(cls);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(GroovyExecutor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rvalue;
    }
    
    public ArrayList<Component> getUpdateComponents(){
        return this.updateComponents;
    }
}
