package com.pshetye.typeanything;

public class MyNote {
	int _id;
	String pNote;
	
	public MyNote() {
	
	}
	
	public MyNote(int _id, String pNote) {
		this._id = _id;
		this.pNote = pNote;
	}
	
	public MyNote(String pNote) {
		this.pNote = pNote;
	}
	
	public int getID() {
		return this._id;
	}
	
	public String getNote() {
		return this.pNote;
	}
	
	public void setID(int _id) {
		this._id = _id;
	}
	
	public void setPNote(String pNote) {
		this.pNote = pNote;
	}
}