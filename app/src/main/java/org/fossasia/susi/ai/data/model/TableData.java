package org.fossasia.susi.ai.data.model;

import io.realm.RealmObject;

public class TableData extends RealmObject {

    private String tableData;

    public TableData() {

    }

    public TableData(String tableData) {
        this.tableData = tableData;
    }

    public String getTableData() {
        return tableData;
    }

    public void setTableData(String tableData) {
        this.tableData = tableData;
    }
}
