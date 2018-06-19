package com.spontivly.chat.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;

import com.spontivly.chat.utilities.ChatDateUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.spontivly.chat.data.ChatContract.EventChatEntry.COLUMN_MESSAGE;
import static com.spontivly.chat.data.ChatContract.EventChatEntry.COLUMN_EVENT_ID;
import static com.spontivly.chat.data.ChatContract.EventChatEntry.COLUMN_SENDER_ID;
import static com.spontivly.chat.data.ChatContract.EventChatEntry.COLUMN_TIMESTAMP;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * These are functions and some test data to make it easier to test your database and Content
 * Provider.
 */
class TestUtilities {

    /* October 1st, 2016 at midnight, GMT time */
    static final long DATE_NORMALIZED = 1475280000000L;

    static final int BULK_INSERT_RECORDS_TO_INSERT = 10;

    /**
     * Ensures there is a non empty cursor and validates the cursor's data by checking it against
     * a set of expected values. This method will then close the cursor.
     *
     * @param error          Message when an error occurs
     * @param valueCursor    The Cursor containing the actual values received from an arbitrary query
     * @param expectedValues The values we expect to receive in valueCursor
     */
    static void validateThenCloseCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertNotNull(
                "This cursor is null. Did you make sure to register your ContentProvider in the manifest?",
                valueCursor);

        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    /**
     * This method iterates through a set of expected values and makes various assertions that
     * will pass if our app is functioning properly.
     *
     * @param error          Message when an error occurs
     * @param valueCursor    The Cursor containing the actual values received from an arbitrary query
     * @param expectedValues The values we expect to receive in valueCursor
     */
    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();

        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int index = valueCursor.getColumnIndex(columnName);

            /* Test to see if the column is contained within the cursor */
            String columnNotFoundError = "Column '" + columnName + "' not found. " + error;
            assertFalse(columnNotFoundError, index == -1);

            /* Test to see if the expected value equals the actual value (from the Cursor) */
            String expectedValue = entry.getValue().toString();
            String actualValue = valueCursor.getString(index);

            String valuesDontMatchError = "Actual value '" + actualValue
                    + "' did not match the expected value '" + expectedValue + "'. "
                    + error;

            assertEquals(valuesDontMatchError,
                    expectedValue,
                    actualValue);
        }
    }

    /**
     * Used as a convenience method to return a singleton instance of ContentValues to populate
     * our database or insert using our ContentProvider.
     *
     * @return ContentValues that can be inserted into our ContentProvider or weather.db
     */
    static ContentValues createTestChatContentValues() {

        ContentValues testChatValues = new ContentValues();

        testChatValues.put(COLUMN_TIMESTAMP, DATE_NORMALIZED);
        testChatValues.put(COLUMN_EVENT_ID, 1);
        testChatValues.put(COLUMN_SENDER_ID, 1);
        testChatValues.put(COLUMN_MESSAGE, "Spontivly Super Event!");

        return testChatValues;
    }

    /**
     * Used as a convenience method to return a singleton instance of an array of ContentValues to
     * populate our database or insert using our ContentProvider's bulk insert method.
     *
     * @return Array of ContentValues that can be inserted into our ContentProvider or weather.db
     */
    static ContentValues[] createBulkInsertTestWeatherValues() {

        ContentValues[] bulkTestWeatherValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        long testDate = TestUtilities.DATE_NORMALIZED;
        long normalizedTestDate = ChatDateUtils.normalizeDate(testDate);

        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {

            normalizedTestDate += ChatDateUtils.DAY_IN_MILLIS;

            ContentValues testChatValues = new ContentValues();

            testChatValues.put(COLUMN_TIMESTAMP, normalizedTestDate);
            testChatValues.put(COLUMN_EVENT_ID, 1);
            testChatValues.put(COLUMN_SENDER_ID, (1+i));
            testChatValues.put(COLUMN_MESSAGE, "Spontivly Super Event #" + i);

            bulkTestWeatherValues[i] = testChatValues;
        }

        return bulkTestWeatherValues;
    }

    static String getConstantNameByStringValue(Class klass, String value)  {
        for (Field f : klass.getDeclaredFields()) {
            int modifiers = f.getModifiers();
            Class<?> type = f.getType();
            boolean isPublicStaticFinalString = Modifier.isStatic(modifiers)
                    && Modifier.isFinal(modifiers)
                    && Modifier.isPublic(modifiers)
                    && type.isAssignableFrom(String.class);

            if (isPublicStaticFinalString) {
                String fieldName = f.getName();
                try {
                    String fieldValue = (String) klass.getDeclaredField(fieldName).get(null);
                    if (fieldValue.equals(value)) return fieldName;
                } catch (IllegalAccessException e) {
                    return null;
                } catch (NoSuchFieldException e) {
                    return null;
                }
            }
        }

        return null;
    }

    static String getStaticStringField(Class clazz, String variableName)
            throws NoSuchFieldException, IllegalAccessException {
        Field stringField = clazz.getDeclaredField(variableName);
        stringField.setAccessible(true);
        String value = (String) stringField.get(null);
        return value;
    }

    static Integer getStaticIntegerField(Class clazz, String variableName)
            throws NoSuchFieldException, IllegalAccessException {
        Field intField = clazz.getDeclaredField(variableName);
        intField.setAccessible(true);
        Integer value = (Integer) intField.get(null);
        return value;
    }

    static String studentReadableClassNotFound(ClassNotFoundException e) {
        String message = e.getMessage();
        int indexBeforeSimpleClassName = message.lastIndexOf('.');
        String simpleClassNameThatIsMissing = message.substring(indexBeforeSimpleClassName + 1);
        simpleClassNameThatIsMissing = simpleClassNameThatIsMissing.replaceAll("\\$", ".");
        String fullClassNotFoundReadableMessage = "Couldn't find the class "
                + simpleClassNameThatIsMissing
                + ".\nPlease make sure you've created that class and followed the TODOs.";
        return fullClassNotFoundReadableMessage;
    }

    static String studentReadableNoSuchField(NoSuchFieldException e) {
        String message = e.getMessage();

        Pattern p = Pattern.compile("No field (\\w*) in class L.*/(\\w*\\$?\\w*);");

        Matcher m = p.matcher(message);

        if (m.find()) {
            String missingFieldName = m.group(1);
            String classForField = m.group(2).replaceAll("\\$", ".");
            String fieldNotFoundReadableMessage = "Couldn't find "
                    + missingFieldName + " in class " + classForField + "."
                    + "\nPlease make sure you've declared that field and followed the TODOs.";
            return fieldNotFoundReadableMessage;
        } else {
            return e.getMessage();
        }
    }
}