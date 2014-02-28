package com.sheepdog.initializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.security.auth.RefreshFailedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.InvalidURLException;
import com.sheepdog.business.exceptions.RepositoryAuthenticationExceptoin;
import com.sheepdog.business.services.ProjectManagementService;
import com.sheepdog.business.services.RevisionManagementService;
import com.sheepdog.business.services.UserManagementService;
import com.sheepdog.business.services.svn.SVNProjectFacade;
import com.sheepdog.business.services.svn.SVNRevisionService;

@Component
@Scope("singleton")
public class Initializer {

	/**
	 * Logger object.
	 */
	public static final Logger LOG = LoggerFactory.getLogger(Initializer.class);

	@Autowired
	ProjectManagementService projectService;

	@Autowired
	SVNProjectFacade projFacade;

	@Autowired
	UserManagementService userService;

	@Autowired
	RevisionManagementService rms;

	@Autowired
	SVNRevisionService svnRevisionService;

	@Value("${repository.url}")
	private String repoUrl;

	@Value("${repository.login}")
	private String repoLogin;

	@Value("${repository.password}")
	private String repoPass;

	@Value("${repository.project.name}")
	private String projectName;

	@Value("${reset.onstart}")
	private String reset;

	@PostConstruct
	public void startup() {

		dbCheck();

		createConnections();

		checkDBRevisions();

	}

	private void dbCheck() {
		Project mainProject = projectService.getCurrentProject();

		if (reset.equals("true")) {
			reloadProject(mainProject);
			cleanDB();
		}

		else if (mainProject == null || !mainProject.getName().equals(projectName)
				|| !mainProject.getUrl().equals(repoUrl)) {
			reloadProject(mainProject);
		}

	}

	private void reloadProject(Project mainProject) {
		if (mainProject != null) {
			// projectService.deleteProject(); TODO
		}
		Project newProject = new Project(projectName, repoUrl);

		// projectService.saveProject(newProject); TODO

		User.getUpdateUser().setProject(projectService.getCurrentProject());
	}

	private void createConnections() {
		try {
			projFacade.createMainConnection(repoUrl, repoLogin, repoPass);
		} catch (InvalidURLException e) {
			LOG.warn("Failed to create main connection. Url:" + e.getUrl() + "is invalid.");
		} catch (RepositoryAuthenticationExceptoin e) {
			LOG.warn("Failed to create main connection. Authentication is failed.");
		} catch (RefreshFailedException e) {
			LOG.warn("Failed to create main connection. Can't load properties.");
		} catch (IOException e) {
			LOG.warn("Failed to create main connection. " + e.getMessage());
		}

		List<User> dbUsers = new ArrayList<>(0);// userService.getAllUsers; TODO

		for (User u : dbUsers) {
			try {
				projFacade.addSVNProjectConnection(u);
			} catch (InvalidURLException e) {
				LOG.warn("Failed to create user connection. User:" + u.getLogin() + " Url:" + e.getUrl()
						+ "is invalid.");
			} catch (RepositoryAuthenticationExceptoin e) {
				LOG.warn("Failed to create user connection. User:" + u.getLogin() + "  Authentication is failed.");
			} catch (IOException e) {
				LOG.warn("Failed to create user connection. " + e.getMessage() + " User:" + u.getLogin());
			} catch (IllegalArgumentException e) {
				LOG.warn("Not registred user or project. User:" + u.getLogin() + " project : "
						+ u.getProject().getName());
			}
		}
	}

	private void checkDBRevisions() {
		long dbLastRev = rms.getCurrentRevision().getRevisionNo();
		long repoLastRev = 1;
		try {
			repoLastRev = svnRevisionService.getLastRevision(User.getUpdateUser()).getRevisionNo();
		} catch (IllegalArgumentException e) {
			LOG.warn("Failed to get last revision from repo. Not registred user or project.");
		} catch (RepositoryAuthenticationExceptoin e) {
			LOG.warn("Failed to get last revision from repo. Authentication is failed.");
		} catch (IOException e) {
			LOG.warn("Failed to get last revision from repo. " + e.getMessage());
		}

		if (dbLastRev < repoLastRev) {
			rms.saveRevisions(loadRevisionsFromRepo(dbLastRev));
		} else {
			LOG.error("DB info is invalid for project:" + projectName + " from url:" + repoUrl
					+ " Check all parameters or set reset.onstart = "
					+ "true and restart application(all subscriptions will be deleted).");
		}

	}

	private Set<Revision> loadRevisionsFromRepo(long fromRevision) {
		Set<Revision> revisions = new HashSet<>(0);
		try {
			revisions = svnRevisionService.getRevisions(User.getUpdateUser(), fromRevision, -1);
		} catch (RepositoryAuthenticationExceptoin e) {
			LOG.warn("Failed to get revisions from repo. Authentication is failed.");
		} catch (IllegalArgumentException e) {
			LOG.warn("Failed to get revisions from repo. Not registred user or project.");
		} catch (IOException e) {
			LOG.warn("Failed to get revisions from repo. " + e.getMessage());
		}
		return revisions;

	}

	private void cleanDB() {
		// TODO CLEAN DB

	}
}
