package org.tiny.gear.model;

import java.sql.Timestamp;
import org.tiny.datawrapper.Column;
import org.tiny.datawrapper.ShortFlagZero;
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

    public static final long serialVersionUID = -1L;

    @LogicalName("ユーザーID")
    @Comment("KeycloakのUUIDを格納する")
    public Column<String> UserId;

    @LogicalName("無効フラグ")
    @Comment("１で無効")
    public ShortFlagZero Disable;

    @LogicalName("ユーザー名")
    @Comment("SamlUserInfo.getNameIdと同値")
    public Column<String> UserName;

    @LogicalName("属性情報")
    @Comment("SAMLで取得した属性を全部JSONで格納する")
    public Column<String> AttributeJson;

    @LogicalName("最終アクセス")
    @Comment("最後にユーザーがアクセスした日時")
    public Column<Timestamp> LastAccess;

    @Override
    public void defineColumns() throws TinyDatabaseException {
        
        this.UserId.setLength(Column.SIZE_64)
                .setAllowNull(false)
                .setVisibleType(Column.VISIBLE_TYPE_LABEL)
                .setPrimaryKey(true);

        this.UserName.setLength(Column.SIZE_128)
                .setAllowNull(false);

        this.AttributeJson.setLength(Column.SIZE_2048)
                .setVisibleType(Column.VISIBLE_TYPE_TEXTAREA);

        this.LastAccess.setAllowNull(false)
                .setDefault("CURRENT_TIMESTAMP")
                .setVisibleType(Column.VISIBLE_TYPE_LABEL);
        
    }
}
