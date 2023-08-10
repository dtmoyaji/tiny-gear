/*
 * Copyright 2023 bythe.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tiny.gear.scenes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.tiny.datawrapper.Table;
import org.tiny.gear.GearApplication;

/**
 * シーンに含まれるビューの抽象クラス
 *
 * @author dtmoyaji
 */
public abstract class AbstractView extends Panel{

    public static final long serialVersionUID = -1L;

    private Label title;
    
    protected GearApplication app;
    
    private HashMap<String, Class<? extends Table>> tableClasses = new HashMap<>();
    private Class<? extends Table> prmaryTableClass = null;

    public AbstractView(GearApplication app) {
        super("scenePanel");
        
        TablesForAbstractView tfav = (TablesForAbstractView) this.getTable(TablesForAbstractView.class);
        
        try (ResultSet tableref = tfav.select(tfav.ViewClassName.sameValueOf(this.getClass().getName()))) {
            if(tableref!=null){
                this.prmaryTableClass = null;
                while(tableref.next()){
                    String tableName = tfav.TableClassName.of(tableref);
                    Class cls = Class.forName(tableName);
                    this.getTable(cls);
                    if(this.prmaryTableClass==null){
                        this.prmaryTableClass = cls;
                    }
                }
            }
        } catch (SQLException 
                | ClassNotFoundException 
                | SecurityException 
                | IllegalArgumentException ex) {
            Logger.getLogger(AbstractView.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public Table getPrimaryTargetTable(){
        return this.getTable(this.prmaryTableClass);
    }
    
    public Table getTable(Class<? extends Table> tableClass){
        this.tableClasses.put(tableClass.getCanonicalName(), tableClass);
        Table rvalue = this.getGearApplication().getCachedTable(tableClass);
        return rvalue;
    }
    
    public HashMap<String, Class<? extends Table>> getTables(){
        return this.tableClasses;
    }

    public abstract String getTitle();
    
    public void redraw(){
        this.removeAll();
        
        this.app = app;
        this.title = new Label("panelTitle", Model.of(this.getTitle()));
        this.add(this.title);
    }
    
    public GearApplication getGearApplication(){
        return (GearApplication) this.getApplication();
    }
    
}
