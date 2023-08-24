package org.tiny.gear.panels.crud;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.tiny.datawrapper.Column;
import org.tiny.datawrapper.Condition;
import org.tiny.datawrapper.ConditionForRegion;
import org.tiny.datawrapper.Table;
import org.tiny.datawrapper.TinyDatabaseException;

/**
 * テーブルのデータを表示するパネル
 *
 * @author dtmoyaji
 */
public abstract class DataTableView extends DataTableInfoPanel {

    public static final long serialVersionUID = -1L;

    private Form curdTableView;

    private Label lblTableName;

    private Label lblRecordCount;

    private int rowsPerPage = 2; // ページに表示する最大行

    private int currentPage = 1;  // 現在のページ

    private int totalPageCount = -1;

    private Condition[] conditions = new Condition[]{};

    private ListView<Column> tableHeader;

    private ArrayList<KeyValueList> tableData = new ArrayList<>();

    private ListView<KeyValueList> tableRows;
    private ArrayList<Column> visibleHeader;

    private AjaxButton pageNext;
    private AjaxButton pagePrev;

    private Label lblCurPageNum;
    private Label lblPageCount;

    private CsvTransportPanel csvTransPortPanelPlaceHolder;

    public DataTableView(String id, Table table) {
        super(id);

        // 初期化
        this.setTable(table);
        this.setOutputMarkupId(true);

        // 描画
        this.curdTableView = new Form("curdTableView");
        this.curdTableView.setOutputMarkupId(true);
        this.add(this.curdTableView);

        this.lblTableName = new Label("lblTableName", Model.of("tablename"));
        this.curdTableView.add(this.lblTableName);

        this.lblRecordCount = new Label("lblRecordCount", Model.of(-1));
        this.curdTableView.add(this.lblRecordCount);

        // 格納したデータを描画する。
        // テーブルの行
        this.tableRows = new ListView<KeyValueList>("tableRows", this.tableData) {
            @Override
            protected void populateItem(ListItem<KeyValueList> item) {

                // テーブルの行
                KeyValueList recordData = (KeyValueList) item.getDefaultModelObject();
                ListView<KeyValue> tableRow = new ListView<KeyValue>("tableRow", recordData) {
                    @Override
                    protected void populateItem(ListItem<KeyValue> item) {
                        // セルのデータ
                        KeyValue celldata = (KeyValue) item.getDefaultModelObject();
                        if (celldata.getKey().equals("__EXTRA__")) {
                            RecordButtons panel = new RecordButtons("tableCell"); //Todo あとでジェネリクスでインスタンス化するコードに置換する。
                            item.add(panel);
                        } else {
                            Label tableCell = new Label("tableCell", Model.of(celldata.getValue()));
                            tableCell.add(new AjaxEventBehavior("click") {
                                @Override
                                protected void onEvent(AjaxRequestTarget target) {
                                    DataTableView.this.onRowClicked(
                                            target, celldata.getParent()
                                    );
                                }
                            });
                            item.add(tableCell);
                        }
                    }
                };
                tableRow.add(new AjaxEventBehavior("click") {
                    @Override
                    protected void onEvent(AjaxRequestTarget target) {
                        DataTableView.this.onRowClicked(
                                target, recordData
                        );
                    }
                });
                tableRow.setOutputMarkupId(true);
                item.add(tableRow);
            }
        };
        this.tableRows.setOutputMarkupId(true);
        this.curdTableView.add(this.tableRows);

        this.pageNext = new AjaxButton("pageNext") {

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                DataTableView.this.currentPage += 1;
                DataTableView.this.redraw();
                target.add(DataTableView.this);
            }
        };
        this.curdTableView.add(this.pageNext);

        this.pagePrev = new AjaxButton("pagePrev") {
            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                DataTableView.this.currentPage -= 1;
                DataTableView.this.redraw();
                target.add(DataTableView.this);
            }
        };
        this.curdTableView.add(this.pagePrev);

        this.lblCurPageNum = new Label("curPageNum", Model.of(this.currentPage));
        this.lblCurPageNum.setOutputMarkupId(true);
        this.curdTableView.add(this.lblCurPageNum);

        this.lblPageCount = new Label("allPageCount", Model.of(this.totalPageCount));
        this.lblPageCount.setOutputMarkupId(true);
        this.curdTableView.add(this.lblPageCount);

        //ヘッダの初期化
        this.visibleHeader = new ArrayList<>();
        this.tableHeader = new ListView<>("tableHeader", this.visibleHeader) {
            @Override
            protected void populateItem(ListItem<Column> item) {
                Column column = item.getModelObject();
                if (column.getVisibleType() != Column.VISIBLE_TYPE_HIDDEN) {
                    String caption = DataTableView.this.targetTable.getColumnLogicalName(column);
                    Label captionLabel = new Label("columnName", Model.of(caption));
                    item.add(captionLabel);
                } else {
                    Logger.getLogger(this.getClass().getName()).log(Level.INFO, column.getName() + " SKIPPED.");
                    System.out.println("SKIP " + column.getName());
                }
            }
        };
        this.tableHeader.setOutputMarkupId(true);
        this.curdTableView.add(this.tableHeader);

        this.csvTransPortPanelPlaceHolder = new CsvTransportPanel(
                "csvTransPortPanelPlaceHolder", 
                this,
                "_DataTableView"
        ) {
            @Override
            public void beforeConstructView(Table myTable) {
            }

            @Override
            public void afterConstructView(Table myTable) {
            }

        };
        this.csvTransPortPanelPlaceHolder.setTable(table);
        this.csvTransPortPanelPlaceHolder.setOutputMarkupId(true);
        this.curdTableView.add(this.csvTransPortPanelPlaceHolder);
        
        // データ取得
        this.redraw();
    }

    public void treatButtonClickable() {
        if (this.pagePrev != null) {
            if (this.currentPage == 1) {
                this.pagePrev.setEnabled(false);
            } else {
                this.pagePrev.setEnabled(true);
            }
        }

        if (this.pageNext != null) {
            if (this.currentPage == this.totalPageCount) {
                this.pageNext.setEnabled(false);
            } else {
                this.pageNext.setEnabled(true);
            }
        }
    }

    public void setRowsPerPage(int rowCount) {
        this.rowsPerPage = rowCount;
    }

    public Condition[] addCondition(Condition[] src, Condition cond) {
        Condition[] rvalue = new Condition[src.length + 1];
        System.arraycopy(src, 0, rvalue, 0, src.length);
        rvalue[rvalue.length - 1] = cond;
        return rvalue;
    }

    public ResultSet redraw(Condition... conditions) {

        this.beforeConstructView(targetTable);

        this.lblTableName.setDefaultModelObject(targetTable.getLogicalName());

        ResultSet rvalue = null;
        this.conditions = conditions;
        try {
            // データの描画
            int recordCount = this.getTable().getCount(
                    this.conditions
            );

            this.updateTableRows();

            // ヘッダ部分の描画
            this.drawTableHeader();

            DataTableView.this.treatButtonClickable();
            DataTableView.this.lblCurPageNum.setDefaultModelObject(
                    DataTableView.this.currentPage
            );
            this.lblPageCount.setDefaultModelObject(this.totalPageCount);

        } catch (TinyDatabaseException ex) {
            Logger.getLogger(DataTableView.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.afterConstructView(targetTable);
        return rvalue;
    }

    /**
     * テーブルの行を描画する。
     */
    private void updateTableRows() {
        try {
            // ページ情報の取得
            int rc = this.targetTable.getCount(this.conditions);
            this.lblRecordCount.setDefaultModelObject(rc + " レコード");
            this.totalPageCount = rc / this.rowsPerPage;
            if (rc % this.rowsPerPage > 0) {
                this.totalPageCount++;
            }

            // ResultSetを取得してアレイに格納する。
            int offset = (this.currentPage - 1) * this.rowsPerPage;
            Condition[] newCnd = this.addCondition(conditions, new ConditionForRegion(ConditionForRegion.LIMIT, this.rowsPerPage));
            newCnd = this.addCondition(newCnd, new ConditionForRegion(ConditionForRegion.OFFSET, offset));

            ResultSet rs = this.targetTable.select(newCnd);
            this.tableData.clear();
            while (rs.next()) {
                KeyValueList row = new KeyValueList();
                for (Column column : targetTable) {
                    if (!column.isMatchedVisibleType(Column.VISIBLE_TYPE_HIDDEN)) {
                        String cellName = column.getSplitedName();
                        String cellData = column.of(rs) != null ? column.of(rs).toString() : null;
                        KeyValue keyValue = new KeyValue(cellName, cellData);
                        keyValue.setPrimaryKey(column.isPrimaryKey());
                        row.add(keyValue);
                    }
                }
                // 操作用の拡張フィールド
                Class extra = this.getExtraColumn();
                if (extra != null) {
                    row.add(new KeyValue("__EXTRA__", extra.getName()));
                }
                this.tableData.add(row);
            }
            rs.close();

        } catch (SQLException | TinyDatabaseException ex) {
            Logger.getLogger(DataTableView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * テーブルのヘッダを描画する。
     */
    private void drawTableHeader() {
        // 非表示カラムを除外したデータを作成する。
        this.visibleHeader.clear();
        for (Column column : this.targetTable) {
            if (column.getVisibleType() != Column.VISIBLE_TYPE_HIDDEN) {
                visibleHeader.add(column);
            }
        }
    }

    public Condition[] getConditions() {
        return this.conditions;
    }
    
    public KeyValueList getFirstKeyValueList(){
        return this.tableData.get(0);
    }

    public abstract Class<? extends Panel> getExtraColumn();

    public abstract void onRowClicked(AjaxRequestTarget target, KeyValueList modelObject);

}
