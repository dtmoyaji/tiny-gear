package org.tiny.gear;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;

/**
 * ロールを扱うクラス
 *
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
            if (rvalue) {
                break;
            }
        }
        return rvalue;
    }

    public static Roles getAllRoles() {
        Roles rvalue = new Roles();
        rvalue.add("guest");
        rvalue.add("user");
        rvalue.add("admin");
        rvalue.add("developer");
        return rvalue;
    }

    public static Roles getUserRoles() {
        Roles rvalue = new Roles();
        rvalue.add("user");
        rvalue.add("admin");
        rvalue.add("developer");
        return rvalue;
    }

    public static Roles getAdminRoles() {
        Roles rvalue = new Roles();
        rvalue.add("admin");
        rvalue.add("developer");
        return rvalue;
    }

    public static Roles getGuestRoles() {
        Roles rvalue = new Roles();
        rvalue.add("guest");
        return rvalue;
    }

    public static Roles getDevelopmentRoles() {
        Roles rvalue = new Roles();
        rvalue.add("developer");
        return rvalue;
    }
}
