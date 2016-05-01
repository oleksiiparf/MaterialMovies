package com.roodie.model.sqlite;

import android.content.ContentValues;
import android.database.Cursor;

import com.roodie.model.util.DateUtils;

import java.util.Date;

/**
 * Created by Roodie on 11.03.2016.
 */
public enum DataType {
    TEXT("TEXT") {

        @Override
        public <T> void writeValue(ContentValues values, String columnName, T value) {
            if (value != null) {
                values.put(columnName, (String)value);
            } else {
                values.putNull(columnName);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public String readValue(Cursor cursor, String columnName) {
            int columnIndex = cursor.getColumnIndex(columnName);
            if (cursor.isNull(columnIndex)) {
                return null;
            }
            return cursor.getString(columnIndex);
        }
    },
    INTEGER("INTEGER") {

        @Override
        public <T> void writeValue(ContentValues values, String columnName, T value) {
            if (value != null) {
                values.put(columnName, ((Number)value).intValue());
            } else {
                values.putNull(columnName);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public Integer readValue(Cursor cursor, String columnName) {
            int columnIndex = cursor.getColumnIndex(columnName);
            if (cursor.isNull(columnIndex)) {
                return null;
            }
            return cursor.getInt(columnIndex);
        }
    },
    LONG("INTEGER") {

        @Override
        public <T> void writeValue(ContentValues values, String columnName, T value) {
            if (value != null) {
                values.put(columnName, ((Number) value).longValue());
            } else {
                values.putNull(columnName);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public Long readValue(Cursor cursor, String columnName) {
            int columnIndex = cursor.getColumnIndex(columnName);
            if (cursor.isNull(columnIndex)) {
                return null;
            }
            return cursor.getLong(columnIndex);
        }
    },
    REAL("REAL") {

        @Override
        public <T> void writeValue(ContentValues values, String columnName, T value) {
            if (value != null) {
                values.put(columnName, ((Number) value).doubleValue());
            } else {
                values.putNull(columnName);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public Double readValue(Cursor cursor, String columnName) {
            int columnIndex = cursor.getColumnIndex(columnName);
            if (cursor.isNull(columnIndex)) {
                return null;
            }
            return cursor.getDouble(columnIndex);
        }
    },
    FLOAT("REAL") {

        @Override
        public <T> void writeValue(ContentValues values, String columnName, T value) {
            if (value != null) {
                values.put(columnName, ((Number) value).floatValue());
            } else {
                values.putNull(columnName);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public Float readValue(Cursor cursor, String columnName) {
            int columnIndex = cursor.getColumnIndex(columnName);
            if (cursor.isNull(columnIndex)) {
                return null;
            }
            return cursor.getFloat(columnIndex);
        }
    },
    BLOB("BLOB") {

        @Override
        public <T> void writeValue(ContentValues values, String columnName, T value) {
            if (value != null) {
                values.put(columnName, (byte[])value);
            } else {
                values.putNull(columnName);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public byte[] readValue(Cursor cursor, String columnName) {
            int columnIndex = cursor.getColumnIndex(columnName);
            if (cursor.isNull(columnIndex)) {
                return null;
            }
            return cursor.getBlob(columnIndex);
        }
    },
    BOOLEAN("INTEGER") {

        @Override
        public <T> void writeValue(ContentValues values, String columnName, T value) {
            if (value != null) {
                values.put(columnName, ((Boolean) value) ? 1 : 0);
            } else {
                values.putNull(columnName);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public Boolean readValue(Cursor cursor, String columnName) {
            int columnIndex = cursor.getColumnIndex(columnName);
            if (cursor.isNull(columnIndex)) {
                return null;
            }
            return (cursor.getInt(columnIndex) == 0 ? Boolean.FALSE : Boolean.TRUE);
        }
    },
    DATE("TEXT") {

        @Override
        public <T> void writeValue(ContentValues values, String columnName, T value) {
            if (value != null) {
                values.put(columnName, DateUtils.format((Date) value, "yyyy-MM-dd HH:mm:ss"));
            } else {
                values.putNull(columnName);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public Date readValue(Cursor cursor, String columnName) {
            int columnIndex = cursor.getColumnIndex(columnName);
            if (cursor.isNull(columnIndex)) {
                return null;
            }
            String date = cursor.getString(columnIndex);
            return DateUtils.parse(date, "yyyy-MM-dd HH:mm:ss");
        }

    },
    DATE_MILLISECONDS("TEXT") {

        @Override
        public <T> void writeValue(ContentValues values, String columnName, T value) {
            if (value != null) {
                values.put(columnName, DateUtils.format((Date)value, "yyyy-MM-dd HH:mm:ss.SSS"));
            } else {
                values.putNull(columnName);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public Date readValue(Cursor cursor, String columnName) {
            int columnIndex = cursor.getColumnIndex(columnName);
            if (cursor.isNull(columnIndex)) {
                return null;
            }
            String date = cursor.getString(columnIndex);
            return DateUtils.parse(date, "yyyy-MM-dd HH:mm:ss.SSS");
        }

    },
    DATE_TZ("TEXT") {

        @Override
        public <T> void writeValue(ContentValues values, String columnName, T value) {
            if (value != null) {
                values.put(columnName, DateUtils.format((Date)value, "yyyy-MM-dd HH:mm:ss Z"));
            } else {
                values.putNull(columnName);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public Date readValue(Cursor cursor, String columnName) {
            int columnIndex = cursor.getColumnIndex(columnName);
            if (cursor.isNull(columnIndex)) {
                return null;
            }
            String date = cursor.getString(columnIndex);
            return DateUtils.parse(date, "yyyy-MM-dd HH:mm:ss Z");
        }

    };

    private String type;

    DataType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public abstract <T> void writeValue(ContentValues values, String columnName, T value);

    public abstract <T> T readValue(Cursor cursor, String columnName);
}