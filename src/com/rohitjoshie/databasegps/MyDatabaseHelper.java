package com.rohitjoshie.databasegps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "RohitDatabase";

	//Overriding the superclass
	public MyDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, 1);
	}

	//Creating the database
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL("CREATE TABLE contacts (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, phonenumber INTEGER);");
	}

	//if database changes
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS contacts");
		onCreate(db);
	}

	//Add phone no
	public void addContact(String name, int phonenumber) {
		ContentValues values = new ContentValues(1);
		values.put("name", name);
		values.put("phonenumber", phonenumber);
		getWritableDatabase().insert("contacts", "name", values);
	}

	//Get phone no.
	public Cursor getContacts() {
		Cursor cursor = getReadableDatabase().rawQuery("select * from contacts",
				null);
		return cursor;
	}
}