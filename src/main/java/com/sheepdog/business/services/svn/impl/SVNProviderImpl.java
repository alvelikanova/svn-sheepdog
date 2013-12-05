package com.sheepdog.business.services.svn.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.InvalidURLException;
import com.sheepdog.business.services.svn.SVNProvider;

@Service("SVNProviderService")
@Scope("singleton")
public class SVNProviderImpl implements SVNProvider

{

	/**
	 * Map of URL string and SVNRepository object.
	 */
	private Map<String, SVNRepository> svnConnections = new ConcurrentHashMap<>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sheepdog.business.services.svn.SVNProvider#addSVNProject(com.sheepdog
	 * .business.domain.entities.Project,
	 * com.sheepdog.business.domain.entities.User)
	 */
	@Override
	public boolean addSVNProject(Project project, User user)
			throws SVNException, InvalidURLException {

		if (svnConnections.get(project.getUrl()) != null)
			return true;

		SVNRepository repo = createSVNProject(project, user);

		repo.testConnection();

		svnConnections.put(project.getUrl(), repo);

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sheepdog.business.services.svn.SVNProvider#getRepository(com.sheepdog
	 * .business.domain.entities.Project)
	 */
	@Override
	public SVNRepository getRepository(Project project)
			throws InvalidURLException {

		SVNRepository repo = svnConnections.get(project.getUrl());

		if (repo == null)
			new InvalidURLException(project.getUrl());

		return repo;
	}

	/**
	 * Create SVNRepository object and set ISVNAuthenticationManager object with
	 * user's name and password.
	 * 
	 * @param project
	 *            Project object containing URL of repository.
	 * @param user
	 *            User object containing authentication info.
	 * @return Configured SVNRepository object.
	 * @throws InvalidURLException
	 *             - if URL of repository is not correct or protocol is not
	 *             supported.
	 */
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

	/**
	 * Set up connection by protocol.
	 * 
	 * @param url
	 *            Repository URL.
	 */
	private void connectionSetup(String url) {
		if (url == null || url.length() < 6)
			new InvalidURLException(url);

		if (url.startsWith("http://") || url.startsWith("https://"))
			DAVRepositoryFactory.setup();

		if (url.startsWith("svn://") || url.startsWith("svn+"))
			DAVRepositoryFactory.setup();

		if (url.startsWith("file://"))
			DAVRepositoryFactory.setup();

		new InvalidURLException(url);

	}

}
