package com.sheepdog.business.services.svn.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.text.StrBuilder;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.InvalidURLException;
import com.sheepdog.business.exceptions.RepositoryAuthenticationExceptoin;
import com.sheepdog.business.services.svn.SVNProjectFacade;
import com.sheepdog.business.services.svn.SVNRevisionService;

public class SVNRevisionServiceTest {

	public static final Logger LOG = (Logger) LoggerFactory.getLogger(SVNRevisionServiceTest.class);

	// @Autowired
	// private SVNProjectFacade projectFacade;;

	public static void main(String[] args) {

		SVNRevisionServiceTest test = new SVNRevisionServiceTest();

		test.getAllRevisions();

	}

	public void getAllRevisions() {
		SVNProjectFacade projectFacade = new SVNProjectFacadeImpl(new SVNRepositoryManager());

		Project project = new Project();
		project.setUrl("https://svn-sheepdog.googlecode.com/svn/trunk/");
		project.setName("svn_test");

		File file = new File();
		file.setProject(project);
		file.setPath("src/main/resources/liquibase/versions/initial/changelog_00.xml");

		User user = new User();
		user.setLogin("ivan.spread@gmail.com");
		user.setPassword("fc9uy8NM5dK8");
		user.setProject(project);

		try {
			projectFacade.addSVNProjectConnection(user);

		} catch (InvalidURLException e1) {
			StrBuilder sb = new StrBuilder("InvalidURLException by URL :");
			sb.append(e1.getUrl());
			LOG.warn(sb.toString());
		} catch (IllegalArgumentException e) {
			LOG.info(e.getMessage());
		} catch (RepositoryAuthenticationExceptoin e) {
			LOG.info("User authentication error: " + e.getUser().getLogin());
		} catch (IOException e) {
			LOG.info(e.getMessage());
		}

		long time = System.currentTimeMillis();

		// SVNRevisionService revService = new SVNRevisionServiceImpl(provider);

		SVNRevisionService revService = new SVNRevisionServiceImpl(projectFacade);
		Set<Revision> revisions = new HashSet<Revision>();
		try {
			revisions = revService.getRevisions(user, 0, -1);
		} catch (IllegalArgumentException e) {
			LOG.info(e.getMessage());
		} catch (RepositoryAuthenticationExceptoin e) {
			LOG.info("User authentication error: " + e.getUser().getLogin());
		} catch (IOException e) {
			LOG.info(e.getMessage());
		}

		LOG.info("\n \n          List of all revisions: \n\n");

		for (Revision r : revisions) {
			StringBuilder sb = new StringBuilder();
			sb.append(r.getRevisionNo());
			sb.append(" author: ");
			sb.append(r.getAuthor());
			sb.append(" message: ");
			sb.append(r.getComment());
			sb.append(" date: ");
			sb.append(r.getDate());

			LOG.info(sb.toString());
		}

		LOG.info("Time getAllRevision: " + (System.currentTimeMillis() - time));

		// ////////////////////////////////////////////////////////////////////////////////////////////////

		Set<Revision> revisions2 = new HashSet<Revision>();
		try {
			revisions2 = revService.getRevisionsByFile(user, file);
		} catch (IllegalArgumentException e) {
			LOG.info(e.getMessage());
		} catch (RepositoryAuthenticationExceptoin e) {
			LOG.info("User authentication error: " + e.getUser().getLogin());
		} catch (IOException e) {
			LOG.info(e.getMessage());
		}

		LOG.info("\n \n          REVISIONS BY FILE  \n \n");

		for (Revision r : revisions2) {
			StringBuilder sb = new StringBuilder();
			sb.append(r.getRevisionNo());
			sb.append(" author: ");
			sb.append(r.getAuthor());
			sb.append(" message: ");
			sb.append(r.getComment());
			sb.append(" date: ");
			sb.append(r.getDate());

			LOG.info(sb.toString());
		}

		Revision revision = new Revision();

		try {
			revision = revService.getLastRevision(user);

		} catch (IllegalArgumentException e) {
			LOG.info(e.getMessage());
		} catch (RepositoryAuthenticationExceptoin e) {
			LOG.info("User authentication error: " + e.getUser().getLogin());
		} catch (IOException e) {
			LOG.info(e.getMessage());
		}

		LOG.info("\n \n          LAST REVISION   \n \n");

		StringBuilder sb = new StringBuilder();
		sb.append(revision.getRevisionNo());
		sb.append(" author: ");
		sb.append(revision.getAuthor());
		sb.append(" message: ");
		sb.append(revision.getComment());
		sb.append(" date: ");
		sb.append(revision.getDate());

		LOG.info(sb.toString());

	}
}
