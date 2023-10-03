
package org.tiny.gear.panels.crud;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.tiny.datawrapper.Column;
import org.tiny.datawrapper.ConditionForOrder;

/**
 *
 * @author dtmoyaji
 */
public class SortingControl extends Panel{
    
    private Column column; // 操作対象のカラム
    private ConditionForOrder order =null; //オーダーの格納先
    
    private Form form;
    private AjaxLink sortSwitch;
    

    public SortingControl(String id, Column column) {
        super(id);
        
        this.column = column;
        
        this.form = new Form("sortingControlForm");
        this.add(this.form);
        
        this.sortSwitch = new AjaxLink("sortSwitch", Model.of("")){
            @Override
            public void onClick(AjaxRequestTarget target) {
                SortingControl.this.onClick(target);
            }
        };
        this.sortSwitch.setOutputMarkupId(true);
        this.form.add(this.sortSwitch);
    }
    
    public void onClick(AjaxRequestTarget target){
        if(this.order==null){
            this.order = new ConditionForOrder(this.column, ConditionForOrder.ORDER_ASC);
            this.sortSwitch.add(AttributeModifier.replace("sort", "asc"));
        }else{
            if(this.order.getOperation()==ConditionForOrder.ORDER_ASC){
                this.order = new ConditionForOrder(this.column, ConditionForOrder.ORDER_DESC);
                this.sortSwitch.add(AttributeModifier.replace("sort", "desc"));
            }else{
                this.order = null;
                this.sortSwitch.add(AttributeModifier.replace("sort", "none"));
            }
        }
        target.add(this.sortSwitch);
    }
    
    public ConditionForOrder getOrder(){
        return this.order;
    }

}
