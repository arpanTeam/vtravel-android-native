package com.vistara.traveler.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.vistara.traveler.model.NotifictionBean;

import java.util.ArrayList;


public class DatabaseAdapter {

	public SQLiteDatabase database;
	private Context mContext;
	public static DatabaseHelper dh;

	public DatabaseAdapter(Context context)
	{
		mContext = context;
		if( dh == null)
			dh = new DatabaseHelper(context);
	}

/*	public DatabaseAdapter()
	{
		if( mContext != appInterface.mContext)
			dh = null;


		if( dh == null) {
			mContext = appInterface.mContext;
			if(appInterface.mContext == null)
				mContext = Signup_Custo.m_context;
			dh = new DatabaseHelper(mContext);
		}
	}*/

	public void open(boolean writable) throws Exception{

		if( dh == null) {
			//mContext = appInterface.mContext;
			dh = new DatabaseHelper(mContext);
		}


		if(writable)
			database = dh.getWritableDatabase();
		else
			database = dh.getReadableDatabase();


	}


	// insert scratch card detail
	public void inserNotifiDetail(String id, String message, String title,
										String date, String image, String link) {
		database = dh.getWritableDatabase();

		ContentValues cValue = new ContentValues();
		cValue.put("server_add_id", id);
		cValue.put("title", title);
		cValue.put("message", message);
		cValue.put("date", date);
		cValue.put("image_url", image);
		cValue.put("link", link);

		String sql ="SELECT * from notification_data where server_add_id='"+id+"'";
		Cursor mCur = database.rawQuery(sql, null);
		int cnt=mCur.getCount();
		if(cnt==0)
		{
			long ret = database.insert("notification_data", null, cValue);
			if(ret>1) {
				int i = 0;
			}
		}
		//database.close();
		if( database != null && database.isOpen())
			database.close();



		if (database != null && database.isOpen())
			database.close();
	}


	public ArrayList<NotifictionBean> getNotifi(){
		Cursor mCur=null;
		int cnt=0;
		String sql="";
		ArrayList<NotifictionBean> data=new ArrayList<NotifictionBean>();
		int state = 1;
		String id,  message,  title,
				 date,  image,  link;
		int productId, queryId;
		try
		{
			open(false);

				sql ="SELECT * from  notification_data";


			mCur = database.rawQuery(sql, null);
			if (mCur!=null)
			{
				if (mCur.moveToFirst()) {
					do {


						id=mCur.getString(mCur.getColumnIndex("server_add_id"));
						message=mCur.getString(mCur.getColumnIndex("title"));
						title=mCur.getString(mCur.getColumnIndex("message"));
						date=mCur.getString(mCur.getColumnIndex("date"));
						image=mCur.getString(mCur.getColumnIndex("image_url"));
						link=mCur.getString(mCur.getColumnIndex("link"));

						data.add(new NotifictionBean(id, title, message, date, image, link));

					} while (mCur.moveToNext());
				}
			}
			mCur.close();
			database.close();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data;
	}



}//end of class
