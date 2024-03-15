package com.vistara.traveler.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


public class DatabaseHelper extends SQLiteOpenHelper {
	
	public static final String DATABASE_NAME = "traveller";
	public static Context mcontext;

	private static final int DATABASE_VERSION = 1;
	
	private static final String notification_data= "create table if not exists "
			+ "notification_data"
			+ " ("
			+ BaseColumns._ID
			+ " INTEGER primary key autoincrement, "
			
			+ "server_add_id"
			+ " text, "

			+ "title"
			+ " text, "
			
			+ "message"
			+ " text, "

			+ "date"
			+ " text, "
			
			+ "image_url"
			+ " text, "
			
			+ "link"
			+ " text "

			+ ");";



	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION );
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		System.out.println("............onCreate");
		createTables(db);
		System.out.println("............onCreate");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS notification_data");

		onCreate(db);
	}


	private void createTables(SQLiteDatabase database) {
		try {
			database.execSQL(notification_data);
		} catch (SQLException e) {
			e.printStackTrace();

		}
		
	}

}
