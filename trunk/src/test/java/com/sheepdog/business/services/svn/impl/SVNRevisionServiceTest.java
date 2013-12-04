package com.sheepdog.business.services.svn.impl;

import java.util.Set;

import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNException;

import ch.qos.logback.classic.Logger;

import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.InvalidURLException;
import com.sheepdog.business.services.svn.SVNProvider;
import com.sheepdog.business.services.svn.SVNRevisionService;

public class SVNRevisionServiceTest {
	
	public static final Logger LOG = (Logger) LoggerFactory.getLogger(SVNRevisionServiceTest.class);

	public static void main(String[] args) {
		SVNProvider provider = new SVNProviderImpl();

		Project project = new Project();
		project.setUrl("https://svn-sheepdog.googlecode.com/svn/trunk/");
		project.setName("svn_test");

		User user = new User();
		user.setName("ivan.spread@gmail.com");
		user.setPassword("fc9uy8NM5dK8");

		try {
			provider.addSVNProject(project, user);
		} catch (InvalidURLException | SVNException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SVNRevisionService revService = new SVNRevisionServiceImpl(provider);

		Set<Revision> revisions = revService.getRevisions(project, 0, -1);

		for (Revision r : revisions)
			LOG.warn(r.getRevision_no() + " " + r.getAuthor());
	}
}
