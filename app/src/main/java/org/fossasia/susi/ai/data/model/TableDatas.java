package org.fossasia.susi.ai.data.model;

import java.util.ArrayList;

/**
 * Created by meeera on 24/8/17.
 */

public class TableDatas {
    private ArrayList<String> columns;
    private ArrayList<String> tableData;

    public TableDatas() {
    }

    public TableDatas(ArrayList<String> columns, ArrayList<String> tableData) {
        this.columns = columns;
        this.tableData = tableData;
    }

    public ArrayList<String> getColumns() {
        return columns;
    }

    public void setColumns(ArrayList<String> columns) {
        this.columns = columns;
    }

    public ArrayList<String> getTableData() {
        return tableData;
    }

    public void setTabledata(ArrayList<String> tableData) {
        this.tableData = tableData;
    }
}
