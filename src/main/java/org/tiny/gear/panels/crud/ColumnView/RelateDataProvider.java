package org.tiny.gear.panels.crud.ColumnView;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.tiny.datawrapper.Column;
import org.tiny.datawrapper.Table;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;

/**
 *
 * @author dtmoyaji
 */
public class RelateDataProvider extends ChoiceProvider<KeyValueContainer> {

    private static final long serialVersionUID = 1L;

    private Table table;
    private Column optionValueColumn;

    public RelateDataProvider(Table table, Column optionValueColumn) {
        super();
        this.table = table;
        this.optionValueColumn = optionValueColumn;
    }

    @Override
    public void query(String string, int i, Response<KeyValueContainer> rspns) {
        
        Column pkey = null;
        for (Column col : this.table) {
            if (col.isPrimaryKey()) {
                pkey = col;
                break;
            }
        }
        this.table.setDebugMode(true);
        
        String SearchWord = "%"+string+"%";
        SearchWord = SearchWord.replaceAll("'", "''"); // SQL Injection対策
        try (ResultSet rs = this.table.select(pkey.setSelectable(true), this.optionValueColumn.like(SearchWord))) {
            while (rs.next()) {
                KeyValueContainer newkv = new KeyValueContainer(
                        (int) pkey.of(rs),
                        String.valueOf(this.optionValueColumn.of(rs))
                );
                rspns.add(newkv);
            }
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(RelateDataProvider.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.table.setDebugMode(false);
    }

    @Override
    public Collection<KeyValueContainer> toChoices(Collection<String> clctn) {
        ArrayList<KeyValueContainer> rvalue = new ArrayList<>();

        for (String key : clctn) {
            this.table.clearValues();
            Column pkey = null;
            for (Column col : this.table) {
                if (col.isPrimaryKey()) {
                    col.setSelectable(true);
                    pkey = col;
                    break;
                }
            }
            try (ResultSet rs = this.table.select(pkey.sameValueOf(key))) {
                if (rs.next()) {
                    int id = (int) pkey.of(rs);
                    String value = String.valueOf(this.optionValueColumn.of(rs));
                    KeyValueContainer newkv = new KeyValueContainer(id, value);
                    rvalue.add(newkv);
                }
            } catch (SQLException ex) {
                Logger.getLogger(RelateDataProvider.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return rvalue;
    }

    @Override
    public String getDisplayValue(KeyValueContainer t) {
        return t.getValue();
    }

    @Override
    public String getIdValue(KeyValueContainer t) {
        return String.valueOf(t.getKey());
    }

}
