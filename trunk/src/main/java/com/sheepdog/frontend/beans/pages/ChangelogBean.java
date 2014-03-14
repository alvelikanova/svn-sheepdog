package com.sheepdog.frontend.beans.pages;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.event.ToggleEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.RepositoryAuthenticationExceptoin;
import com.sheepdog.business.services.RevisionManagementService;
import com.sheepdog.business.services.svn.SVNFileService;
import com.sheepdog.business.services.svn.impl.TypeOfFileChanges;
import com.sheepdog.frontend.beans.templates.FeedbackBean;

@Component(value = "changelogBean")
@Scope("session")
public class ChangelogBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4000881840123490825L;

	/**
	 * Logger object.
	 */
	public static final Logger LOG = LoggerFactory.getLogger(ChangelogBean.class);

	@Autowired
	private FeedbackBean feedback;

	@Autowired
	private LoginManager lm;

	@Autowired
	private RevisionManagementService rms;

	@Autowired
	private SVNFileService svnFileService;

	@Autowired
	private TweetBean tweetBean;

	private List<Revision> dbRevisions = new ArrayList<>(0);

	private List<Map.Entry<File, TypeOfFileChanges>> revisionFiles = new ArrayList<>(0);

	private Map.Entry<File, TypeOfFileChanges> selectedFile = null;

	private void loadRevisionFiles(ToggleEvent event) {
		System.out.println("VIZOV");

		User user = lm.getCurrentUser();

		System.out.println("VIZOV" + user.getLogin());
		Revision r = (Revision) event.getData();
		System.out.println("NO REV " + r.getRevisionNo());

		revisionFiles.clear();

		try {
			revisionFiles.addAll(svnFileService.getFilesByRevision(user, (Revision) event.getData()).entrySet());
		} catch (IllegalArgumentException e) {
			LOG.info("Failed to get revision files from repository. " + e.getMessage());
			feedback.feedback(FacesMessage.SEVERITY_WARN, "Registration problem.",
					"Check your profile or contact to your administrator.");
		} catch (RepositoryAuthenticationExceptoin e) {
			LOG.warn("Failed to get all files. Authentication is failed.");
			feedback.feedback(FacesMessage.SEVERITY_ERROR, "Authentication is failed.",
					"Check your autentication info in profile page and Verify connection.");
		} catch (IOException e) {
			LOG.warn("Failed to get all files. " + e.getMessage());
			feedback.feedback(FacesMessage.SEVERITY_ERROR, "Connetion to repository is failed.",
					"Check your profile or contact to your administrator.");
		}

		for (Map.Entry<File, TypeOfFileChanges> entry : revisionFiles)
			System.out.println("File " + entry.getKey().getName());

	}

	public void loadFilesAndTweets(ToggleEvent event) {
		loadRevisionFiles(event);

		tweetBean.loadTweets((Revision) event.getData());

	}

	public List<Map.Entry<File, TypeOfFileChanges>> getFiles() {

		return revisionFiles;

	}

	public void loadRevisionFromDB() {
		Set<Revision> revSet = new TreeSet<>();
		revSet.addAll(rms.getAllRevisions());

		dbRevisions.clear();
		dbRevisions.addAll(revSet);

		RequestContext.getCurrentInstance().update("changelog_form:changelog");
	}

	public List<Revision> getDbRevisions() {
		return dbRevisions;
	}

	public void setDbRevisions(List<Revision> dbRevisions) {
		this.dbRevisions = dbRevisions;
	}

	public Map.Entry<File, TypeOfFileChanges> getSelectedFile() {
		return selectedFile;
	}

	public void setSelectedFile(Map.Entry<File, TypeOfFileChanges> selectedFile) {
		this.selectedFile = selectedFile;
	}

}
