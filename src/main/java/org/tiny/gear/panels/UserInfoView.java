package org.tiny.gear.panels;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.tiny.wicket.onelogin.SamlAuthInfo;
import org.tiny.wicket.onelogin.SamlSession;

/**
 *
 * @author bythe
 */
public class UserInfoView extends Panel {

    private final Label samlNameId;
    
    private final Label userInfoLabel;

    public UserInfoView(String id) {
        super(id);
        
        this.samlNameId = new Label("samlNameId", Model.of(""));
        this.add(this.samlNameId);

        this.userInfoLabel = new Label("userInfoLabel", Model.of(""));
        this.add(this.userInfoLabel);

        SamlSession ssession = (SamlSession) this.getSession();
        if (ssession.getSamlAuthInfo() != null) {
            
            SamlAuthInfo ainfo = ssession.getSamlAuthInfo();
            this.samlNameId.setDefaultModelObject(ainfo.getNameId());
            this.userInfoLabel.setDefaultModelObject(ssession.getSamlAuthInfo().toString());
            
        }
        
    }

    public void show(SamlAuthInfo userInfo) {
        System.out.println(userInfo.toString());
        this.userInfoLabel.setDefaultModelObject(userInfo.toString());
    }

}
