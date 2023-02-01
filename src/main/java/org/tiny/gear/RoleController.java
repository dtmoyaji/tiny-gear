package org.tiny.gear;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;

/**
 * ロールを扱うクラス
 * @author dtmoyaji
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
