package ru.sukharev.pathtracker.utils.orm;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * That class implements ORM representation of paths, that user records.
 */

@DatabaseTable(tableName = MapPath.TABLE_NAME)
public class MapPath {

    public final static String TABLE_NAME = "paths";
    public final static String COLUMN_NAME = "name";
    public final static String COLUMN_START_DATE = "start";
    public final static String COLUMN_END_DATE = "end";
    public final static String COLUMN_DISTANCE = "distance";
    public final static String COLUMN_AVG_SPEED = "avg_speed";
    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(canBeNull = false, dataType = DataType.STRING, columnName = COLUMN_NAME)
    private String name;
    @DatabaseField(canBeNull = false, dataType = DataType.LONG, columnName = COLUMN_START_DATE)
    private long startTime;
    @DatabaseField(canBeNull = false, dataType = DataType.LONG, columnName = COLUMN_END_DATE)
    private long endTime;
    @DatabaseField(dataType = DataType.DOUBLE, columnName = COLUMN_DISTANCE)
    private double distance;
    @DatabaseField(dataType = DataType.DOUBLE, columnName = COLUMN_AVG_SPEED)
    private double avgSpeed;


    @ForeignCollectionField
    private ForeignCollection<MapPoint> points;


    public MapPath(String name, long start, long end, double distance, double avgSpeed) {
        this.name = name;
        this.startTime = start;
        this.endTime = end;
        this.distance = distance;
        this.avgSpeed = avgSpeed;
    }

    MapPath(){

    }

    public ForeignCollection<MapPoint> getPoints() {
        return points;
    }

    public void setPoints(ForeignCollection<MapPoint> points) {
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(double avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof MapPath)) return false;
        return (id == ((MapPath) o).getId());
    }

}

