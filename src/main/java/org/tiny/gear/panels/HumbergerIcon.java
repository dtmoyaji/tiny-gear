package org.tiny.gear.panels;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * @author dtmoyaji
 */
public class HumbergerIcon extends Panel {

    public static final long serialVersionUID = -1L;

    private Form humbergerForm;
    private AjaxButton humberger;

    private String targetElementId;

    public HumbergerIcon(String id, String targetElementId) {
        super(id);

        this.targetElementId = targetElementId;

        this.humberger = new AjaxButton("humbergerPanel") {
            public static final long serialVersionUID = -1L;

            @Override
            public void onSubmit(AjaxRequestTarget target) {
                target.appendJavaScript(
                        "const nav = document.getElementById('"
                        + HumbergerIcon.this.targetElementId
                        + "');\n"
                        + "nav.setAttribute(\"opened\", nav.getAttribute(\"opened\") ? '' : \"true\");"
                );
            }
        };

        this.humbergerForm = new Form("humbergerForm");
        this.humbergerForm.add(this.humberger);

        this.add(this.humbergerForm);
    }

    public String getTargetElementId() {
        return this.targetElementId;
    }

}
