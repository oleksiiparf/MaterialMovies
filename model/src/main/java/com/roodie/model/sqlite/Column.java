package com.roodie.model.sqlite;

import android.content.ContentValues;
import android.database.Cursor;


/**
 * Created by Roodie on 11.03.2016.
 */
public interface Column {

    /**
     * Default id column name.
     */
    String ID = "_id";

    /**
     * Default parent id column name.
     */
    String PARENT_ID = "parentId";

    /**
     * Default dtype column name.
     */
    String DTYPE = "dtype";

    /**
     * Default syncStatus column name.
     */
    String SYNC_STATUS = "syncStatus";

    /**
     * Default value column name.
     */
    String VALUE = "value";

    /**
     * Primary key constraint
     */
    String PRIMARY_KEY = "PRIMARY KEY";

    /**
     * Primary key constraint with autoincrement
     */
    String PRIMARY_KEY_AUTOINCREMENT = "PRIMARY KEY ON CONFLICT REPLACE AUTOINCREMENT";

    /**
     * Adds a the value given value to {@link ContentValues} according column definition.
     *
     * @param values to add the value.
     * @param value to add.
     */
    <T> void addValue(ContentValues values, T value);

    /**
     * Reads the value from {@link Cursor} according column definition.
     *
     * @param cursor to get the data.
     * @return the value.
     */
    <E> E readValue(Cursor cursor);

    /**
     * @return the dataType
     */
    DataType getDataType();

    /**
     * @return the columnName
     */
    String getColumnName();

    /**
     * @return extra qualifiers for the column, like "PRIMARY KEY" or "AUTOINCREMENT".
     */
    String getExtraQualifier();

    /**
     * @return the optional
     */
    Boolean isOptional();

    /**
     * @return true if the column should be included in unique constraint.
     */
    Boolean isUnique();

    /**
     * Returns a reference if the column contains a foreign key and should be added to reference constraints, otherwise
     * returns null.
     *
     * @return the reference.
     */
    Reference getReference();
}