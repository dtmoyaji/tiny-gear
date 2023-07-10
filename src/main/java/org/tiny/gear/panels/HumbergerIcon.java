/*
 * Copyright 2023 bythe.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tiny.gear.panels;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;

/**
 *
 * @author bythe
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
                        "const nav = document.getElementById('"+
                        HumbergerIcon.this.targetElementId+"');\n"
                        + "nav.setAttribute(\"opened\", nav.getAttribute(\"opened\") ? '' : \"true\");");
            }
        };

        this.humbergerForm = new Form("humbergerForm");
        this.humbergerForm.add(this.humberger);

        this.add(this.humbergerForm);
    }

}
