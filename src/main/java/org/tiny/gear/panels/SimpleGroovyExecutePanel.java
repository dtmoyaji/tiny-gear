/*
 * Copyright 2023 MURAKAMI Takahiro <daianji@gmail.com>.
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

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.Model;

/**
 *
 * @author MURAKAMI Takahiro <daianji@gmail.com>
 */
public class SimpleGroovyExecutePanel extends AbstractPanel {

    private Form editor;

    private TextArea code;

    private AjaxButton btnRun;

    public SimpleGroovyExecutePanel(String id) {
        super(id);

        this.editor = new Form("editor");
        this.add(this.editor);

        this.code = new TextArea("code", Model.of(""));
        this.editor.add(this.code);

        this.btnRun = new AjaxButton("btnRun", Model.of("実行")) {

            @Override
            public void onSubmit(AjaxRequestTarget target) {
                String script = (String) code.getModelObject();
                if (script != null) {
                    if (script.trim().length() > 0) {
                        Logger.getLogger(btnRun.getClass().getName()).log(Level.INFO, script);

                        Binding binding = new Binding();
                        binding.setVariable("myApplication", SimpleGroovyExecutePanel.this.getApplication());
                        binding.setVariable("mySession", SimpleGroovyExecutePanel.this.getSession());
                        binding.setVariable("myPanel", SimpleGroovyExecutePanel.this);
                        GroovyShell shell = new GroovyShell(binding);
                        String rvalue = String.valueOf(shell.evaluate(script));
                        Logger.getLogger(btnRun.getClass().getName()).log(Level.INFO, rvalue);
                    }
                    target.add(editor);
                }
            }

        };
        this.editor.add(this.btnRun);

    }

    @Override
    public String getTitle() {
        return "Groovy";
    }

}
