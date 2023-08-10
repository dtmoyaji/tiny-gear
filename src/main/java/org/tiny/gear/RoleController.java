package org.tiny.gear;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;


/**
 * ロールを扱うクラス
 *
 * @author dtmoyaji
 */
public class RoleController {
    public static final String ROLE_ALL = "ALL";
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_GUEST = "GUEST";
    public static final String ROLE_DEVELOPER = "DEVELOPMENT";

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
    
    public static Roles of(String RoleName){
        Roles role = null;
        switch(RoleName){
            case RoleController.ROLE_ADMIN:
                role = RoleController.getAdminRoles();
                break;
            case RoleController.ROLE_ALL:
                role = RoleController.getAllRoles();
                break;
            case RoleController.ROLE_DEVELOPER:
                role = RoleController.getDevelopmentRoles();
                break;
            case RoleController.ROLE_GUEST:
                role = RoleController.getGuestRoles();
                break;
            case RoleController.ROLE_USER:
                role = RoleController.getUserRoles();
                break;
        }
        return role;
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
