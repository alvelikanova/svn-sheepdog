package com.sheepdog.frontend.beans.pages;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private RevisionManagementService rms;

	@Autowired
	private SVNFileService svnFileService;

	private List<Revision> dbRevisions = new ArrayList<>(0);

	private Map<File, TypeOfFileChanges> revisionFiles = new HashMap<>(0);

	public void loadRevisionFiles(ToggleEvent event) {
		User user = User.getUpdateUser();// TODO

		try {
			revisionFiles = svnFileService.getFilesByRevision(user, (Revision) event.getData());
		} catch (IllegalArgumentException e) {
			LOG.info("Failed to get files from repository. " + e.getMessage());
			// TODO feedback for exceptions
		} catch (RepositoryAuthenticationExceptoin e) {
			LOG.warn("Failed to get all files. Authentication is failed.");
			feedback("Failed to get all files. Authentication is failed.");
		} catch (IOException e) {
			LOG.warn("Failed to get all files. " + e.getMessage());
			feedback("Failed to get all files. " + e.getMessage());
		}

	}

	private void feedback(String string) {
		// TODO Auto-generated method stub

	}

	public List<Map.Entry<File, TypeOfFileChanges>> getFiles() {

		Set<Map.Entry<File, TypeOfFileChanges>> fileSet = revisionFiles.entrySet();
		return new ArrayList<Map.Entry<File, TypeOfFileChanges>>(fileSet);

	}

	public List<Revision> getDbRevisions() {
		//dbRevisions = rms.getAllRevisions(); TODO

		return dbRevisions;
	}

	public void setDbRevisions(List<Revision> dbRevisions) {
		this.dbRevisions = dbRevisions;
	}

	public Map<File, TypeOfFileChanges> getRevisionFiles() {
		return revisionFiles;
	}

	public void setRevisionFiles(Map<File, TypeOfFileChanges> revisionFiles) {
		this.revisionFiles = revisionFiles;
	}

}
