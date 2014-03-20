package com.sheepdog.frontend.beans.templates;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import javax.faces.event.ValueChangeEvent;

import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.FileTreeComposite;
import com.sheepdog.business.domain.entities.Revision;
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

	private Revision selectedRev;

	private Long lastRev;

	// private List<Revision> revisions = new ArrayList<>(0);

	// private List<SelectItem> revisions = new ArrayList<>();
	private Map<String, Revision> revisions = new LinkedHashMap<>();

	// private Map<String, Revision> revisions = new TreeMap<>();

	private File file = null;

	private String fileName = "";

	public void loadContent() {
		if (file == null) {
			return;
		}

		if (file.isDir()) {
			content = "This is directory.";
			return;
		}

		if (revisions.isEmpty()) {
			loadRevisions();
		}

		int revision = 0;
		if (selectedRev != null) {
			revision = selectedRev.getRevisionNo();
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

	}

	private void loadRevisions() {
		Collection<Revision> fileRevisions = new TreeSet<>();

		try {
			fileRevisions.addAll(revService.getRevisionsByFile(loginManager.getCurrentUser(), file));// TODO
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
				revisions.put(String.valueOf(r.getRevisionNo()), r);
			}

		}


	}

	public void clearContentDT(SelectEvent se) {

		content = "Loading content...\nPlease wait...";

		Entry entry = (Entry) se.getObject();

		file = (File) entry.getKey();

		fileName = file.getName();

		revisions.clear();

	}

	public void clearContentTT(NodeSelectEvent se) {

		content = "Loading content...\nPlease wait...";

		FileTreeComposite ftc = (FileTreeComposite) se.getTreeNode().getData();
		file = ftc.getFile();

		fileName = file.getName();

		revisions.clear();

	}

	public void loadContentRev(ValueChangeEvent event) {
		content = "Loading content...\nPlease wait...";

		loadContent();

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

	public Revision getSelectedRev() {
		return selectedRev;
	}

	public void setSelectedRev(Revision selectedRev) {
		this.selectedRev = selectedRev;
	}

	public Map<String, Revision> getRevisions() {

		return revisions;
	}

	public void setRevisions(Map<String, Revision> revisions) {
		this.revisions = revisions;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
