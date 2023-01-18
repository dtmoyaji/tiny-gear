package org.tiny.gear;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.tiny.wicket.SamlMainPage;

public class Index extends SamlMainPage {
    
	private static final long serialVersionUID = 1L;

	public Index(final PageParameters parameters) {
		super(parameters);

	}

    @Override
    public String getUserAccountKey() {
        return "displayname";
    }
}
