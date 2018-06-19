package com.spontivly.chat.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import static com.spontivly.chat.data.TestUtilities.getConstantNameByStringValue;
import static com.spontivly.chat.data.TestUtilities.getStaticIntegerField;
import static com.spontivly.chat.data.TestUtilities.getStaticStringField;
import static com.spontivly.chat.data.TestUtilities.studentReadableClassNotFound;
import static com.spontivly.chat.data.TestUtilities.studentReadableNoSuchField;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

/**
 * Used to test the database we use in Spontivly to cache chat data. Within these tests, we
 * test the following:
 * <p>
 * <p>
 * 1) Creation of the database with proper table(s)
 * 2) Insertion of single record into our weather table
 * 3) When a record is already stored in the weather table with a particular date, a new record
 * with the same date will overwrite that record.
 * 4) Verify that NON NULL constraints are working properly on record inserts
 * 5) Verify auto increment is working with the ID
 * 6) Test the onUpgrade functionality of the WeatherDbHelper
 */
@RunWith(AndroidJUnit4.class)
public class TestChatDatabase {
    /*
     * Context used to perform operations on the database and create WeatherDbHelpers.
     */
    private final Context context = InstrumentationRegistry.getTargetContext();

    private static final String packageName = "com.spontivly.chat";
    private static final String dataPackageName = packageName + ".data";

    private Class weatherEntryClass;
    private Class weatherDbHelperClass;
    private static final String weatherContractName = ".WeatherContract";
    private static final String weatherEntryName = weatherContractName + "$WeatherEntry";
    private static final String weatherDbHelperName = ".WeatherDbHelper";

    private SQLiteDatabase database;
    private SQLiteOpenHelper dbHelper;


    /**
     * This method tests that our database contains all of the tables that we think it should
     * contain. Although in our case, we just have one table that we expect should be added
     * <p>
     * {@link com.spontivly.chat.data.ChatContract.EventChatEntry#TABLE_NAME}.
     * <p>
     * Despite only needing to check one table name in Sunshine, we set this method up so that
     * you can use it in other apps to test databases with more than one table.
     */
    @Test
    public void testCreateDb() {
        /*
         * Will contain the name of every table in our database. Even though in our case, we only
         * have only table, in many cases, there are multiple tables. Because of that, we are
         * showing you how to test that a database with multiple tables was created properly.
         */
        final HashSet<String> tableNameHashSet = new HashSet<>();

        /* Here, we add the name of our only table in this particular database */
        tableNameHashSet.add(REFLECTED_TABLE_NAME);
        /* Students, here is where you would add any other table names if you had them */
//        tableNameHashSet.add(MyAwesomeSuperCoolTableName);
//        tableNameHashSet.add(MyOtherCoolTableNameThatContainsOtherCoolData);

        /* We think the database is open, let's verify that here */
        String databaseIsNotOpen = "The database should be open and isn't";
        assertEquals(databaseIsNotOpen,
                true,
                database.isOpen());

        /* This Cursor will contain the names of each table in our database */
        Cursor tableNameCursor = database.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table'",
                null);

        /*
         * If tableNameCursor.moveToFirst returns false from this query, it means the database
         * wasn't created properly. In actuality, it means that your database contains no tables.
         */
        String errorInCreatingDatabase =
                "Error: This means that the database has not been created correctly";
        assertTrue(errorInCreatingDatabase,
                tableNameCursor.moveToFirst());

        /*
         * tableNameCursor contains the name of each table in this database. Here, we loop over
         * each table that was ACTUALLY created in the database and remove it from the
         * tableNameHashSet to keep track of the fact that was added. At the end of this loop, we
         * should have removed every table name that we thought we should have in our database.
         * If the tableNameHashSet isn't empty after this loop, there was a table that wasn't
         * created properly.
         */
        do {
            tableNameHashSet.remove(tableNameCursor.getString(0));
        } while (tableNameCursor.moveToNext());

        /* If this fails, it means that your database doesn't contain the expected table(s) */
        assertTrue("Error: Your database was created without the expected tables.",
                tableNameHashSet.isEmpty());

        /* Always close the cursor when you are finished with it */
        tableNameCursor.close();
    }

    /**
     * This method tests inserting a single record into an empty table from a brand new database.
     * It will fail for the following reasons:
     * <p>
     * 1) Problem creating the database
     * 2) A value of -1 for the ID of a single, inserted record
     * 3) An empty cursor returned from query on the weather table
     * 4) Actual values of weather data not matching the values from TestUtilities
     */
    @Test
    public void testInsertSingleRecordIntoWeatherTable() {

        /* Obtain weather values from TestUtilities */
        ContentValues testWeatherValues = TestUtilities.createTestChatContentValues();

        /* Insert ContentValues into database and get a row ID back */
        long weatherRowId = database.insert(
                REFLECTED_TABLE_NAME,
                null,
                testWeatherValues);

        /* If the insert fails, database.insert returns -1 */
        int valueOfIdIfInsertFails = -1;
        String insertFailed = "Unable to insert into the database";
        assertNotSame(insertFailed,
                valueOfIdIfInsertFails,
                weatherRowId);

        /*
         * Query the database and receive a Cursor. A Cursor is the primary way to interact with
         * a database in Android.
         */
        Cursor weatherCursor = database.query(
                /* Name of table on which to perform the query */
                REFLECTED_TABLE_NAME,
                /* Columns; leaving this null returns every column in the table */
                null,
                /* Optional specification for columns in the "where" clause above */
                null,
                /* Values for "where" clause */
                null,
                /* Columns to group by */
                null,
                /* Columns to filter by row groups */
                null,
                /* Sort order to return in Cursor */
                null);

        /* Cursor.moveToFirst will return false if there are no records returned from your query */
        String emptyQueryError = "Error: No Records returned from weather query";
        assertTrue(emptyQueryError,
                weatherCursor.moveToFirst());

        /* Verify that the returned results match the expected results */
        String expectedWeatherDidntMatchActual =
                "Expected weather values didn't match actual values.";
        TestUtilities.validateCurrentRecord(expectedWeatherDidntMatchActual,
                weatherCursor,
                testWeatherValues);

        /*
         * Since before every method annotated with the @Test annotation, the database is
         * deleted, we can assume in this method that there should only be one record in our
         * Weather table because we inserted it. If there is more than one record, an issue has
         * occurred.
         */
        assertFalse("Error: More than one record returned from weather query",
                weatherCursor.moveToNext());

        /* Close cursor */
        weatherCursor.close();
    }

}
