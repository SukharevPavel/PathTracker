package ru.sukharev.pathtracker.utils.orm;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

/**
 * That class implements ORM representation of all point, where user was.
 */
@DatabaseTable(tableName = MapPoint.TABLE_NAME)
public class MapPoint {

    public final static String TABLE_NAME = "points";
    public final static String COLUMN_X = "loc_lat";
    public final static String COLUMN_Y = "loc_long";
    public final static String COLUMN_DATE = "date";

    public MapPoint(double loc_x, double loc_y, long time){
        this.lattitude = loc_x;

        this.longitude = loc_y;
        this.time = time;

    }

    MapPoint(){

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

    public void setPath(MapPath path){
        this.path = path;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getTime() {
        return time;
    }

    public int getId() {
        return id;
    }

    public MapPath getPath() {
        return path;
    }

    public double getLattitude() {
        return lattitude;
    }
}
