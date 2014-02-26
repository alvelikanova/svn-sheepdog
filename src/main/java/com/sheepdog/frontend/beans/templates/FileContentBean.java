package com.sheepdog.frontend.beans.templates;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;


import org.primefaces.event.NodeSelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.FileTreeComposite;
import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.RepositoryAuthenticationExceptoin;
import com.sheepdog.business.services.svn.SVNFileService;
import com.sheepdog.business.services.svn.SVNRevisionService;

@Component(value = "fileContentBean")
@Scope("session")
public class FileContentBean {

	@Autowired
	private SVNFileService fileService;

	@Autowired
	private SVNRevisionService revService;

	private String content;

	private Integer selectedRev;

	private Long lastRev;

	// private List<Revision> revisions = new ArrayList<>(0);

	private Set<Integer> revisions = new TreeSet<>();

	private File file;

	// TODO set parameter User object of authenticated user

	public void select(NodeSelectEvent se) {

		FileTreeComposite ftc = (FileTreeComposite) se.getTreeNode().getData();

		file = ftc.getFile();

		if (file.isDir()) {
			content = "This is directory.";
		} else {
			loadRevisions();
			loadContent();
		}
	}

	public void loadContent() {

		int revision = -1;
		if (selectedRev != null) {
			revision = selectedRev;
		}

		try {
			content = fileService.getFileContent(User.getUpdateUser(), file, revision);// TODO
																						// ACTUAL
																						// USER
		} catch (InvalidParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryAuthenticationExceptoin e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// @Async
	private void loadRevisions() {
		Collection<Revision> fileRevisions = new LinkedList<>();

		try {
			fileRevisions.addAll(revService.getRevisionsByFile(User.getUpdateUser(), file));// TODO
		} catch (RepositoryAuthenticationExceptoin e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		revisions.clear();

		if (fileRevisions != null) {
			for (Revision r : fileRevisions) {
				revisions.add(r.getRevisionNo());
			}

		}

	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setFileService(SVNFileService fileService) {
		this.fileService = fileService;
	}

	public Long getLastRev() {
		return lastRev;
	}

	public void setLastRev(Long lastRev) {
		this.lastRev = lastRev;
	}

	public Integer getSelectedRev() {
		return selectedRev;
	}

	public void setSelectedRev(Integer selectedRev) {
		this.selectedRev = selectedRev;
	}

	public Set<Integer> getRevisions() {
		return revisions;
	}

	public void setRevisions(Set<Integer> revisions) {
		this.revisions = revisions;
	}
}
