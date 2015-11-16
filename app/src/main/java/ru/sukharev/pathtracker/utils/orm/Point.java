package ru.sukharev.pathtracker.utils.orm;

import android.support.annotation.NonNull;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * That class implements ORM representation of all point, where user was.
 */
@DatabaseTable(tableName = "points")
public class Point {

    public final static String COLUMN_X = "loc_x";
    public final static String COLUMN_Y = "loc_y";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true)
    Path path;

    @DatabaseField(canBeNull = false, dataType = DataType.FLOAT, columnName = COLUMN_X)
    private float x;

    @DatabaseField(canBeNull = false, dataType = DataType.FLOAT, columnName = COLUMN_Y)
    private float y;



}
