package org.tiny.gear.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.tiny.wicket.onelogin.SamlAuthInfo;
import org.tiny.wicket.onelogin.SamlSession;

/**
 *
 * @author bythe
 */
public class UserInfoView extends AbstractView {

    private final Label samlNameId;

    private HashMap<String, List<String>> userAttributes;
    private ListView<String> KeySet;

    public UserInfoView() {

        super();

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
