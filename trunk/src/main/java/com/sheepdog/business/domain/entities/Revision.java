package com.sheepdog.business.domain.entities;

import java.util.Date;

public class Revision {

	private long revision_no;

	private String author;

	private String comment;

	private Date date;

	public Revision(long revision_no, String author, String comment, Date date) {
		super();
		this.revision_no = revision_no;
		this.author = author;
		this.comment = comment;
		this.date = date;
	}

	public long getRevision_no() {
		return revision_no;
	}

	public void setRevision_no(long revision_no) {
		this.revision_no = revision_no;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
