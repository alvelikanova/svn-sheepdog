package com.sheepdog.business.services.svn.impl;

import java.util.HashMap;
import java.util.Map;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.InvalidURLException;
import com.sheepdog.business.services.svn.SVNProvider;

public class SVNProviderImpl implements SVNProvider

{

	private Map<String, SVNRepository> svnConnections = new HashMap<>();

	@Override
	public boolean addSVNProject(Project project, User user)
			throws SVNException, InvalidURLException {

		if (svnConnections.get(project.getUrl()) != null)
			return true;

		SVNRepository repo = createSVNProject(project, user);

		try {
			if (repo.getLatestRevision() < 1)
				return false;
		} catch (SVNException | NullPointerException e) {
			return false;
		}

		svnConnections.put(project.getUrl(), repo);

		return true;
	}

	private SVNRepository createSVNProject(Project project, User user)
			throws SVNException, InvalidURLException {

		connectionSetup(project.getUrl());

		SVNURL repositoryURL = SVNURL.parseURIEncoded(project.getUrl());

		SVNRepository repository = SVNRepositoryFactory.create(repositoryURL);

		ISVNAuthenticationManager authManager = SVNWCUtil
				.createDefaultAuthenticationManager(user.getName(),
						user.getPassword());

		repository.setAuthenticationManager(authManager);

		return repository;
	}

	private void connectionSetup(String url) {
		if (url == null || url.length() < 6)
			new InvalidURLException(url);

		switch (url.substring(0, 4)) {
		case "svn:":
			SVNRepositoryFactoryImpl.setup();
			return;
		case "http":
			DAVRepositoryFactory.setup();
			return;
		case "file":
			FSRepositoryFactory.setup();
			return;

		default:
			new InvalidURLException(url);
		}
	}

	@Override
	public SVNRepository getRepository(Project project)
			throws InvalidURLException {

		SVNRepository repo = svnConnections.get(project.getUrl());

		if (repo == null)
			new InvalidURLException(project.getUrl());

		return repo;
	}
}
