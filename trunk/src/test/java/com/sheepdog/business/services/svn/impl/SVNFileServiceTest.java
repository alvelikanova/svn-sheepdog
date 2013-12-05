package com.sheepdog.business.services.svn.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.text.StrBuilder;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.InvalidURLException;
import com.sheepdog.business.services.svn.SVNFileService;
import com.sheepdog.business.services.svn.SVNProvider;

import ch.qos.logback.classic.Logger;

public class SVNFileServiceTest {

	public static final Logger LOG = (Logger) LoggerFactory
			.getLogger(SVNRevisionServiceTest.class);

	public static void main(String[] args) {
		SVNFileServiceTest test = new SVNFileServiceTest();

		test.getAllFiles();

	}

	public void getAllFiles() {
		SVNProvider provider = new SVNProviderImpl();

		Project project = new Project();
		project.setUrl("https://svn-sheepdog.googlecode.com/svn/trunk/");
		project.setName("svn_test");

		User user = new User();
		user.setName("ivan.spread@gmail.com");
		user.setPassword("fc9uy8NM5dK8");

		try {
			provider.addSVNProject(project, user);
		} catch (InvalidURLException e1) {
			StrBuilder sb = new StrBuilder("InvalidURLException by URL :");
			sb.append(e1.getUrl());
			LOG.warn(sb.toString());
		} catch (SVNException e1) {
			LOG.error(e1.toString());
		}

		SVNFileService fileService = new SVNFileServiceImpl(provider);

		List<File> files = new ArrayList<>();

		try {
			files = fileService.getAllFiles(project);
		} catch (SVNException e) {
			LOG.error(e.toString());
		}

		for (File f : files) {
			LOG.info(f.getQualifiedName());
		}

	}
}
