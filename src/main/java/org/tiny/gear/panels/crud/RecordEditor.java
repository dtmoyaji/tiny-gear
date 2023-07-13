package org.tiny.gear.panels.crud;

import java.sql.ResultSet;
import java.util.ArrayList;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.tiny.datawrapper.Column;
import org.tiny.datawrapper.Table;

/**
 * 
 * @author bythe
 */
public abstract class RecordEditor extends Panel{
    
    public static int MODE_NEW = 0;
    
    public static int MODE_UPDATE = 1;
    
    private Table targetTable;
    
    private ListView<Column> controls;
    private ArrayList<DataControl> dataControls=new ArrayList<>();
    
    public RecordEditor(String id) {
        super(id);
    }
    
    public void buildForm(Table targetTable){
        
        this.targetTable = targetTable;
        this.dataControls.clear();
        
        this.controls = new ListView<Column>("controls", this.targetTable){
            
            @Override
            protected void populateItem(ListItem<Column> item) {
                Column col = item.getModelObject();
                DataControl dcol = new DataControl("dataControl", col);
                item.add(dcol);
                RecordEditor.this.dataControls.add(dcol);
            }
            
        };
        this.add(this.controls);
    }
    
    public void stackData(ResultSet rs){
        for(Column col: this.targetTable){
            col.setValue(col.of(rs));
        }
    }
    
    /**
     * フォームを生成する前の処理を定義する。
     */
    public abstract void beforeFormBuild();
    
    /**
     * フォームを生成した後の処理を定義する。
     */
    public abstract void afterFormBuild();
}
