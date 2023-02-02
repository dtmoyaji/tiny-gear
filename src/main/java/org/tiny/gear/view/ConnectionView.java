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
package org.tiny.gear.view;

import java.util.Properties;
import org.tiny.gear.GearApplication;
import org.tiny.gear.panels.PropertyPanel;

/**
 *
 * @author bythe
 */
public class ConnectionView extends AbstractView{
    
    private PropertyPanel jdbcPanel;
    private PropertyPanel samlPanel;
    
    public ConnectionView(){
        super();
        
        this.jdbcPanel = new PropertyPanel("jdbcProperties"){
            
            @Override
            public String getTitle(){
                return "JDBC接続";
            }
            
            @Override
            public Properties getProperties(){
                GearApplication app = (GearApplication)this.getApplication();
                Properties props = app.getProperties("tiny.gear");
                return props;
            }
            
            @Override
            public String getPrefix(){
                return "tiny.gear.jdbc";
            }
        };
        this.add(this.jdbcPanel);
        
        this.samlPanel = new PropertyPanel("samlProperties"){
            
            @Override
            public String getTitle(){
                return "SAML設定";
            }
            
            @Override
            public Properties getProperties(){
                GearApplication app = (GearApplication)this.getApplication();
                Properties props = app.getProperties("onelogin.saml");
                return props;
            }
            
            @Override
            public String getPrefix(){
                return "onelogin.saml2";
            }
        };
        this.add(this.samlPanel);
        
    }
    
    @Override
    public String getTitle() {
        return "各種接続設定";
    }
    
}
