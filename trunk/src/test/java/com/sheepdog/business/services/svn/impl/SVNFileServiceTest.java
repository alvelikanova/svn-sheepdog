package com.sheepdog.business.services.svn.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

	public static final Logger LOG = (Logger) LoggerFactory
			.getLogger(SVNRevisionServiceTest.class);

	public static void main(String[] args) {
		SVNFileServiceTest test = new SVNFileServiceTest();

		test.allTests();

	}

	public void allTests() {
		SVNProjectFacade projectFacade = new SVNProjectFacadeImpl(
				new SVNRepositoryManager());

		Project project = new Project();
		project.setUrl("https://svn-sheepdog.googlecode.com/svn/trunk/");
		project.setName("svn_test");

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

		LOG.info("Time init SVNRepository: "
				+ (System.currentTimeMillis() - time));

		// testGetAllFiles(provider, project, user);
		//
		// testGetFileByRevision(provider, project, user);

		testGetFilesByCreator(projectFacade, project, user);

	}

	private void testGetAllFiles(SVNProjectFacade projectFacade,
			Project project, User user) {

		long time = System.currentTimeMillis();

		SVNFileService fileService = new SVNFileServiceImpl(projectFacade,
				new SVNRevisionServiceImpl(null));

		List<File> files = new ArrayList<>();

		try {
			files = fileService.getAllFiles(project);
		} catch (SVNException e) {
			LOG.error(e.toString());
		}

		for (File f : files) {
			LOG.info(f.getQualifiedName());
		}

		LOG.info("Time print repo tree: " + (System.currentTimeMillis() - time));

	}

	private void testGetFileByRevision(SVNProjectFacade projectFacade,
			Project project, User user) {

		SVNFileService fileService = new SVNFileServiceImpl(projectFacade,
				new SVNRevisionServiceImpl(projectFacade));

		long time = System.currentTimeMillis();

		Map<File, String> files2 = new HashMap<File, String>();

		files2 = fileService.getFilesByRevision(project, new Revision(project, 7, null,
				null, null));

		for (File f : files2.keySet()) {
			LOG.info(f.getName());
		}

		LOG.info("Time getFilesByRevision: "
				+ (System.currentTimeMillis() - time));

	}

	private void testGetFilesByCreator(SVNProjectFacade projectFacade,
			Project project, User user) {

		SVNFileService fileService = new SVNFileServiceImpl(projectFacade,
				new SVNRevisionServiceImpl(projectFacade));

		long time = System.currentTimeMillis();

		Set<File> files = new java.util.HashSet<>();

		try {
			files = fileService.getFilesByCreator(project, user);
		} catch (InvalidURLException e) {
			LOG.error("InvalidURLException : " + e.getUrl());
		} catch (SVNException e) {
			LOG.error(e.toString());
		}

		for (File f : files) {
			LOG.info(f.getName());
		}

		LOG.info("Time getFilesByCreator: "
				+ (System.currentTimeMillis() - time));
	}

}
