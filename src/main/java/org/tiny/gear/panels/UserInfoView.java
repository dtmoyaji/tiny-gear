package org.tiny.gear.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.tiny.wicket.onelogin.SamlAuthInfo;

/**
 *
 * @author bythe
 */
public class UserInfoView extends Panel{
    
    private final Label userInfoLabel;

    public UserInfoView(String id) {
        super(id);
        
        this.userInfoLabel = new Label("userInfoLabel",Model.of(""));
        this.add(this.userInfoLabel);
    }

    
    public void show(SamlAuthInfo userInfo){
        System.out.println(userInfo.toString());
        this.userInfoLabel.setDefaultModelObject(userInfo.toString());
    }
    
}
