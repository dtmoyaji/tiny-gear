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
package org.tiny.gear.scenes.trader;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.tiny.gear.GearApplication;
import org.tiny.gear.RoleController;
import org.tiny.gear.scenes.AbstractScene;

/**
 *
 * @author dtmoyaji
 */
public class TraderEditScene extends AbstractScene{

    public TraderEditScene(Roles allowed, GearApplication application){
        super(allowed, application);
    }

    @Override
    public void defineMenu() {
        this.putMenu("仕入先編集",
                TraderEditView.class,
                RoleController.getUserRoles(),
                true);
    }

    @Override
    public Class getDefaultViewClass() {
        return TraderEditView.class;
    }

    @Override
    public String getSceneName() {
        return "取引先編集";
    }
    
}
