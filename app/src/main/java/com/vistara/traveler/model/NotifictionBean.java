package com.vistara.traveler.model;

public class NotifictionBean {

	String id;
	public String message;
	public String date;
	public String image;
	public String url;
	public String title;
	
	public NotifictionBean(String id, String title, String message, String date, String image, String url)
	{
		this.id=id;
		this.message=message;
		this.date=date;
		this.image=image;
		this.url=url;
		this.title=title;
	}

}
