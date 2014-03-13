package com.sheepdog.business.domain.entities;

import java.util.Date;

public class Revision extends PersistentEntity<Integer> implements Comparable<Revision> {

	private static final long serialVersionUID = -9057200620775979080L;

	private Project project;

	private int revisionNo;
	private String author;
	private String comment;

	private Date date = new Date();// TODO ??????/

	public Revision() {
	}

	public Revision(Project project, int revisionNo, String author, String comment, Date date) {
		this.project = project;
		this.revisionNo = revisionNo;
		this.author = author;
		this.comment = comment;
		this.date = date;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public int getRevisionNo() {
		return revisionNo;
	}

	public void setRevisionNo(int revisionNo) {
		this.revisionNo = revisionNo;
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

	@Override
	public int compareTo(Revision o) {

		if (this == o) {
			return 0;
		}

		int compare = Integer.valueOf(this.revisionNo).compareTo(Integer.valueOf(o.getRevisionNo()));

		return compare;
	}

}
