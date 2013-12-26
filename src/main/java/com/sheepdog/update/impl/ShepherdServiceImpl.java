package com.sheepdog.update.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.security.auth.RefreshFailedException;
import javax.xml.transform.TransformerException;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.qos.logback.classic.Logger;

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

@Service
public class ShepherdServiceImpl implements ShepherdService {

	/**
	 * Logger object.
	 */
	public static final Logger LOG = (Logger) LoggerFactory.getLogger(ShepherdServiceImpl.class);

	@Autowired
	private SVNRevisionService svnRevisionService;

	@Autowired
	private SVNFileService svnFileService;

	@Autowired
	private FileManagementService fileManagementService;

	@Autowired
	private RevisionManagementService revisionManagementService;

	@Autowired
	private SubscriptionManagementService subscriptionManagementService;

	@Autowired
	private UserManagementService userManagementService;

	@Autowired
	private MailService mailService;

	private Map<Revision, Map<File, TypeOfFileChanges>> newRevisionsAndFiles;

	private final User updateUser = User.getUpdateUser();

	public ShepherdServiceImpl(SVNRevisionService svnRevisionService, SVNFileService svnFileService,
			FileManagementService fileManagementService, RevisionManagementService revisionManagementService,
			SubscriptionManagementService subscriptionManagementService, UserManagementService userManagementService,
			Map<Revision, Map<File, TypeOfFileChanges>> newRevisionsAndFiles) {
		super();
		this.svnRevisionService = svnRevisionService;
		this.svnFileService = svnFileService;
		this.fileManagementService = fileManagementService;
		this.revisionManagementService = revisionManagementService;
		this.subscriptionManagementService = subscriptionManagementService;
		this.userManagementService = userManagementService;
		this.newRevisionsAndFiles = newRevisionsAndFiles;
	}

	@Override
	public void lookOn() {
		long currentRevision = 0;

		try {
			currentRevision = revisionManagementService.getCurrentRevision().getRevisionNo();
		} catch (NullPointerException e) {
			LOG.warn("No current revisions in DB.");
		}

		try {
			if (checkUpdates(currentRevision)) {
				loadRevisions(currentRevision);

				revisionManagementService.saveRevisions(newRevisionsAndFiles.keySet());

				subscribeManagement();
			}
		} catch (RefreshFailedException e) {
			LOG.error("UPDATE PROCESS IS FAILED! SEE LOG FOR MORE DETAILS!");
		} catch (Exception e) {
			LOG.error("UPDATE PROCESS IS FAILED! UNKNOWN PROBLEM!");
		}
	}

	private boolean checkUpdates(long currentRevision) throws RefreshFailedException {

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

	private void loadRevisions(long currentRevision) throws RefreshFailedException {
		newRevisionsAndFiles.clear();
		Set<Revision> newRevisionsSet = new HashSet<>(0);

		try {
			newRevisionsSet = svnRevisionService.getRevisions(updateUser, currentRevision + 1, -1);

			for (Revision r : newRevisionsSet) {
				newRevisionsAndFiles.put(r, svnFileService.getFilesByRevision(updateUser, r));
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
		}

	}

	private void subscribeManagement() throws RefreshFailedException {
		Map<Subscription, TypeOfFileChanges> newSubscriptions = new HashMap<Subscription, TypeOfFileChanges>(0);
		Map<Subscription, TypeOfFileChanges> necessarySubscriptions = new HashMap<Subscription, TypeOfFileChanges>(0);

		Set<Subscription> tempSubscriptions = new HashSet<>(0);

		User tempUser;

		for (Revision r : newRevisionsAndFiles.keySet()) {

			tempUser = userManagementService.getUserByLogin(r.getAuthor());

			if (tempUser != null) {
				newSubscriptions.putAll(subscribeNewFiles(newRevisionsAndFiles.get(r), tempUser));
			}

			for (Map.Entry<File, TypeOfFileChanges> entry : newRevisionsAndFiles.get(r).entrySet()) {
				// tempSubscriptions =
				// subscriptionManagementService.getSubscriptionsByFile(e); TODO
				if (!tempSubscriptions.isEmpty()) {
					for (Subscription s : tempSubscriptions) {
						necessarySubscriptions.put(s, entry.getValue());
					}
					// fileManagementService.updateFilesRevision(entry.getKey);
					// TODO
				}
			}
		}

		subscriptionManagementService.saveSubscriptions(newSubscriptions.keySet());

		try {
			mailService.sendMailBySubscription(necessarySubscriptions);
		} catch (RefreshFailedException e) {
			throw new RefreshFailedException();
		} catch (IOException e) {
			throw new RefreshFailedException();
		} catch (TransformerException e) {
			throw new RefreshFailedException();
		}

	}

	private Map<Subscription, TypeOfFileChanges> subscribeNewFiles(Map<File, TypeOfFileChanges> tempFiles, User user) {
		Map<Subscription, TypeOfFileChanges> subscriptions = new HashMap<Subscription, TypeOfFileChanges>(0);

		// check existing added files in revision.
		if (!tempFiles.containsValue(TypeOfFileChanges.ADDED)) {
			return subscriptions;
		}

		for (Map.Entry<File, TypeOfFileChanges> entry : tempFiles.entrySet()) {
			if (TypeOfFileChanges.ADDED.equals(entry.getValue())) {
				subscriptions.put(new Subscription(user, entry.getKey()), TypeOfFileChanges.ADDED);
			}
		}
		return subscriptions;
	}

}
