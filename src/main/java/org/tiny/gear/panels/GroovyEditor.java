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
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.Model;

/**
 *
 * @author MURAKAMI Takahiro <daianji@gmail.com>
 */
public class GroovyEditor extends AbstractPanel {

    public static final long serialVersionUID = -1L;

    private Form<String> editor;

    private TextArea<String> code;

    private AjaxButton btnRun;

    private Label result;

    public GroovyEditor(String id) {

        super(id);

        this.editor = new Form<>("editor");
        this.add(this.editor);

        this.code = new TextArea<>("code", Model.of("import org.tiny.gear.model.UserInfo\n\ndef jdbc = myApplication.getJdbc()\ndef uinfo = new UserInfo()\nuinfo.alterOrCreateTable(jdbc)\n\nreturn uinfo.toString()"));
        this.editor.add(this.code);

        this.btnRun = new AjaxButton("btnRun", Model.of("実行")) {
            public static final long serialVersionUID = -1L;

            @Override
            public void onSubmit(AjaxRequestTarget target) {
                String script = (String) code.getModelObject();
                if (script != null) {
                    if (script.trim().length() > 0) {
                        Logger.getLogger(btnRun.getClass().getName()).log(Level.INFO, script);

                        Binding binding = new Binding();
                        binding.setVariable("myApplication", GroovyEditor.this.getApplication());
                        binding.setVariable("mySession", GroovyEditor.this.getSession());
                        binding.setVariable("myPanel", GroovyEditor.this);
                        GroovyShell shell = new GroovyShell(binding);

                        try {
                            String rvalue = String.valueOf(shell.evaluate(script));
                            GroovyEditor.this.result.setDefaultModelObject(rvalue);
                            Logger.getLogger(btnRun.getClass().getName()).log(Level.INFO, rvalue);
                        } catch (Exception ex) {
                            String strException = ex.getMessage();

                            strException += ex.getStackTrace().toString();
                            GroovyEditor.this.result.setDefaultModelObject(strException);
                        }

                    }
                    target.add(editor);
                }
            }

        };
        this.editor.add(this.btnRun);

        this.result = new Label("lblResult", Model.of("よしなに。"));
        this.result.setEscapeModelStrings(false);
        this.editor.add(this.result);

    }

    @Override
    public String getTitle() {
        return "Groovy";
    }

}
