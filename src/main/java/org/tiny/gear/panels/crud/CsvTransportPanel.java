package org.tiny.gear.panels.crud;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.tiny.gear.GroovyExecuteButton;

/**
 *
 * @author dtmoyaji
 */
public abstract class CsvTransportPanel extends DataTableInfoPanel {

    GroovyExecuteButton exportButton;

    GroovyExecuteButton importButton;

    public CsvTransportPanel(String id, Panel basePanel, String panelName) {
        super(id);

        this.exportButton = new GroovyExecuteButton(
                "exportButton",
                Model.of("エクスポート"),
                this.getGearApplication(),
                basePanel,
                panelName
        );
        this.add(this.exportButton);

        this.importButton = new GroovyExecuteButton(
                "importButton",
                Model.of("インポート"),
                this.getGearApplication(),
                basePanel,
                panelName
        );
        this.add(this.importButton);
    }

}
