package com.opera.link.webapp;

import java.io.Serializable;

public class SpeedDial implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 0101011L;
	
	public SpeedDial() {
		// empty constructor
	}
	
	public SpeedDial(String title, String uri, byte[] thumbnail) {
		this.title =title;
		this.uri = uri;
		this.thumbnail = thumbnail;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public byte[] getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(byte[] thumbnail) {
		this.thumbnail = thumbnail;
	}
	
	private String title;
	private String uri;
	private byte[] thumbnail;
}
