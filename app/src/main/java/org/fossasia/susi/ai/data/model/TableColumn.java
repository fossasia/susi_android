package org.fossasia.susi.ai.data.model;

import io.realm.RealmObject;

public class TableColumn extends RealmObject {

    private String columnName;

    public TableColumn() {

    }

    public TableColumn(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
