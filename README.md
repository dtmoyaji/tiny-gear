# tiny-gear

ユーザー認証にSAMLを使用したWicketベースの開発フレームワーク
* Groovyでカスタムテーブルを組み込むことができます。
 
　汎用データ編集でGroovyを使用してtiny-datawrapperのテーブルを継承したクラスを作成すると、カスタムテーブルが作成されます。 開発者権限を持っているユーザはそのまま汎用データ編集でデータ編集ができます。
  
* CRUDに対応したマスター編集画面の自動生成機能を搭載しています。

 カスタムテーブルを作成すると、自動的に新規作成、更新、削除用の画面をマスターメンテメニュー内に生成します。

 JDBC接続を使って、外部のデータベースと接続できます。外部データベースの閲覧、編集、更新、エクスポート処理をサポートしています。

### LDAP > Keycloak > tiny-gear の連携時のユーザーロール
LDAPからユーザーフェデレーションによりユーザー情報を維持している場合、以下の手順でLDAPでユーザー権限を制御できる。

* LDAPのorganizationalUnitを使って権限ごとにグループを作り、ユーザーを登録する。
* Keycloakのユーザーフェデレーションでrole-ldap-mapperを使ってマッパーを追加すると、ログイン認証時にグループ名がユーザーロールに格納されて情報提供される。

Keycloak単独でユーザーIDを供給する場合は、Keycloakでロールを手動追加しておき、各ユーザーに必要なロールを割り当てる。

