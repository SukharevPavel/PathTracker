package ru.sukharev.pathtracker.utils.orm;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * That class implements ORM representation of all point, where user was.
 */
@DatabaseTable(tableName = MapPoint.TABLE_NAME)
public class MapPoint {

    public final static String TABLE_NAME = "points";
    public final static String COLUMN_X = "loc_lat";
    public final static String COLUMN_Y = "loc_long";
    public final static String COLUMN_DATE = "date";
    public final static String COLUMN_SPEED = "speed";
    public final static String COLUMN_HAS_SPEED = "has_speed";
    @DatabaseField(foreign = true)
    MapPath path;
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(canBeNull = false, dataType = DataType.DOUBLE, columnName = COLUMN_X)
    private double lattitude;
    @DatabaseField(canBeNull = false, dataType = DataType.DOUBLE, columnName = COLUMN_Y)
    private double longitude;
    @DatabaseField(canBeNull = false, dataType = DataType.LONG, columnName = COLUMN_DATE)
    private long time;
    @DatabaseField(dataType = DataType.DOUBLE, columnName = COLUMN_SPEED)
    private double speed;
    @DatabaseField(dataType = DataType.BOOLEAN, columnName = COLUMN_HAS_SPEED)
    private boolean hasSpeed;

    public MapPoint(double loc_x, double loc_y, long time) {
        this.lattitude = loc_x;

        this.longitude = loc_y;
        this.time = time;

    }

    public MapPoint(Location loc) {
        this.lattitude = loc.getLatitude();

        this.longitude = loc.getLongitude();
        this.time = loc.getTime();
        if (loc.hasSpeed()) this.speed = loc.getSpeed();
    }

    MapPoint() {

    }

    public boolean isHasSpeed() {
        return hasSpeed;
    }

    public void setHasSpeed(boolean hasSpeed) {
        this.hasSpeed = hasSpeed;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public LatLng toLatLng() {
        return new LatLng(lattitude, longitude);
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MapPath getPath() {
        return path;
    }

    public void setPath(MapPath path) {
        this.path = path;
    }

    public double getLattitude() {
        return lattitude;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }
}
