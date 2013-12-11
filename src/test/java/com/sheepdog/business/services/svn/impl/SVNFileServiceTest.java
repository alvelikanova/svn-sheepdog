package com.sheepdog.business.services.svn.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.text.StrBuilder;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.InvalidURLException;
import com.sheepdog.business.services.svn.SVNFileService;
import com.sheepdog.business.services.svn.SVNProjectFacade;

import ch.qos.logback.classic.Logger;

public class SVNFileServiceTest {

	public static final Logger LOG = (Logger) LoggerFactory.getLogger(SVNRevisionServiceTest.class);

	public static void main(String[] args) {
		SVNFileServiceTest test = new SVNFileServiceTest();

		test.allTests();

	}

	public void allTests() {
		SVNProjectFacade projectFacade = new SVNProjectFacadeImpl(new SVNRepositoryManager());

		Project project = new Project();
		project.setUrl("https://svn-sheepdog.googlecode.com/svn/trunk/");
		project.setName("sheepdog");

		User user = new User();
		user.setLogin("ivan.spread@gmail.com");
		user.setPassword("fc9uy8NM5dK8");

		long time = System.currentTimeMillis();

		try {
			projectFacade.addSVNProjectConnection(project, user);
		} catch (InvalidURLException e1) {
			StrBuilder sb = new StrBuilder("InvalidURLException by URL :");
			sb.append(e1.getUrl());
			LOG.warn(sb.toString());
		} catch (SVNException e1) {
			LOG.error(e1.toString());
		}

		LOG.info("Time init SVNRepository: " + (System.currentTimeMillis() - time));

		testGetAllFiles(projectFacade, project, user);

		testGetFileByRevision(projectFacade, project, user);

		testGetFilesByCreator(projectFacade, project, user);

	}

	private void testGetAllFiles(SVNProjectFacade projectFacade, Project project, User user) {

		long time = System.currentTimeMillis();

		SVNFileService fileService = new SVNFileServiceImpl(projectFacade);

		Set<File> files = new HashSet<>();
		try {
			files = fileService.getAllFiles(project);
		} catch (SVNException e) {
			LOG.error(e.toString());
		}

		for (File f : files) {
			if (f != null)
				LOG.info(f.getQualifiedName() + "   name = " + f.getName());
		}

		LOG.info("Time print repo tree: " + (System.currentTimeMillis() - time));

	}

	private void testGetFileByRevision(SVNProjectFacade projectFacade, Project project, User user) {

		SVNFileService fileService = new SVNFileServiceImpl(projectFacade);

		long time = System.currentTimeMillis();

		Map<File, Character> files2 = new HashMap<File, Character>();

		try {
			files2 = fileService.getFilesByRevision(project, new Revision(project, 7, null, null, null));
		} catch (InvalidURLException e) {
			LOG.error("InvalidURLException : " + e.getUrl());
		} catch (SVNException e) {
			LOG.error(e.toString());
		}

		for (File f : files2.keySet()) {
			LOG.info(f.getName());
		}

		LOG.info("Time getFilesByRevision: " + (System.currentTimeMillis() - time));

	}

	private void testGetFilesByCreator(SVNProjectFacade projectFacade, Project project, User user) {

		SVNFileService fileService = new SVNFileServiceImpl(projectFacade);

		long time = 0;

		Set<File> files = new java.util.HashSet<>();

		try {
			Set<Revision> revision = new SVNRevisionServiceImpl(projectFacade).getRevisions(project, 0, -1);

			time = System.currentTimeMillis();

			files = fileService.getFilesByCreator(project, user, revision);
		} catch (InvalidURLException e) {
			LOG.error("InvalidURLException : " + e.getUrl());
		} catch (SVNException e) {
			LOG.error(e.toString());
		}

		for (File f : files) {
			LOG.info(f.getName());
		}

		LOG.info("Time getFilesByCreator: " + (System.currentTimeMillis() - time));
	}
}
