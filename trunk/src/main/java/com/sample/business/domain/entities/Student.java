package com.sample.business.domain.entities;

public class Student extends PersistentEntity<Integer> {

	private static final long serialVersionUID = -4913340722982408879L;

	private String studentNo;
	private User user;

	public Student() {
		user = new User();
	}

	public String getStudentNo() {
		return studentNo;
	}

	public void setStudentNo(String studentNo) {
		this.studentNo = studentNo;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
