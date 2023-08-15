package org.tiny.gear;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tiny.datawrapper.NameDescriptor;
import org.tiny.datawrapper.Table;
import org.tiny.gear.scenes.webdb.CustomTable;

/**
 * CustomTableで作成したGroovy形式のテーブル定義をインスタンス化するクラス
 *
 * @author dtmoyaji
 */
public class GroovyTableBuilder {

    public static final String CUSTOM_TABLE_PACKAGE = "org.tiny.gear.customtable";

    private GearApplication app;
    private CustomTable customTable;
    private String tableDefHeader = "";
    private String codeForInstance = "return new %s()";

    public GroovyTableBuilder(GearApplication app) {
        this.app = app;
        this.customTable = (CustomTable) app.getCachedTable(CustomTable.class);
        this.customTable.setJdbc(app.getJdbc());

        this.tableDefHeader += "package " + GroovyTableBuilder.CUSTOM_TABLE_PACKAGE + "\n";
        this.tableDefHeader += "import org.tiny.datawrapper.annotations.LogicalName \n";
        this.tableDefHeader += "import org.tiny.datawrapper.annotations.Comment \n";
        this.tableDefHeader += "import org.tiny.datawrapper.Table \n";
        this.tableDefHeader += "import org.tiny.datawrapper.Column \n";
        this.tableDefHeader += "import org.tiny.datawrapper.IncrementalKey \n";
        this.tableDefHeader += "import org.tiny.datawrapper.ShortFlagZero \n";
        this.tableDefHeader += "import org.tiny.datawrapper.CurrentTimestamp \n";

    }

    public Table createTable(String tableName, String tableDef) {
        Table rvalue = this.getTable(tableName);
        if (rvalue == null) {
            String cfi = String.format(codeForInstance, NameDescriptor.toJavaName(tableName));
            tableDef = tableDefHeader + "\n" + tableDef + "\n" + cfi;
            Binding binding = new Binding();
            GroovyShell grshell = new GroovyShell(binding);
            rvalue = (Table) grshell.evaluate(tableDef);
            rvalue.alterOrCreateTable(this.app.getJdbc());
            rvalue = this.app.stackTableOnCach(rvalue);
        }
        return rvalue;

    }

    private Table getTable(String tableName) {
        String tableClass = GroovyTableBuilder.CUSTOM_TABLE_PACKAGE + "." + NameDescriptor.toJavaName(tableName);
        Table rvalue = (this.app.isTableCached(tableClass)) ? this.app.getCachedTable(tableClass) : null;
        return rvalue;
    }

    public Table createTable(String tableName) {

        Table rvalue = this.getTable(tableName);
        if (rvalue == null) {
            try (ResultSet rs = this.customTable.select(this.customTable.TableName.sameValueOf(tableName))) {
                if (rs.next()) {
                    String tableDef = this.customTable.TableDef.of(rs);
                    rs.close();
                    rvalue = this.createTable(tableName, tableDef);
                }
            } catch (SQLException ex) {
                Logger.getLogger(GroovyTableBuilder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return rvalue;
    }

    public static String toSQLName(String tblClass) {
        tblClass = tblClass.replace(GroovyTableBuilder.CUSTOM_TABLE_PACKAGE + ".", "");
        String rvalue = NameDescriptor.toSqlName(tblClass);
        return rvalue;
    }

}
