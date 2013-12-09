package com.sheepdog.business.services.svn.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.text.StrBuilder;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.tmatesoft.svn.core.SVNException;

import ch.qos.logback.classic.Logger;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.InvalidURLException;
import com.sheepdog.business.services.svn.SVNProjectFacade;
import com.sheepdog.business.services.svn.SVNRevisionService;

public class SVNRevisionServiceTest {

	public static final Logger LOG = (Logger) LoggerFactory
			.getLogger(SVNRevisionServiceTest.class);

	// @Autowired
	// private SVNProjectFacade projectFacade;;

	public static void main(String[] args) {

		SVNRevisionServiceTest test = new SVNRevisionServiceTest();

		test.getAllRevisions();

	}

	/**
	 * Get all revisions. Get revision, which containing required file.
	 */
	public void getAllRevisions() {
		 SVNProjectFacade projectFacade = new SVNProjectFacadeImpl(new SVNRepositoryManager());

		Project project = new Project();
		project.setUrl("https://svn-sheepdog.googlecode.com/svn/trunk/");
		project.setName("svn_test");

		User user = new User();
		user.setName("ivan.spread@gmail.com");
		user.setPassword("fc9uy8NM5dK8");

		try {
			projectFacade.addSVNProject(project, user);
		} catch (InvalidURLException e1) {
			StrBuilder sb = new StrBuilder("InvalidURLException by URL :");
			sb.append(e1.getUrl());
			LOG.warn(sb.toString());
		} catch (SVNException e1) {
			LOG.error(e1.toString());
		}

		long time = System.currentTimeMillis();

		// SVNRevisionService revService = new SVNRevisionServiceImpl(provider);

		SVNRevisionService revService = new SVNRevisionServiceImpl(projectFacade);
		Set<Revision> revisions = new HashSet<Revision>();
		try {
			revisions = revService.getRevisions(project, 0, -1);
		} catch (InvalidURLException e) {
			StrBuilder sb = new StrBuilder("InvalidURLException by URL :");
			sb.append(e.getUrl());
			LOG.warn(sb.toString());
		} catch (SVNException e) {
			LOG.error(e.toString());
		}

		LOG.info("\n \n          List of all revisions: \n\n");

		for (Revision r : revisions) {
			StringBuilder sb = new StringBuilder();
			sb.append(r.getRevision_no());
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
			revisions2 = revService
					.getRevisionsByFile(
							project,
							new File(
									"",
									"src/main/resources/liquibase/versions/initial/changelog_00.xml",
									"", true));
		} catch (InvalidURLException e) {
			StrBuilder sb = new StrBuilder("InvalidURLException by URL :");
			sb.append(e.getUrl());
			LOG.warn(sb.toString());
		} catch (SVNException e) {
			LOG.error(e.toString());
		}

		LOG.info("\n \n          REVISIONS BY FILE  \n \n");

		for (Revision r : revisions2) {
			StringBuilder sb = new StringBuilder();
			sb.append(r.getRevision_no());
			sb.append(" author: ");
			sb.append(r.getAuthor());
			sb.append(" message: ");
			sb.append(r.getComment());
			sb.append(" date: ");
			sb.append(r.getDate());

			LOG.info(sb.toString());
		}

	}
	// public SVNProvider getProvider() {
	// return provider;
	// }
	//
	// public void setProvider(SVNProvider provider) {
	// this.provider = provider;
	// }
}
