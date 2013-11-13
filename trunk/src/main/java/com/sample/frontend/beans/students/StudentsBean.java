package com.sample.frontend.beans.students;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sample.business.domain.entities.Student;
import com.sample.dal.exceptions.DaoException;
import com.sample.dal.providers.StudentsDataProvider;
import com.sample.frontend.utils.Dialogs;
import com.sample.utils.annotations.SessionScope;
import com.sample.utils.frontend.beans.CommonBean;

@Component
@SessionScope
public class StudentsBean extends CommonBean {

	private static final long serialVersionUID = 3298253739257885379L;
	private static final Logger LOG = LoggerFactory.getLogger(StudentsBean.class);

	@Autowired
	private StudentsDataProvider dataProvider;

	private Student selectedStudent;
	private Student newStudent;

	public void createStudent(ActionEvent event) {
		newStudent = new Student();

		LOG.info("createStudent()");
		showDialog(Dialogs.NEW_STUDENT_DIALOG);
	}

	public void saveNewStudent() {
		LOG.trace("saveNewStudent()");

		try {
			dataProvider.create(newStudent);
		} catch (DaoException ex) {
			// TBD
		}
	}

	/**
	 * Getters and setters
	 */

	public Student getSelectedStudent() {
		return selectedStudent;
	}

	public void setSelectedStudent(Student selectedStudent) {
		this.selectedStudent = selectedStudent;
	}

	public Student getNewStudent() {
		return newStudent;
	}

	public void setNewStudent(Student newStudent) {
		this.newStudent = newStudent;
	}

}
