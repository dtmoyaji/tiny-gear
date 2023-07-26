package org.tiny.gear.panels.crud;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.tiny.datawrapper.Table;

/**
 * データテーブル情報を格納するクラス
 * @author bythe
 */
public abstract class DataTableInfoPanel extends Panel {
    
    protected Table targetTable;

    public DataTableInfoPanel(String id) {
        super(id);
    }
    
    public DataTableInfoPanel(String id, Model model){
        super(id, model);
    }
    
    /**
     * テーブルをクローンして格納することで、外部のオブジェクト操作の影響を除外する。
     * @param target 
     */
    public void setTable(Table target){

        try {
            Class cls = target.getClass();
            Constructor constructor = cls.getDeclaredConstructor();
            constructor.setAccessible(true);
            Table clone = (Table) constructor.newInstance();

            this.targetTable = clone;
            this.beforeConstructView(this.targetTable);
        } catch (SecurityException | IllegalArgumentException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException  ex) {
            Logger.getLogger(DataTableInfoPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Table getTable(){
        return this.targetTable;
    }
    
    public abstract void beforeConstructView(Table myTable);
    
}
