package com.pshetye.typeanything;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyNote {
	long _id;
	String pNote;
	String pDate;
	static public SimpleDateFormat NoteDateFormat;
	
	public MyNote() {
		if (NoteDateFormat == null)
			NoteDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		pDate = NoteDateFormat.format(new Date());
	}
	
	public MyNote(long _id, String pNote) {
		if (NoteDateFormat == null)
			NoteDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		this._id = _id;
		this.pNote = pNote;
		this.pDate = NoteDateFormat.format(new Date());
	}
	
	public MyNote(long _id, String pNote, String pDate) {
		if (NoteDateFormat == null)
			NoteDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		this._id = _id;
		this.pNote = pNote;
		this.pDate = pDate;
	}
	
	public MyNote(String pNote) {
		if (NoteDateFormat == null)
			NoteDateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		this.pNote = pNote;
		this.pDate = NoteDateFormat.format(new Date());
	}
	
	public long getID() {
		return this._id;
	}
	
	public String getNote() {
		return this.pNote;
	}
	
	public String getDate() {
		return this.pDate;
	}
	
	public void setID(long _id) {
		this._id = _id;
	}
	
	public void setPNote(String pNote) {
		this.pNote = pNote;
	}
	
	public void setDate(String pDate) {
		this.pDate = pDate;
	}
}