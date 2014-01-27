package com.sheepdog.business.services.svn.impl;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.security.auth.RefreshFailedException;

import org.apache.commons.lang3.text.StrBuilder;
import org.slf4j.LoggerFactory;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.FileTreeComposite;
import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.InvalidURLException;
import com.sheepdog.business.exceptions.RepositoryAuthenticationExceptoin;
import com.sheepdog.business.services.svn.SVNFileService;
import com.sheepdog.business.services.svn.SVNProjectFacade;
import com.sheepdog.business.services.svn.impl.TypeOfFileChanges;

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
		
//		project.setUrl("https://svn-kit-test.googlecode.com/svn/trunk/");
//		project.setName("svn-kit-test");

		User user = new User();
		user.setLogin("ivan.spread@gmail.com");
		user.setPassword("fc9uy8NM5dK8");
		user.setProject(project);
		user.setIsAdmin(true);

		long time = System.currentTimeMillis();

		try {

			projectFacade.createMainConnection();

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
		} catch (RefreshFailedException e) {
			LOG.info(e.getMessage());
		}
		LOG.info("Time init SVNRepository: " + (System.currentTimeMillis() - time));

		testGetAllFiles(projectFacade, project, user);

		testGetFileByRevision(projectFacade, project, user);

		testGetFilesByCreator(projectFacade, project, user);

		testGetFileContent(projectFacade, project, user);

	}

	private void testGetAllFiles(SVNProjectFacade projectFacade, Project project, User user) {

		SVNFileService fileService = new SVNFileServiceImpl(projectFacade);
		LOG.info("\n\n GET ALL FILES \n\n");

		long time = System.currentTimeMillis();

	
		FileTreeComposite root = null;
		try {
			root = fileService.getAllFiles(user);
		} catch (IllegalArgumentException e) {
			LOG.info(e.getMessage());
		} catch (RepositoryAuthenticationExceptoin e) {
			LOG.info("User authentication error: " + e.getUser().getLogin());
		} catch (IOException e) {
			LOG.info(e.getMessage());
		}

		
		printComposite(root);
		

		LOG.info("Time print repo tree: " + (System.currentTimeMillis() - time));

	}
	
	private void printComposite(FileTreeComposite ftc){
		StringBuilder sb = new StringBuilder();
		sb.append(ftc.getFile().getQualifiedName());
		sb.append(";   name = ");
		sb.append(ftc.getFile().getName());
		sb.append(";   last rev = ");
		sb.append(ftc.getProperty().get("svn:entry:committed-rev"));
		sb.append(";   commiter = ");
		sb.append(ftc.getProperty().get("svn:entry:last-author"));
		sb.append(";   date = ");
		sb.append(ftc.getProperty().get("svn:entry:committed-date"));
		
		LOG.info(sb.toString());
		
		if (!ftc.getChilds().isEmpty()){
			for(FileTreeComposite ftcChild : ftc.getChilds()){
				printComposite(ftcChild);
			}
		}
		
	}

	private void testGetFileByRevision(SVNProjectFacade projectFacade, Project project, User user) {

		SVNFileService fileService = new SVNFileServiceImpl(projectFacade);

		LOG.info("\n\n GET FILE BY REVISION \n\n");
		long time = System.currentTimeMillis();

		Map<File, TypeOfFileChanges> files2 = new HashMap<File, TypeOfFileChanges>();

		try {
			files2 = fileService.getFilesByRevision(user, new Revision(project, 20, null, null, null));
		} catch (IllegalArgumentException e) {
			LOG.info(e.getMessage());
		} catch (RepositoryAuthenticationExceptoin e) {
			LOG.info("User authentication error: " + e.getUser().getLogin());
		} catch (IOException e) {
			LOG.info(e.getMessage());
		}

		for (File f : files2.keySet()) {
			LOG.info(f.getName() + " ---- " + files2.get(f).toString());
		}

		LOG.info("Time getFilesByRevision: " + (System.currentTimeMillis() - time));

	}

	private void testGetFilesByCreator(SVNProjectFacade projectFacade, Project project, User user) {

		SVNFileService fileService = new SVNFileServiceImpl(projectFacade);

		long time = 0;

		Set<File> files = new java.util.HashSet<>();

		try {
			Set<Revision> revision = new SVNRevisionServiceImpl(projectFacade).getRevisions(user, 0, -1);

			LOG.info("\n\n GET FILE BY CREATOR \n\n");

			time = System.currentTimeMillis();

			files = fileService.getFilesByCreator(user, revision);
		} catch (IllegalArgumentException e) {
			LOG.info(e.getMessage());
		} catch (RepositoryAuthenticationExceptoin e) {
			LOG.info("User authentication error: " + e.getUser().getLogin());
		} catch (IOException e) {
			LOG.info(e.getMessage());
		}

		for (File f : files) {
			LOG.info(f.getName());
		}

		LOG.info("Time getFilesByCreator: " + (System.currentTimeMillis() - time));
	}

	private void testGetFileContent(SVNProjectFacade projectFacade, Project project, User user) {
		SVNFileService fileService = new SVNFileServiceImpl(projectFacade);

		File file = new File();
		file.setPath("src/main/webapp/protected/hello.xhtml");

		LOG.info("\n\n  File content   \n \n ");

		long time = System.currentTimeMillis();
		try {
			LOG.info(fileService.getFileContent(user, file, -1));
		} catch (InvalidParameterException e) {
			LOG.error(e.toString());
		} catch (IllegalArgumentException e) {
			LOG.info(e.getMessage());
		} catch (RepositoryAuthenticationExceptoin e) {
			LOG.info("User authentication error: " + e.getUser().getLogin());
		} catch (IOException e) {
			LOG.info(e.getMessage());
		}
		LOG.info("" + (System.currentTimeMillis() - time));

	}
}
