package com.sheepdog.update.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
import com.sheepdog.update.ShepherdService;

public class ShepherdServiceImpl implements ShepherdService {

	/**
	 * Logger object.
	 */
	public static final Logger LOG = (Logger) LoggerFactory.getLogger(ShepherdServiceImpl.class);

	@Autowired
	private SVNRevisionService svnRevisionService;

	@Autowired
	private SVNFileService svnFileService;

	private FileManagementService fileManagementService;

	private RevisionManagementService revisionManagementService;

	private SubscriptionManagementService subscriptionManagementService;

	private UserManagementService userManagementService;

	// private FileManagementService fileManagementService;
	//
	// private FileManagementService fileManagementService;

	private Map<Revision, Map<File, TypeOfFileChanges>> newRevisionsAndFiles;

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
	public void manageHerd() {
		long currentRevision = 0;

		try {
			currentRevision = revisionManagementService.getCurrentRevision().getRevisionNo();
		} catch (NullPointerException e) {
			LOG.warn("No current revisions in DB.");
		}

		if (checkUpdates(currentRevision)) {
			loadRevisions(currentRevision);

			revisionManagementService.saveRevisions(newRevisionsAndFiles.keySet());

			subscribeManagement();
		}

	}

	private boolean checkUpdates(long currentRevision) {

		Revision latestRepositoryRevision = null;

		try {
			latestRepositoryRevision = svnRevisionService.getLastRevision(User.UPDATE_USER);
		} catch (RepositoryAuthenticationExceptoin e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (latestRepositoryRevision.getRevisionNo() > currentRevision) {
			return true;
		}
		if (latestRepositoryRevision.getRevisionNo() < currentRevision) {
			// throw new UpdateException(); TODO
		}

		return false;
	}

	private void loadRevisions(long currentRevision) {
		newRevisionsAndFiles.clear();
		Set<Revision> newRevisionsSet = new HashSet<>(0);

		try {
			newRevisionsSet = svnRevisionService.getRevisions(User.UPDATE_USER, currentRevision + 1, -1);

			for (Revision r : newRevisionsSet) {
				newRevisionsAndFiles.put(r, svnFileService.getFilesByRevision(User.UPDATE_USER, r));
			}
		} catch (RepositoryAuthenticationExceptoin e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void subscribeManagement() {
		Map<Subscription, TypeOfFileChanges> newSubscriptions = new HashMap<Subscription, TypeOfFileChanges>(0);
		Map<Subscription, TypeOfFileChanges> necessarySubscriptions = new HashMap<Subscription, TypeOfFileChanges>(0);

		Map<Subscription, TypeOfFileChanges> tempSubscriptions = new HashMap<Subscription, TypeOfFileChanges>(0);

		User tempUser;

		for (Revision r : newRevisionsAndFiles.keySet()) {

			tempUser = userManagementService.getUserByLogin(r.getAuthor());

			if (tempUser != null) {
				newSubscriptions.putAll(subscribeNewFiles(newRevisionsAndFiles.get(r), tempUser));
			}

			tempSubscriptions = subscriptionManagementService.getSubscriptionsByFiles(newRevisionsAndFiles.get(r));

			necessarySubscriptions.putAll(tempSubscriptions);
		}

		subscriptionManagementService.saveSubscriptions(newSubscriptions.keySet());

		// mailSendingService.send(Set <Subscription> subscriptions);TODO

	}

	private Map<Subscription, TypeOfFileChanges> subscribeNewFiles(Map<File, TypeOfFileChanges> tempFiles, User user) {
		Map<Subscription, TypeOfFileChanges> subscriptions = new HashMap<Subscription, TypeOfFileChanges>(0);

		// check existing added files in revision.
		if (!tempFiles.containsValue(TypeOfFileChanges.ADDED)) {
			return subscriptions;
		}

		for (File f : tempFiles.keySet()) {
			if (TypeOfFileChanges.ADDED.equals(tempFiles.get(f))) {
				subscriptions.put(new Subscription(user, f), TypeOfFileChanges.ADDED);
			}
		}
		return subscriptions;
	}

}
