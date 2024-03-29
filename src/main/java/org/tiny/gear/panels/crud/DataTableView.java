package org.tiny.gear.panels.crud;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.tiny.datawrapper.Column;
import org.tiny.datawrapper.Condition;
import org.tiny.datawrapper.ConditionForRegion;
import org.tiny.datawrapper.RelationInfo;
import org.tiny.datawrapper.Table;

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

    /**
     * 外部から与えられるフィルタ条件
     */
    private ArrayList<Condition> conditionsForFilter = new ArrayList<>();

    /**
     * ページ範囲を含むフィルタ条件
     */
    private ArrayList<Condition> conditionsForPage = new ArrayList();

    /**
     * 並べ替え条件
     */
    private ArrayList<Condition> conditionsForOrdinal = new ArrayList();

    private ListView<Column> tableHeader;

    private ArrayList<KeyValueList> tableData = new ArrayList<>();

    private HashMap<String, Table> subTable = new HashMap<>();

    private ListView<KeyValueList> tableRows;
    private WebMarkupContainer tableBody;

    private ArrayList<Column> visibleHeader;
    private HashMap<String, SortingControl> sortingControls;

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

        this.tableBody = new WebMarkupContainer("tableBody");
        this.curdTableView.add(this.tableBody);

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
        this.tableBody.add(this.tableRows);
        this.tableBody.setOutputMarkupId(true);

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
        this.sortingControls = new HashMap<>();
        this.tableHeader = new ListView<>("tableHeader", this.visibleHeader) {
            @Override
            protected void populateItem(ListItem<Column> item) {
                Column column = item.getModelObject();
                if (column.getVisibleType() != Column.VISIBLE_TYPE_HIDDEN) {
                    String caption = DataTableView.this.targetTable.getColumnLogicalName(column);
                    if (column.isPrimaryKey()) {
                        caption += "*";
                    }
                    Label captionLabel = new Label("columnName", Model.of(caption));
                    item.add(captionLabel);
                    SortingControl sortingControl = sortingControls.get(column.getName());
                    if (sortingControl == null) {
                        sortingControl = new SortingControl("sortingControl", column) {
                            @Override
                            public void onClick(AjaxRequestTarget target) {
                                super.onClick(target);
                                DataTableView.this.sortOrderChanged(target);
                            }
                        };
                        sortingControls.put(column.getName(), sortingControl);
                    }
                    item.add(sortingControl);
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

    public void sortOrderChanged(AjaxRequestTarget target) {
        this.conditionsForOrdinal.clear();
        Set<String> keys = this.sortingControls.keySet();
        for (String key : keys) {
            SortingControl scontrol = this.sortingControls.get(key);
            if (scontrol.getOrder() != null && scontrol.getOrder().getColumn() != null) {
                this.conditionsForOrdinal.add(scontrol.getOrder());
            }
        }
        this.redraw();
        target.add(this.tableBody);

    }

    /**
     * 絞り込み条件、ソート条件、ページ範囲を結合して1つの配列にする。
     *
     * @return
     */
    public Condition[] getUnifiedConditions() {
        ArrayList<Condition> conditionArray = new ArrayList();
        conditionArray.addAll(this.conditionsForFilter);
        conditionArray.addAll(this.conditionsForOrdinal);
        conditionArray.addAll(this.conditionsForPage);
        ArrayList<Condition> buf = new ArrayList();
        /*        for(Condition condition: conditionArray){
            if( !(condition instanceof ConditionForOrder)
                    && condition.getColumn()==null){
                buf.add(condition);
            }
        }
        for(Condition cnd: buf){
            conditionArray.remove(cnd);
        }*/
        return this.ConditionsToArray(conditionArray);
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

    public Condition[] margeConditions(Condition[] src, Condition[] additive) {
        Condition[] rvalue;
        if (src.length < 1) {
            rvalue = additive;
        } else {
            rvalue = new Condition[src.length + additive.length];
            System.arraycopy(src, 0, rvalue, 0, src.length);
            System.arraycopy(additive, 0, rvalue, src.length, additive.length);
        }
        return rvalue;
    }

    public ArrayList<Condition> getConditionsForFilter() {
        return this.conditionsForFilter;
    }

    public ArrayList<Condition> getConditionsForOrdinal() {
        return this.conditionsForOrdinal;
    }

    public ArrayList<Condition> getConditionsForPage() {
        return this.conditionsForPage;
    }

    public Condition[] ConditionsToArray(ArrayList<Condition> conditionsList) {
        Condition[] rvalue = new Condition[conditionsList.size()];
        for (int i = 0; i < conditionsList.size(); i++) {
            rvalue[i] = conditionsList.get(i);
        }
        return rvalue;
    }

    public ResultSet redraw() {

        this.beforeConstructView(targetTable);

        this.lblTableName.setDefaultModelObject(targetTable.getLogicalName());

        ResultSet rvalue = null;
        // とくにソート指定がない場合は、主キーを使って並び変える。
        if (this.conditionsForOrdinal.isEmpty()) {
            for (Column column : this.targetTable) {
                if (column.isPrimaryKey()) {
                    this.conditionsForOrdinal.add(column.asc());
                }
            }
        }

        Condition[] unifiedConditions = this.getUnifiedConditions();

        // データの描画
        int recordCount = this.getTable().getCount(
                this.ConditionsToArray(this.conditionsForFilter)
        );
        this.drawTableRows();
        // ヘッダ部分の描画
        this.drawTableHeader();
        DataTableView.this.treatButtonClickable();
        DataTableView.this.lblCurPageNum.setDefaultModelObject(
                DataTableView.this.currentPage
        );
        this.lblPageCount.setDefaultModelObject(this.totalPageCount);

        this.afterConstructView(targetTable);
        return rvalue;
    }

    /**
     * テーブルの行を描画する。
     */
    private void drawTableRows() {
        try {
            // ページ情報の取得
            int rc = this.targetTable.getCount(
                    this.ConditionsToArray(this.conditionsForFilter)
            );
            this.lblRecordCount.setDefaultModelObject(rc + " レコード");
            this.totalPageCount = rc / this.rowsPerPage;
            if (rc % this.rowsPerPage > 0) {
                this.totalPageCount++;
            }

            // ResultSetを取得してアレイに格納する。
            int offset = (this.currentPage - 1) * this.rowsPerPage;
            this.conditionsForPage.clear();
            this.conditionsForPage.add(new ConditionForRegion(ConditionForRegion.LIMIT, this.rowsPerPage));
            this.conditionsForPage.add(new ConditionForRegion(ConditionForRegion.OFFSET, offset));

            // 外部テーブルデータをSELECT文に追加
            for (Column col : this.targetTable) {
                this.conditionsForFilter.add(col.setSelectable(true));
                if (col.hasRelation()) {
                    RelationInfo rinfo = (RelationInfo) col.get(0);
                    Table subt = this.getGearApplication().getCachedTable(rinfo.getTableClass());
                    for (Column subc : subt) {
                        if (subc.isMatchedRelationType(Column.TARGET_FOR_EXTERNAL_RELATION)) {
                            this.conditionsForFilter.add(subc.setSelectable(true));
                        }
                    }
                }
            }

            Condition[] conditions = this.getUnifiedConditions();
            //this.targetTable.setDebugMode(true);
            ResultSet rs = this.targetTable.select(conditions);
            //this.targetTable.setDebugMode(false);
            this.tableData.clear();
            while (rs.next()) {
                KeyValueList row = new KeyValueList();
                for (Column column : targetTable) {

                    // 内部フィールドの描画
                    if (!column.isMatchedVisibleType(Column.VISIBLE_TYPE_HIDDEN)) {
                        String cellName = column.getSplitedName();
                        String cellData = column.of(rs) != null ? column.of(rs).toString() : null;
                        KeyValue keyValue = new KeyValue(cellName, cellData);
                        keyValue.setPrimaryKey(column.isPrimaryKey());
                        row.add(keyValue);
                    }

                    // 外部フィールドの自動追加
                    if (column.hasRelation()) {
                        RelationInfo relinfo = (RelationInfo) column.get(0);
                        Table externalTable = this.getGearApplication().getCachedTable(relinfo.getTableClass());
                        for (Column extcol : externalTable) {
                            if (!extcol.isMatchedVisibleType(Column.VISIBLE_TYPE_HIDDEN)
                                    && extcol.isMatchedRelationType(Column.TARGET_FOR_EXTERNAL_RELATION)) {
                                String extcellName = extcol.getSplitedName();
                                String extcellData = extcol.of(rs) != null ? extcol.of(rs).toString() : null;
                                KeyValue extkeyValue = new KeyValue(extcellName, extcellData);
                                extkeyValue.setPrimaryKey(extcol.isPrimaryKey());
                                row.add(extkeyValue);
                            }
                        }
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

        } catch (SQLException ex) {
            Logger.getLogger(DataTableView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void setTable(Table table) {
        this.conditionsForFilter.clear();
        this.conditionsForOrdinal.clear();
        this.conditionsForPage.clear();
        super.setTable(table);
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
            // 外部フィールドの自動追加
            if (column.hasRelation()) {
                RelationInfo relinfo = (RelationInfo) column.get(0);
                Table externalTable = this.getGearApplication().getCachedTable(relinfo.getTableClass());
                for (Column extcol : externalTable) {
                    if (extcol.isMatchedRelationType(Column.TARGET_FOR_EXTERNAL_RELATION)
                            && !extcol.isMatchedVisibleType(Column.VISIBLE_TYPE_HIDDEN)) {
                        this.visibleHeader.add(extcol);
                    }
                }
            }
        }
    }

    public KeyValueList getFirstKeyValueList() {
        return this.tableData.get(0);
    }

    public abstract Class<? extends Panel> getExtraColumn();

    public abstract void onRowClicked(AjaxRequestTarget target, KeyValueList modelObject);

}
