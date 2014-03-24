package com.sheepdog.update.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.security.auth.RefreshFailedException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sheepdog.business.domain.entities.File;
import com.sheepdog.business.domain.entities.Revision;
import com.sheepdog.business.domain.entities.Subscription;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.RepositoryAuthenticationExceptoin;
import com.sheepdog.business.services.FileManagementService;
import com.sheepdog.business.services.RevisionManagementService;
import com.sheepdog.business.services.SubscriptionManagementService;
import com.sheepdog.business.services.UserManagementService;
import com.sheepdog.business.services.svn.SVNFileService;
import com.sheepdog.business.services.svn.SVNRevisionService;
import com.sheepdog.business.services.svn.impl.TypeOfFileChanges;
import com.sheepdog.mail.MailService;
import com.sheepdog.update.ShepherdService;

/**
 * Implementation of {@link ShepherdService}. First, service check repository
 * changes. If repository was modified, service load and save changes
 * information into DB. Then, if SVN Sheepdog user added some files, service
 * create new subscriptions and check existing subscriptions. After that
 * ShepherdServiceImpl invoke mail service's method to send notification emails.
 * 
 * @author Ivan Arkhipov
 * 
 */
@Controller
@RequestMapping(value = "/shepherdService")
public class ShepherdServiceImpl implements ShepherdService {

	/**
	 * Logger object.
	 */
	public static final Logger LOG = LoggerFactory.getLogger(ShepherdServiceImpl.class);

	/**
	 * Load new revision info from repository.
	 */
	@Autowired
	private SVNRevisionService svnRevisionService;

	/**
	 * Load new file info from repository.
	 */
	@Autowired
	private SVNFileService svnFileService;

	/**
	 * Update and save new file's info into DB.
	 */
	@Autowired
	private FileManagementService fileManagementService;

	/**
	 * RevisionManagementService save new revision info into DB and check latest
	 * revision.
	 */
	@Autowired
	private RevisionManagementService revisionManagementService;

	/**
	 * Save new subscriptions and check existing subscriptions.
	 */
	@Autowired
	private SubscriptionManagementService subscriptionManagementService;

	/**
	 * Check authors of repository changes.
	 */
	@Autowired
	private UserManagementService userManagementService;

	/**
	 * Send notification emails.
	 */
	@Autowired
	private MailService mailService;

	/**
	 * Fresh information about repository changes.
	 */
	private Map<Revision, Map<File, TypeOfFileChanges>> newRevisionsAndFiles = new TreeMap<>();

	/**
	 * Required UPDATE_USER.
	 */
	private final User updateUser = User.getUpdateUser();

	public ShepherdServiceImpl() {

	}

	@RequestMapping(method = RequestMethod.POST)
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		LOG.info("\n=================================================\n"
				+ "Post-Commit Hook success. Starting lookOn() method..........."
				+ "\n===============================================\n");
		lookOn();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sheepdog.update.ShepherdService#lookOn()
	 */
	@Override
	public void lookOn() {
		int currentRevision = 0;

		try {
			currentRevision = revisionManagementService.getCurrentRevision().getRevisionNo();
		} catch (NullPointerException e) {
			LOG.warn("No current revisions in DB.");
		}

		try {
			if (checkUpdates(currentRevision)) {
				LOG.info("Update is required.");

				loadRevisions(currentRevision);
				LOG.info("New revisions were loaded");
				revisionManagementService.saveRevisions(newRevisionsAndFiles.keySet());
				LOG.info("New revisions were saved.");

				subscribeManagement();
			} else {
				LOG.info("Update is not required.");
			}
		} catch (RefreshFailedException e) {
			LOG.error("UPDATE PROCESS IS FAILED! SEE LOG FOR MORE DETAILS!");
		} catch (HibernateException e) {
			LOG.error("UPDATE PROCESS IS FAILED! DB PROBLEM!");
		} catch (Exception e) {
			LOG.error("UPDATE PROCESS IS FAILED! UNKNOWN PROBLEM!" + e.getMessage());
		}
		
		LOG.info("Update is over\n====================================================================");
	}

	/**
	 * Method load current revision from repository and compare it with latest
	 * revision from DB.
	 * 
	 * @param currentRevision
	 *            Latest revision from DB.
	 * @return True - if number of latest repository revision is more than
	 *         number of current revision in DB. False - if revisions is equals.
	 * @throws RefreshFailedException
	 *             if failed connection to repository or authentication is
	 *             failed, or number of latest repository revision is less then
	 *             number of current revision in DB.
	 */
	private boolean checkUpdates(int currentRevision) throws RefreshFailedException {

		Revision latestRepositoryRevision = new Revision();
		latestRepositoryRevision.setRevisionNo(0);

		try {
			latestRepositoryRevision = svnRevisionService.getLastRevision(updateUser);
		} catch (RepositoryAuthenticationExceptoin e) {
			LOG.error("UPDATE_USER is fail repository authentication.");
			throw new RefreshFailedException();
		} catch (IllegalArgumentException e) {
			LOG.error("UPDATE_USER is not exist or not correctly registred.");
			throw new RefreshFailedException();
		} catch (IOException e) {
			LOG.error("Connection to repository failed by UPDATE_USER");
			throw new RefreshFailedException();
		}

		if (latestRepositoryRevision.getRevisionNo() > currentRevision) {
			return true;
		}
		if (latestRepositoryRevision.getRevisionNo() < currentRevision) {
			throw new RefreshFailedException();
		}

		return false;
	}

	/**
	 * Load new revisions and files info from repository.
	 * 
	 * @param currentRevision
	 *            Number of latest revision in DB.
	 * @throws RefreshFailedException
	 *             if failed connection to repository or authentication is
	 *             failed
	 */
	private void loadRevisions(long currentRevision) throws RefreshFailedException {
		newRevisionsAndFiles.clear();
		Set<Revision> newRevisionsSet = new HashSet<>(0);
		Map<File, TypeOfFileChanges> newRevFiles = new HashMap<>(0);

		try {
			newRevisionsSet = svnRevisionService.getRevisions(updateUser, currentRevision + 1, -1);
			for (Revision r : newRevisionsSet) {

				newRevFiles = svnFileService.getFilesByRevision(updateUser, r);

				newRevisionsAndFiles.put(r, newRevFiles);

			}

		} catch (RepositoryAuthenticationExceptoin e) {
			LOG.error("UPDATE_USER is fail repository authentication.");
			throw new RefreshFailedException();
		} catch (IllegalArgumentException e) {
			LOG.error("UPDATE_USER is not exist or not correctly registred.");
			throw new RefreshFailedException();
		} catch (IOException e) {
			LOG.error("Connection to repository failed by UPDATE_USER");
			throw new RefreshFailedException();
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("loadRevisions EXCEPTION");
		}

	}

	/**
	 * Method check changes info and, if SVN Sheepdog user added some files,
	 * create new subscriptions and save it in DB. Also method check existing
	 * subscriptions and invoke methods of mailservice to send notification
	 * emails.
	 * 
	 * @throws RefreshFailedException
	 *             if mail service is failed on sending emails.
	 */
	private void subscribeManagement() throws RefreshFailedException {
		Map<Subscription, TypeOfFileChanges> newSubscriptions = new HashMap<Subscription, TypeOfFileChanges>(0);
		Map<Subscription, TypeOfFileChanges> necessarySubscriptions = new HashMap<Subscription, TypeOfFileChanges>(0);

		Set<Subscription> tempSubscriptions = new HashSet<>(0);

		User tempUser;

		for (Revision r : newRevisionsAndFiles.keySet()) {

			tempUser = userManagementService.getUserByLogin(r.getAuthor());

			if (tempUser == null) {
				tempUser = userManagementService.getUserByEmail(r.getAuthor());
			}

			if (tempUser != null) {
				newSubscriptions.putAll(subscribeNewFiles(newRevisionsAndFiles.get(r), tempUser));
			}

			for (Map.Entry<File, TypeOfFileChanges> entry : newRevisionsAndFiles.get(r).entrySet()) {

				tempSubscriptions = subscriptionManagementService.getSubscriptionsByFile(entry.getKey());

				if (!tempSubscriptions.isEmpty()) {
					for (Subscription s : tempSubscriptions) {
						necessarySubscriptions.put(s, entry.getValue());
					}
					entry.getKey().setRevision(r);
					fileManagementService.updateFilesRevision(entry.getKey());
				}
			}
		}

		subscriptionManagementService.saveSubscriptions(newSubscriptions.keySet());

		necessarySubscriptions.putAll(newSubscriptions);

		try {
			mailService.sendMailBySubscription(necessarySubscriptions);
		} catch (IOException e) {
			throw new RefreshFailedException();
		} catch (TransformerException e) {
			throw new RefreshFailedException();
		}

	}

	/**
	 * Method check map with changed files. If some files was added, create and
	 * return new subscriptions.
	 * 
	 * @param tempFiles
	 *            Changed files.
	 * @param user
	 *            User of SVN Sheepdog and author of revision.
	 * @return New subscriptions.
	 */
	private Map<Subscription, TypeOfFileChanges> subscribeNewFiles(Map<File, TypeOfFileChanges> tempFiles, User user) {
		Map<Subscription, TypeOfFileChanges> subscriptions = new HashMap<Subscription, TypeOfFileChanges>(0);

		// check existing added files in revision.
		if (!tempFiles.containsValue(TypeOfFileChanges.ADDED)) {
			return subscriptions;
		}
		LOG.info("Revision have added files.");

		File newFile = null;
		for (Map.Entry<File, TypeOfFileChanges> entry : tempFiles.entrySet()) {
			if (TypeOfFileChanges.ADDED.equals(entry.getValue())) {
				newFile = prepareFile(entry.getKey());

				subscriptions.put(new Subscription(user, newFile), TypeOfFileChanges.ADDED);
			}
		}
		return subscriptions;
	}

	private File prepareFile(File file) {
		if (file == null) {
			return null;
		}
		File newFile = fileManagementService.getFileByQualifiedName(file.getQualifiedName());

		if (newFile == null) {
			fileManagementService.saveFile(file);
			newFile = fileManagementService.getFileByQualifiedName(file.getQualifiedName());
		}
		return newFile;
	}
}
