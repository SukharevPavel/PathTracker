package ru.sukharev.pathtracker.utils.orm;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * That class implements ORM representation of all point, where user was.
 */
@DatabaseTable(tableName = "points")
public class MapPoint {

    public final static String COLUMN_X = "loc_lat";
    public final static String COLUMN_Y = "loc_long";
    public final static String COLUMN_DATE = "date";

    public double getLatitude() {
        return lattitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getTime() {
        return time;
    }

    public MapPoint(double loc_x, double loc_y, long time){
        this.lattitude = loc_x;

        this.longitude = loc_y;
        this.time = time;

    }

    public void setPath(MapPath path){
        this.path = path;
    }

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true)
    MapPath path;

    @DatabaseField(canBeNull = false, dataType = DataType.DOUBLE, columnName = COLUMN_X)
    private double lattitude;

    @DatabaseField(canBeNull = false, dataType = DataType.DOUBLE, columnName = COLUMN_Y)
    private double longitude;

    @DatabaseField(canBeNull = false, dataType = DataType.LONG, columnName = COLUMN_DATE)
    private long time;



}
