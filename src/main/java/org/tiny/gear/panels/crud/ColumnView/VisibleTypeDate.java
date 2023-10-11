package org.tiny.gear.panels.crud.ColumnView;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.tiny.datawrapper.Column;

/**
 * 日付型に対応した入力コントロール
 *
 * @author dtmoyaji
 */
public class VisibleTypeDate extends AbstractColumnView {

    private Form controlValueForm;
    private DateTextField controlValue;

    public VisibleTypeDate(String id, IModel<Column> column) {
        super(id, column);

    }

    @Override
    public void onInitialize() {
        super.onInitialize();

        this.controlValueForm = new Form("controlValueForm");
        this.controlValueForm.setOutputMarkupId(true);
        this.add(this.controlValueForm);

        String offsetString = this.getGearApplication().getSystemVariable("TIMEZONE_OFFSET", "+9");
        ZoneOffset offset = ZoneOffset.of(offsetString);
        Date colvalue;
        if (this.getColumn().getValue() != null) {
            Timestamp innerValue = (Timestamp) this.getColumn().getValue();
            colvalue = Date.from(innerValue.toLocalDateTime().toInstant(offset));
        } else {
            colvalue = new Date();
        }

        this.controlValue = new DateTextField("controlValue", new Model<>(), "yyyy-MM-dd");
        this.controlValue.setModelObject(colvalue);

        this.controlValue.setOutputMarkupId(true);
        this.controlValueForm.add(this.controlValue);
    }

    @Override
    public void copyControlValueToColumn() {
        Date dtvalue = this.controlValue.getModelObject();
        LocalDateTime buf = this.controlValue.getModelObject().toInstant()
                .atZone(ZoneOffset.of(this.getGearApplication().getSystemVariable("TIMEZOE_OFFSET", "+9")))
                .toLocalDateTime();
        Timestamp colValue = Timestamp.valueOf(buf);
        this.getColumn().setValue(colValue);
    }

    @Override
    public void copyColumnValueToConrtol() {
        Timestamp colValue = (Timestamp) this.getColumn().getValue();
        LocalDateTime ldt = colValue.toLocalDateTime();
        Date cntlValue = Date.from(ldt.toInstant(
                ZoneOffset.of(
                        this.getGearApplication().getSystemVariable("TIMEZONE_OFFSET", "+9")
                )
        ));
        this.controlValue.setDefaultModelObject(cntlValue);
    }

}
