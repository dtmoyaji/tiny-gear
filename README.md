# tiny-gear

ユーザー認証にSAMLを使用したWicketベースの開発フレームワーク


### LDAP > Keycloak > tiny-gear の連携時のユーザーロール
LDAPからユーザーフェデレーションによりユーザー情報を維持している場合、以下の手順でLDAPでユーザー権限を制御できる。
 *LDAPのorganizationalUnitを使って権限ごとにグループを作り、ユーザーを登録する。
 *Keycloakのユーザーフェデレーションでrole-ldap-mapperを使ってマッパーを追加すると、ログイン認証時にグループ名がユーザーロールに格納されて情報提供される。

Keycloak単独でユーザーIDを供給する場合は、Keycloakでロールを手動追加しておき、各ユーザーに必要なロールを割り当てる。

