package com.sample.frontend.utils;

import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;

/**
 * Holds widgetVar for all dialogs. At the moment such a hack is need to read
 * constants from EL.
 * 
 * @author tillias
 * 
 */

@ManagedBean
@ApplicationScoped
public class Dialogs {
	/**
	 * Java constants
	 */
	public static final String NEW_STUDENT_DIALOG = "newstdlg";

	/**
	 * EL constants
	 */
	public final String new_student_dialog = NEW_STUDENT_DIALOG;

	public String getNew_student_dialog() {
		return new_student_dialog;
	}
}
