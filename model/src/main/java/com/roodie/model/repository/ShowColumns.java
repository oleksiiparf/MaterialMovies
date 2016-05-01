package com.roodie.model.repository;

import android.content.ContentValues;
import android.database.Cursor;

import com.roodie.model.sqlite.Column;
import com.roodie.model.sqlite.DataType;
import com.roodie.model.sqlite.Reference;

/**
 * Created by Roodie on 13.03.2016.
 */
public enum ShowColumns implements Column {
    ID("_id",DataType.TEXT, null,Boolean.FALSE, Boolean.TRUE),
    TITLE("title", DataType.TEXT, null, Boolean.TRUE, Boolean.FALSE),
    POSTER_PATH("poster_path", DataType.TEXT, null, Boolean.TRUE, Boolean.FALSE),
    BACKDROP_PATH("backdrop_path", DataType.TEXT, null, Boolean.TRUE, Boolean.FALSE),
    OVERVIEW("overview", DataType.TEXT, null, Boolean.TRUE, Boolean.FALSE),
    VOTE_AVERAGE("vote_average", DataType.REAL, null, Boolean.TRUE, Boolean.FALSE),
    VOTE_COUNT("vote_count", DataType.INTEGER, null, Boolean.TRUE, Boolean.FALSE),
    RELEASE_DATE("release_time", DataType.LONG, null, Boolean.TRUE, Boolean.FALSE),
    IS_WATCHED("is_watched", DataType.BOOLEAN, null, Boolean.TRUE, Boolean.FALSE);



    private String columnName;
    private DataType dataType;
    private String extraQualifier;
    private Boolean optional;
    private Boolean unique;

    ShowColumns(String columnName, DataType dataType, String extraQualifier, Boolean optional, Boolean unique) {
        this.columnName = columnName;
        this.dataType = dataType;
        this.extraQualifier = extraQualifier;
        this.optional = optional;
        this.unique = unique;
    }

    @Override
    public <T> void addValue(ContentValues values, T value) {
        this.dataType.writeValue(values, this.columnName, value);

    }

    @Override
    public <E> E readValue(Cursor cursor) {
        return this.dataType.readValue(cursor, this.columnName);
    }

    @Override
    public DataType getDataType() {
        return this.dataType;
    }

    @Override
    public String getColumnName() {
        return this.columnName;
    }

    @Override
    public String getExtraQualifier() {
        return this.extraQualifier;
    }

    @Override
    public Boolean isOptional() {
        return this.optional;
    }

    @Override
    public Boolean isUnique() {
        return this.unique;
    }

    @Override
    public Reference getReference() {
        return null;
    }
}