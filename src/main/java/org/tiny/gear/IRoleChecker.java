package org.tiny.gear;

import org.apache.wicket.authroles.authorization.strategies.role.Roles;

/**
 *
 * @author dtmoyaji
 */
public interface IRoleChecker {

    public boolean isAuthenticated(Roles roles);
}
