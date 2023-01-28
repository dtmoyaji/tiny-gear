package org.tiny.gear;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.tiny.gear.panels.UserInfoView;
import org.tiny.wicket.SamlMainPage;

public class Index extends SamlMainPage {

    private static final long serialVersionUID = 1L;

    private final UserInfoView userInfoView;
    
    public Index(final PageParameters parameters) {
        super(parameters);

        this.userInfoView = new UserInfoView("userInfoView");
        this.add(this.userInfoView);

        if (this.signControlPanel.getAuth() != null) {
            this.userInfoView.show(this.signControlPanel.getAuth());
        }
        
        
    }

    @Override
    public String getUserAccountKey() {
        return "displayname";
    }
}
