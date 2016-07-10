package com.phobos.system.heavykey;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;

import java.util.ArrayList;
// hanldes the sqllite database
public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 5;
    private static final String DATABASE_NAME = "log.db";
    public static final String TABLE_CONTACTS = "contacts";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NUMBER = "phoneNumber";
    public static final String COLUMN_SENDPAD = "sendpad";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_RECEIVEPAD = "recievepad";
    public static final String COLUMN_MESSAGES = "texts";


    //constructor
    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    //creates databases if they do not exist
    @Override
    public void onCreate(SQLiteDatabase db) {
        //makes the table for contacts
        String query = "CREATE TABLE " + TABLE_CONTACTS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NUMBER + " TEXT NOT NULL UNIQUE, " +
                COLUMN_NAME + " TEXT NOT NULL UNIQUE, " +
                COLUMN_SENDPAD + " BLOB, " +
                COLUMN_RECEIVEPAD + " BLOB, " +
                COLUMN_MESSAGES + " TEXT " + ");";
        db.execSQL(query);
    }


    // update code
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    //removes all contacts and message logs
    public void erase() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    //adds a contact
    public void addContact(Contact contact) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NUMBER, contact.get_phoneNumber());
        values.put(COLUMN_SENDPAD, contact.getSendPad());
        values.put(COLUMN_RECEIVEPAD, contact.getRecievePad());
        values.put(COLUMN_MESSAGES, contact.getMessages());
        values.put(COLUMN_NAME,contact.getName());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }


    //removes a single contact
    public void deleteContact(String contactName) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CONTACTS + " WHERE " + COLUMN_NAME + "=\"" + contactName + "\"");
        db.close();
    }


    //gets list of contacts and their messages
    public String extractContactData(String table) {
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + table + " WHERE 1";

        //Cursor points to a location in your results
        Cursor c = db.rawQuery(query, null);
        //Move to the first row in your results
        c.moveToFirst();

        //Position after the last row means the end of the results (safe to use again)
        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex(COLUMN_NUMBER)) != null) {
                dbString += c.getString(c.getColumnIndex(COLUMN_NUMBER)) + "|\n" + EncryptionHandler.getHeader(c.getBlob(c.getColumnIndex(COLUMN_RECEIVEPAD))) +"|\n" + c.getString(c.getColumnIndex(COLUMN_MESSAGES));
                dbString += "\n";
            }
            c.moveToNext();
        }
        c.close();
        db.close();
        System.out.println("WORKED");

        return dbString;
    }

    //gives pads to contacts. Will be updated to give different pads
    public void setSendPad(byte[] pad, String name) {
        SQLiteDatabase db = getWritableDatabase();
        String where = COLUMN_NAME + "=\"" + name + "\"";
        ContentValues content = new ContentValues();
        content.put(COLUMN_SENDPAD, pad);
        db.update(TABLE_CONTACTS, content, where, null);
        db.close();
    }
    public void setReceivePad(byte[] pad, String name) {
        SQLiteDatabase db = getWritableDatabase();
        String where = COLUMN_NAME + "=\"" + name + "\"";
        ContentValues content = new ContentValues();
        content.put(COLUMN_RECEIVEPAD, pad);
        db.update(TABLE_CONTACTS, content, where, null);
        db.close();
    }
    public void wipeMessages( String name) {
        SQLiteDatabase db = getWritableDatabase();
        String where = COLUMN_NAME + "=\"" + name + "\"";
        ContentValues content = new ContentValues();
        content.put(COLUMN_MESSAGES, "");
        db.update(TABLE_CONTACTS, content, where, null);
        db.close();
    }

    // adds a message to the messages database for the user specified
    public void addMessage(String name, String message) {
        //makes the data sql friendly
       String processedMessage = message.replaceAll("\"","&quot");
        processedMessage =processedMessage.replaceAll("\'","&apos");
        SQLiteDatabase db = getWritableDatabase();
        String selectQuery = "SELECT " + COLUMN_MESSAGES + " FROM " + TABLE_CONTACTS + " WHERE " + COLUMN_NAME + "=?";
        Cursor c = db.rawQuery(selectQuery, new String[]{name});
        String output = null;
        if (c.moveToFirst()) {
            output = c.getString(c.getColumnIndex(COLUMN_MESSAGES));
        }
        c.close();
        // adds new message
        output = output + "\n" + processedMessage;
        System.out.println(output);
        // puts value back
        db.execSQL("UPDATE " + TABLE_CONTACTS +
                " SET " + COLUMN_MESSAGES + " = '" + output +
                "' WHERE " + COLUMN_NAME + " = '" + name + "';");
        db.close();
    }
    // clears messages for a specific contact
    public void clearMessages(String name)
    {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE " + TABLE_CONTACTS +
                " SET " + COLUMN_MESSAGES + " = '" + "" +
                "' WHERE " + COLUMN_NAME + " = '" + name + "';");
        db.close();

    }


    //looks for presence of value in column
    public boolean entryExists(String table, String column, String Value) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c = db.rawQuery("SELECT 1 FROM" + column +
                " FROM " + table +
                " WHERE " + column + " =?", new String[]{Value});
        boolean exists = c.moveToFirst();
        c.close();
        db.close();
        System.out.println(exists);
        return exists;
    }

   // gets send pad. No moving parts.
    public byte[] getSendPad(String name) {
        SQLiteDatabase db = getWritableDatabase();
        String selectQuery = "SELECT " + COLUMN_SENDPAD + " FROM " + TABLE_CONTACTS + " WHERE " + COLUMN_NAME + "=?";
        Cursor c = db.rawQuery(selectQuery, new String[]{name});
        byte[] output = null;
        if (c.moveToFirst()) {
            output = c.getBlob(c.getColumnIndex(COLUMN_SENDPAD));
        }
        c.close();
        db.close();
        System.out.println(output);
        return output;

    }
    // gets receive pad. No moving parts.
    public byte[] getReceivePad(String name) {
        SQLiteDatabase db = getWritableDatabase();
        String selectQuery = "SELECT " + COLUMN_RECEIVEPAD + " FROM " + TABLE_CONTACTS + " WHERE " + COLUMN_NAME + "=?";
        Cursor c = db.rawQuery(selectQuery, new String[]{name});
        byte[] output = null;
        if (c.moveToFirst()) {
            output = c.getBlob(c.getColumnIndex(COLUMN_RECEIVEPAD));
        }
        c.close();
        db.close();

        System.out.println(output);
        return output;

    }

    // gets phone number from ID
    public String getNumber(int index) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c=db.rawQuery("SELECT * FROM " + TABLE_CONTACTS, null);
        c.moveToPosition(index);
        String output = c.getString(c.getColumnIndex(COLUMN_NUMBER));
        db.close();
        return output;


    }
    public String getName(int index) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c=db.rawQuery("SELECT * FROM " + TABLE_CONTACTS, null);
        c.moveToPosition(index);
        String output = c.getString(c.getColumnIndex(COLUMN_NAME));
        db.close();
        return output;


    }

    // gets messages
    public String[] getMessages(int index) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor c= db.rawQuery("SELECT * FROM " + TABLE_CONTACTS, null);
        c.moveToPosition(index);
        String messages = null;

         messages = c.getString(c.getColumnIndex(COLUMN_MESSAGES));

        if (messages.equals("")) {
            messages = "No messages yet :^)";
        }
        c.close();
        //   System.out.println( messages ;
        db.close();

        messages =messages.replaceAll("&quot","\"");
        messages =messages.replaceAll("&apos","\'");
        return messages.split("[\\r\\n]+");

    }


    //gets a list of contacts
    public String[] getContacts() {
        ArrayList<String> returnMsg = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CONTACTS + " WHERE 1", null);


        int contentpathColumn = c.getColumnIndex(COLUMN_NAME);


        if (c.moveToFirst()) {
            do {
                do {
                    String number = c.getString(contentpathColumn);
                    returnMsg.add(number);
                } while (c.moveToNext());
            } while (c.moveToNext());
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        if (db != null) {
            db.close();
        }
        String[] output = new String[returnMsg.size()];
        output = returnMsg.toArray(output);
        db.close();
        return output;
    }
    public String[] getNumbers() {
        ArrayList<String> returnMsg = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_CONTACTS + " WHERE 1", null);


        int contentpathColumn = c.getColumnIndex(COLUMN_NUMBER);


        if (c.moveToFirst()) {
            do {
                do {
                    String number = c.getString(contentpathColumn);
                    returnMsg.add(number);
                } while (c.moveToNext());
            } while (c.moveToNext());
        }
        if (c != null && !c.isClosed()) {
            c.close();
        }
        if (db != null) {
            db.close();
        }
        String[] output = new String[returnMsg.size()];
        output = returnMsg.toArray(output);
        db.close();
        return output;
    }
}
