package org.tiny.gear;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tiny.datawrapper.Jdbc;
import org.tiny.datawrapper.NameDescriptor;
import org.tiny.datawrapper.Table;
import org.tiny.gear.scenes.webdb.CustomTable;

/**
 * CustomTableで作成したGroovy形式のテーブル定義をインスタンス化するクラス
 * 指定するGroovyのソースコードは、パッケージ宣言とimport宣言を省略すること。
 *
 * @author dtmoyaji
 */
public class CustomTableBuilder {

    public static final String CUSTOM_TABLE_PACKAGE = "org.tiny.gear.customtable";

    public static final String JDBC_NAME_LOCAL = "LOCAL";

    private GearApplication app;
    private CustomTable customTable;
    private String tableDefHeader = "";
    private String codeForInstance = "return new %s()";

    public CustomTableBuilder(GearApplication app) {
        this.app = app;
        this.customTable = new CustomTable();
        this.customTable.setJdbc(app.getJdbc());

        this.tableDefHeader += "package " + CustomTableBuilder.CUSTOM_TABLE_PACKAGE + "\n";
        this.tableDefHeader += "import java.sql.Timestamp \n";
        this.tableDefHeader += "import org.tiny.gear.GearApplication \n";
        this.tableDefHeader += "import org.tiny.datawrapper.annotations.LogicalName \n";
        this.tableDefHeader += "import org.tiny.datawrapper.annotations.Comment \n";
        this.tableDefHeader += "import org.tiny.datawrapper.Table \n";
        this.tableDefHeader += "import org.tiny.datawrapper.View \n";
        this.tableDefHeader += "import org.tiny.datawrapper.Column \n";
        this.tableDefHeader += "import org.tiny.datawrapper.IncrementalKey \n";
        this.tableDefHeader += "import org.tiny.datawrapper.ShortFlagZero \n";
        this.tableDefHeader += "import org.tiny.datawrapper.CurrentTimestamp \n";

    }

    public Table createTable(String tableName, String tableDef) throws Exception {
        Table rvalue = this.createTableObject(tableName, tableDef);
        if (rvalue != null) {
            rvalue.alterOrCreateTable(this.app.getJdbc());
        }
        return rvalue;

    }

    public Table createTable(String tableName, String tableDef, String jdbcStackName) throws Exception {
        Table rvalue = this.createTableObject(tableName, tableDef);
        Jdbc jdbc;
        if (jdbcStackName.equals(CustomTableBuilder.JDBC_NAME_LOCAL)) {
            jdbc = this.app.getJdbc();
            rvalue.alterOrCreateTable(jdbc);
        } else {
            rvalue.recordInfo(this.app.getJdbc());
            jdbc = this.getExternalJdbc(jdbcStackName);
            rvalue.setJdbc(jdbc);
        }
        return rvalue;
    }

    public Table createTable(String tableName, String tableDef, String postConstruct, String jdbcStackName) throws Exception {
        Table rvalue = this.createTable(tableName, tableDef, jdbcStackName);
        if ((postConstruct == null) || postConstruct.isEmpty()) {
            return rvalue;
        }
        Binding binding = new Binding();
        binding.setVariable("_GearApplication", this.app);
        binding.setVariable("_Table", rvalue);
        GroovyShell grshell = new GroovyShell(binding);
        postConstruct += "\n return _rvalue;";
        grshell.evaluate(postConstruct);
        return rvalue;
    }

    private Table createTableObject(String tableName, String tableDef) throws Exception {
        Table rvalue = this.getTable(tableName);
        if (rvalue == null) {
            String cfi = String.format(codeForInstance, NameDescriptor.toJavaName(tableName));
            tableDef = tableDefHeader + "\n" + tableDef + "\n" + cfi;
            Binding binding = new Binding();
            binding.setVariable("_GearApplication", this.app);
            GroovyShell grshell = new GroovyShell(binding);
            rvalue = (Table) grshell.evaluate(tableDef);
        }
        return rvalue;
    }

    private Jdbc getExternalJdbc(String jdbcStackName) {
        Jdbc rvalue = null;
        Table jdbcStack = this.app.getCachedTable(CustomTableBuilder.CUSTOM_TABLE_PACKAGE + ".JdbcStack");
        try (ResultSet rs = jdbcStack.select(jdbcStack.get("Name").sameValueOf(jdbcStackName))) {
            if (rs.next()) {
                rvalue = new Jdbc();
                rvalue.setDriver((String) jdbcStack.get("DRIVER_CLASS").of(rs));
                rvalue.setUrl((String) jdbcStack.get("JDBC_URL").of(rs));
                rvalue.setUser((String) jdbcStack.get("JDBC_USER").of(rs));
                rvalue.setPassword((String) jdbcStack.get("JDBC_PASSWORD").of(rs));
            }
        } catch (SQLException ex) {
            Logger.getLogger(CustomTableBuilder.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rvalue;
    }

    private Table getTable(String tableName) {
        String tableClass = CustomTableBuilder.CUSTOM_TABLE_PACKAGE
                + "." + NameDescriptor.toJavaName(tableName);
        Table rvalue = (this.app.isTableCached(tableClass))
                ? this.app.getCachedTable(tableClass) : null;
        return rvalue;
    }

    public Table createTable(String tableName) {

        Table rvalue = this.getTable(tableName);
        if (rvalue == null) {
            try (ResultSet rs = this.customTable.select(
                    this.customTable.TableName.sameValueOf(tableName)
            )) {
                if (rs.next()) {
                    String tableDef = this.customTable.TableDef.of(rs);
                    String jdbcName = this.customTable.JdbcStackName.of(rs);
                    String postConstruct = this.customTable.PostConstructed.of(rs);
                    rs.close();
                    rvalue = this.createTable(tableName, tableDef, postConstruct, jdbcName);
                }
            } catch (Exception ex) {
                Logger.getLogger(CustomTableBuilder.class.getName())
                        .log(Level.SEVERE, null, ex);
            }
        }
        return rvalue;
    }

    public static String toSQLName(String tblClass) {
        tblClass = tblClass.replace(
                CustomTableBuilder.CUSTOM_TABLE_PACKAGE + ".",
                ""
        );
        String rvalue = NameDescriptor.toSqlName(tblClass);
        return rvalue;
    }

    public static boolean isCustomTable(String tblClassName) {
        return tblClassName.contains(CustomTableBuilder.CUSTOM_TABLE_PACKAGE);
    }

}
