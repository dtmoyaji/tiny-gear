package org.tiny.gear.panels.crud.ColumnView;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.tiny.datawrapper.Column;
import org.tiny.datawrapper.RelationInfo;
import org.tiny.datawrapper.Table;
import org.tiny.gear.model.Attribute;
import org.wicketstuff.select2.Select2Choice;

/**
 *
 * @author dtmoyaji
 */
public class SimpleRelationSelector extends AbstractColumnView {

    private Form form;
    private Label recordId;
    private Select2Choice<KeyValueContainer> select2Choice;

    public SimpleRelationSelector(String id, IModel<Column> model) {
        super(id, model);

        this.form = new Form("columnValueForm");
        this.add(this.form);

        Column col = model.getObject();
        RelationInfo rinfo = (RelationInfo) col.get(0);
        Table rtable = this.getGearApplication().getCachedTable(rinfo.getTableClass());
        Column searchCol = null;
        Column pkcol = null;
        for (Column eachcol : rtable) {
            if (eachcol.isPrimaryKey()) {
                pkcol = eachcol;
                break;
            }
        }
        for (Column eachcol : rtable) {
            if (eachcol.getAttributes().containsKey(Attribute.COLUMN_FOR_SEARCH)) {
                searchCol = eachcol;
                break;
            }
        }

        this.select2Choice = new Select2Choice<>(
                "columnValue",
                new Model(new KeyValueContainer(-1, "")),
                new RelateDataProvider(rtable, searchCol));
        this.select2Choice.add(new AjaxFormComponentUpdatingBehavior("change") {
            @Override
            protected void onUpdate(AjaxRequestTarget art) {
                KeyValueContainer kvc = select2Choice.getModelObject();
                recordId.setDefaultModelObject(kvc.getKey());
                art.add(select2Choice);
                art.add(recordId);
            }

        });
        this.select2Choice.getSettings().setMinimumInputLength(1)
                .setPlaceholder("1文字以上入力")
                .setAllowClear(true);
        this.select2Choice.setOutputMarkupId(true);
        this.form.add(this.select2Choice);

        this.recordId = new Label("recordId", Model.of(-1));
        this.recordId.setOutputMarkupId(true);
        this.form.add(this.recordId);

        rtable.selectAllColumn();
        try (ResultSet rs = rtable.select(pkcol.sameValueOf(String.valueOf(col.getValue())))) {
            if (rs != null && rs.next()) {
                this.recordId.setDefaultModelObject(pkcol.of(rs));
                this.select2Choice.setDefaultModelObject(
                        new KeyValueContainer(
                                (int) pkcol.of(rs),
                                (String) searchCol.of(rs))
                );
            }
        } catch (SQLException ex) {
            Logger.getLogger(SimpleRelationSelector.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void updateColumnValue() {
        Column col = (Column) this.getDefaultModelObject();
        col.setValue(String.valueOf(this.recordId.getDefaultModelObject()));
    }

}
