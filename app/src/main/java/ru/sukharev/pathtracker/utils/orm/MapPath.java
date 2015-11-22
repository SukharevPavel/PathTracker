package ru.sukharev.pathtracker.utils.orm;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Collection;
import java.util.Date;

/**
 * That class implements ORM representation of paths, that user records.
 */

@DatabaseTable(tableName = MapPath.TABLE_NAME)
public class MapPath {

    public final static String TABLE_NAME = "paths";
    public final static String COLUMN_NAME = "name";
    public final static String COLUMN_START_DATE = "start";
    public final static String COLUMN_END_DATE = "end";

    public MapPath(String name, long start, long end){
        this.name = name;
        this.startTime = start;
        this.endTime = end;
    }

    @DatabaseField(id = true, canBeNull = false, dataType = DataType.STRING, columnName = COLUMN_NAME)
    private String name;

    @DatabaseField(canBeNull = false, dataType = DataType.LONG, columnName = COLUMN_START_DATE)
    private long startTime;

    @DatabaseField(canBeNull = false, dataType = DataType.LONG, columnName = COLUMN_END_DATE)
    private long endTime;

    @ForeignCollectionField(eager = true)
    private Collection<MapPoint> points;

    MapPath(){

    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }
}
