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
package org.tiny.gear;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;

/**
 *
 * @author bythe
 */
public class RoleController {

    public static boolean isRolesMatched(Roles userRole, Roles menuRole) {
        boolean rvalue = false;
        for (String usrrole : userRole) {
            for (String menurole : menuRole) {
                if (menurole.equals(usrrole)) {
                    rvalue = true;
                    break;
                }
            }
            if(rvalue){
                break;
            }
        }
        return rvalue;
    }
    
    public static Roles getAllRoles(){
        Roles generalRoles = new Roles();
        generalRoles.add("guest");
        generalRoles.add("user");
        generalRoles.add("admin");
        return generalRoles;
    }

    public static Roles getUserRoles() {
        Roles generalRoles = new Roles();
        generalRoles.add("user");
        generalRoles.add("admin");
        return generalRoles;
    }

    public static Roles getAdminRoles() {
        Roles adminRoles = new Roles();
        adminRoles.add("admin");
        return adminRoles;
    }
    
    public static Roles getGuestRoles(){
        Roles adminRoles = new Roles();
        adminRoles.add("guest");
        return adminRoles;
    }
}
