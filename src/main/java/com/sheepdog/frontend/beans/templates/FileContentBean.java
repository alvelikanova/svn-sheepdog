package com.sheepdog.frontend.beans.templates;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.model.SelectItem;

import org.primefaces.context.RequestContext;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
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
import com.sheepdog.frontend.beans.pages.LoginManager;

@Component(value = "fileContentBean")
@Scope("session")
public class FileContentBean {

	@Autowired
	private SVNFileService fileService;

	@Autowired
	private SVNRevisionService revService;

	@Autowired
	private LoginManager loginManager;

	private String content;

	private Integer selectedRev;

	private Long lastRev;

	// private List<Revision> revisions = new ArrayList<>(0);

	private Set<String> revisions = new TreeSet<>();

	private File file = null;

	// TODO set parameter User object of authenticated user

	public void select() {

		loadContent();

		RequestContext.getCurrentInstance().update("file_form:cont_dialog");
	}

	public void selectDT() {

		loadContent();

		RequestContext.getCurrentInstance().update("changelog_form:cont_dialog");

	}

	public void loadContent() {
		if (file == null) {
			return;
		}

		if (file.isDir()) {
			content = "This is directory.";
			return;
		}
		loadRevisions();

		int revision = 0;
		if (selectedRev != null) {
			revision = selectedRev;
		}
		if (revision == 0) {
			revision = -1;
		}

		try {
			content = fileService.getFileContent(loginManager.getCurrentUser(), file, revision);
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
		
		System.out.println("VYZOV LOAD CONENTA");

	}

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
				revisions.add(String.valueOf(r.getRevisionNo()));
			}

		}

	}

	public void clearContentDT(SelectEvent se) {

		content = "Loading content...";

		Entry entry = (Entry) se.getObject();

		file = (File) entry.getKey();

		System.out.println("FILE INSTALLED");

	}

	public void clearContentTT(NodeSelectEvent se) {

		content = "Loading content...";

		FileTreeComposite ftc = (FileTreeComposite) se.getTreeNode().getData();
		file = ftc.getFile();

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

	public Set<String> getRevisions() {
		return revisions;
	}

	public void setRevisions(Set<String> revisions) {
		this.revisions = revisions;
	}
}
