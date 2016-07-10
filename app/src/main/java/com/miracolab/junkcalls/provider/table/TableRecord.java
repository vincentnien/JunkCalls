package com.miracolab.junkcalls.provider.table;

import com.miracolab.junkcalls.provider.AbstractProvider;
import com.miracolab.junkcalls.provider.AbstractTable;

public class TableRecord extends AbstractTable {
    public static final String TABLE = "record";

    public TableRecord(AbstractProvider provider) {
        super(provider);
    }

    public static final class Columns {
        public static final String NUMBER = "number";
        public static final String DESCRIPTION = "description";
        public static final String REPORT = "report";
    }

    @Override
    public void setupColumns() {
        addColumn(Columns.NUMBER, "VARCHAR(32)");
        addColumn(Columns.DESCRIPTION, "TEXT");
        addColumn(Columns.REPORT, "INTEGER");
    }

    @Override
    public String getTableName() {
        return TABLE;
    }
}
