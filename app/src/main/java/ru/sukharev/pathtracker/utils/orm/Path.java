package ru.sukharev.pathtracker.utils.orm;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * That class implements ORM representation of paths, that user records.
 */

@DatabaseTable(tableName = "paths")
public class Path {

    public final static String COLUMN_NAME = "name";
    public final static String COLUMN_START_DATE = "start";
    public final static String COLUMN_END_DATE = "end";

    @DatabaseField(id = true, canBeNull = false, dataType = DataType.STRING, columnName = COLUMN_NAME)
    private String name;

    @DatabaseField(canBeNull = false, dataType = DataType.DATE, columnName = COLUMN_START_DATE)
    private Date startDate;

    @DatabaseField(canBeNull = false, dataType = DataType.DATE, columnName = COLUMN_END_DATE)
    private Date endDate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
