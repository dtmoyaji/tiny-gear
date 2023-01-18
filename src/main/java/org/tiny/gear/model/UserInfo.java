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
package org.tiny.gear.model;

import org.tiny.datawrapper.Column;
import org.tiny.datawrapper.IncrementalKey;
import org.tiny.datawrapper.Table;
import org.tiny.datawrapper.TinyDatabaseException;
import org.tiny.datawrapper.annotations.Comment;
import org.tiny.datawrapper.annotations.LogicalName;

/**
 * ユーザーデータ
 *
 * @author dtmoyaji
 */
@LogicalName("ユーザー情報")
@Comment("SAML認証で取得したユーザー情報を格納するテーブル")
public class UserInfo extends Table {
    
    @LogicalName("レコード番号")
    public IncrementalKey RecordId;

    @LogicalName("ユーザーID")
    @Comment("KeycloakのUUIDを格納する")
    public Column<String> UserId;
    
    @LogicalName("ユーザー名")
    public Column<String> UserName;
    
    @LogicalName("属性情報")
    @Comment("SAMLで取得した属性を全部JSONで格納する")
    public Column<String> AttributeJson;

    @Override
    public void defineColumns() throws TinyDatabaseException {
        
        this.UserId.setLength(Column.SIZE_64)
                .setAllowNull(false);
        
        this.UserName.setLength(Column.SIZE_128)
                .setAllowNull(false);
        
        this.AttributeJson.setLength(Column.SIZE_1024);
        
    }

}
