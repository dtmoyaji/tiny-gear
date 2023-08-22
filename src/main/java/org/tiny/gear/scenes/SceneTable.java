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
package org.tiny.gear.scenes;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.tiny.datawrapper.Column;
import org.tiny.datawrapper.Table;
import org.tiny.datawrapper.TinyDatabaseException;
import org.tiny.datawrapper.annotations.LogicalName;
import org.tiny.gear.GearApplication;
import org.tiny.gear.Index;
import org.tiny.gear.RoleController;

/**
 *
 * @author bythe
 */
@LogicalName("シーンテーブル")
public class SceneTable extends Table implements Serializable{

    public static final long serialVersionUID = -1L;

    @LogicalName("クラス名")
    public Column<String> SceneClassName;

    @LogicalName("ロール名")
    public Column<String> RoleName;
    
    @LogicalName("並び順")
    public Column<Integer> Ordinal;

    @Override
    public void defineColumns() throws TinyDatabaseException {
        this.SceneClassName.setLength(Column.SIZE_512)
                .setPrimaryKey(true)
                .setAllowNull(false);

        this.RoleName.setLength(Column.SIZE_512)
                .setPrimaryKey(true)
                .setAllowNull(false);
        
        this.Ordinal.setDefault("-1");
    }
    
    public void registScene(GearApplication application, Class<? extends AbstractScene> sceneClass, int ordinal, String RoleName){
        SceneTable stable = (SceneTable) application.getCachedTable(SceneTable.class);
        stable.merge(
                stable.SceneClassName.setValue(sceneClass.getCanonicalName()),
                stable.Ordinal.setValue(ordinal),
                stable.RoleName.setValue(RoleName)
        );
    }

    public AbstractScene createScene(GearApplication application, String sceneClassName) {
        SceneTable sceneTable = new SceneTable();
        sceneTable.alterOrCreateTable(application.getJdbc());
        // sceneTable.setDebugMode(true);
        ResultSet rs = sceneTable.select(sceneTable.SceneClassName.sameValueOf(sceneClassName));
        AbstractScene scene = null;
        if (rs != null) {
            try {
                if (rs.next()) {
                    String roleName = sceneTable.RoleName.of(rs);
                    scene = (AbstractScene) Class.forName(sceneClassName)
                            .getDeclaredConstructor(Roles.class, GearApplication.class)
                            .newInstance(RoleController.of(roleName), application);
                }
                rs.close();
            } catch (SQLException
                    | ClassNotFoundException
                    | NoSuchMethodException
                    | SecurityException
                    | InstantiationException
                    | IllegalAccessException
                    | IllegalArgumentException
                    | InvocationTargetException ex) {
                sceneTable.delete(sceneTable.SceneClassName.sameValueOf(sceneClassName));
                Logger.getLogger(Index.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return scene;
    }
}
