package org.tiny.gear.scenes.setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.tiny.gear.GearApplication;
import org.tiny.gear.scenes.AbstractView;
import org.tiny.wicket.onelogin.SamlAuthInfo;
import org.tiny.wicket.onelogin.SamlSession;

/**
 *
 * @author dtmoyaji
 */
public class UserInfoView extends AbstractView {

    public static final long serialVersionUID = -1L;

    private Label samlNameId;

    private HashMap<String, List<String>> userAttributes;
    private ListView<String> KeySet;

    public UserInfoView(GearApplication app) {

        super(app);
    }
    
    @Override
    public void redraw(){
        super.redraw();

        this.samlNameId = new Label("samlNameId", Model.of(""));
        this.add(this.samlNameId);

        SamlSession ssession = (SamlSession) this.getSession();

        ArrayList<String> kset = new ArrayList<>();
        if (ssession.getSamlAuthInfo() != null) {

            SamlAuthInfo ainfo = ssession.getSamlAuthInfo();
            this.samlNameId.setDefaultModelObject(ainfo.getNameId());

            this.userAttributes = (HashMap<String, List<String>>) ainfo.getAttributes();
            kset = new ArrayList(this.userAttributes.keySet());
        }

        this.KeySet = new ListView<>("userAttributes", kset) {
            public static final long serialVersionUID = -1L;

            @Override
            protected void populateItem(ListItem<String> item) {
                String key = item.getModelObject();
                item.add(new Label("userAttribute", Model.of(key)));
                ArrayList<String> attrs = (ArrayList<String>) userAttributes.get(key);
                String value = "";
                for (String v : attrs) {
                    value += ", " + v;
                }
                value = value.substring(2);
                item.add(new Label("userAttributeValue", Model.of(value)));
            }
        };
        this.add(this.KeySet);

    }

    @Override
    public String getTitle() {
        return "ユーザー情報";
    }

}
