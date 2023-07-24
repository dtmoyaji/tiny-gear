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
package org.tiny.gear.webdb;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.tiny.datawrapper.IJdbcSupplier;
import org.tiny.gear.RoleController;
import org.tiny.gear.scenes.AbstractScene;
import org.tiny.gear.view.AbstractView;

/**
 *
 * @author bythe
 */
public class CustomTableManagementScene extends AbstractScene {

    public CustomTableManagementScene(Roles allowed, IJdbcSupplier supplier) {
        super(allowed, supplier);
        
    }
    
    @Override
    public AbstractView getDefaultPanel() {
        return new CustomTableEditView(this.supplier);
    }    
    
    @Override
    public String getSceneName(){
        return "WEB DB";
    }

    @Override
    public void defineMenu() {
        this.putMenu(
                "カスタムテーブル", CustomTableEditView.class,
                RoleController.getAllRoles(), true
        );

    }

}
