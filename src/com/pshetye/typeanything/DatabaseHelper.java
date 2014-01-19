package com.pshetye.typeanything;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 2;
	 
    // Database Name
    private static final String DATABASE_NAME = "MyNotes";
 
    // Contacts table name
    private static final String MYNOTES = "MyNote";
 
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NOTE = "pNote";
    private static final String KEY_DATE = "pDate";
    
    private static int notes;

	public DatabaseHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + MYNOTES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NOTE + " TEXT, " 
				+ KEY_DATE + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		System.out.print("Inside onUpgrade in DatabaseHelper");
		
		if (oldVersion < newVersion) {
			if (newVersion == 2) {
				db.execSQL("ALTER TABLE " + MYNOTES + " ADD COLUMN " + KEY_DATE
						+ " TEXT");
				if (MyNote.NoteDateFormat == null)
					MyNote.NoteDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
				ContentValues values = new ContentValues();

			    values.put(KEY_DATE, MyNote.NoteDateFormat.format(new Date()));
			 
			    // updating row
			    db.update(MYNOTES, values, "", null);
			}
		}
	}

	// Adding new contact
	public void addNote(MyNote note) {
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    values.put(KEY_NOTE, note.getNote()); // Contact Name
	    values.put(KEY_ID, note.getID()); // Contact Name
	    values.put(KEY_DATE, note.getDate()); // Contact Name
	 
	    // Inserting Row
	    db.insert(MYNOTES, null, values);
	    db.close(); // Closing database connection		
	}
	 
	// Getting single contact
	public MyNote getNote(long _id) {
		SQLiteDatabase db = this.getReadableDatabase();
		 
	    Cursor cursor = db.query(MYNOTES, new String[] { KEY_ID,
	            KEY_NOTE }, KEY_ID + "=?",
	            new String[] { String.valueOf(_id) }, null, null, null, null);
	    if (cursor != null)
	        cursor.moveToFirst();
	 
	    MyNote note = new MyNote(Integer.parseInt(cursor.getString(0)), cursor.getString(1));
	    // return Note
	    return note;
	}
	 
	// Getting All Contacts
	public List<MyNote> getAllNotes() {
		SQLiteDatabase db = this.getReadableDatabase();
		List<MyNote> noteList = new ArrayList<MyNote>();
		String selectQuery = "SELECT  * FROM " + MYNOTES;
		
		Cursor cursor = db.rawQuery(selectQuery, null);        
		
		// looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	            MyNote note = new MyNote();
	            note.setID(Integer.parseInt(cursor.getString(0)));
	            note.setPNote(cursor.getString(1));
	            note.setDate(cursor.getString(2));
	            // Adding contact to list
	            noteList.add(note);
	        } while (cursor.moveToNext());
	    }
	 
	    // return contact list
	    return noteList;
	}
	
	// Getting All Contacts
	public List<String> getAllStringNotes() {
		SQLiteDatabase db = this.getReadableDatabase();
		List<String> noteList = new ArrayList<String>();
		String selectQuery = "SELECT  * FROM " + MYNOTES; // + " ORDER BY " + KEY_ID + " DESC";
		
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		notes = cursor.getCount();
		
		// looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	            // Adding contact to list
	            noteList.add(cursor.getString(1));
	        } while (cursor.moveToNext());
	    }
	    // return contact list
	    return noteList;
	}
	 
	// Getting contacts Count
	public int getNotesCount() {
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM " + MYNOTES;
		
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		return cursor.getCount();
	}
	
	// Updating single contact
	public int updateNote(MyNote note) {
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    values.put(KEY_NOTE, note.getNote());
	    values.put(KEY_DATE, MyNote.NoteDateFormat.format(new Date()));
	 
	    // updating row
	    return db.update(MYNOTES, values, KEY_ID + " = ?",
	            new String[] { String.valueOf(note.getID()) });
	}
	 
	// Deleting single contact
	public void deleteNote(MyNote note) {
		SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(MYNOTES, KEY_ID + " = ?",
	            new String[] { String.valueOf(note.getID()) });
	    if (note.getID() != 0 && note.getID() < notes) {
	    	
	    }
	    db.close();
	}
}
