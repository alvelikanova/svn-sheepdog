package com.sheepdog.update.impl;

import java.io.IOException;
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
import com.sheepdog.business.services.svn.SVNFileService;
import com.sheepdog.business.services.svn.SVNRevisionService;
import com.sheepdog.business.services.svn.impl.TypeOfFileChanges;
import com.sheepdog.update.HerdingService;

public class HerdingServiceImpl implements HerdingService {

	/**
	 * Logger object.
	 */
	public static final Logger LOG = (Logger) LoggerFactory.getLogger(HerdingServiceImpl.class);

	@Autowired
	private SVNRevisionService svnRevisionService;

	@Autowired
	private SVNFileService svnFileService;

	private Map<Revision, Map<File, TypeOfFileChanges>> newRevisionsAndFiles;

	//ONE PROJECT QUESTION
	private List<User> users;

	public HerdingServiceImpl(SVNRevisionService svnRevisionService, SVNFileService svnFileService, List<User> users) {
		super();
		this.svnRevisionService = svnRevisionService;
		this.svnFileService = svnFileService;
		this.users = users;
	}

	@Override
	public void manageHerd() {
		long currentRevision = 0;

		// currentRevision =
		// dbRevisionService.getCurrentRevision().getRevisionNo(); - NPE TODO

		if (!checkUpdates(currentRevision)) {
			return;
		}

		loadRevisions(currentRevision);

		// dbRevisionService.saveRevisions(newRevisionsAndFiles.keySet()); TODO

		subscribeManagement();

	}

	private boolean checkUpdates(long currentRevision) {

		Revision latestRepositoryRevision = null;

		try {
			latestRepositoryRevision = svnRevisionService.getLastRevision(users.get(0));
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
			newRevisionsSet = svnRevisionService.getRevisions(users.get(0), currentRevision + 1, -1);

			for (Revision r : newRevisionsSet) {
				newRevisionsAndFiles.put(r, svnFileService.getFilesByRevision(users.get(0), r));
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
		Set<Subscription> newSubscriptions = new HashSet<>(0);
		Set<Subscription> requiredSubscriptions = new HashSet<>(0);

		Set<Subscription> tempSubscriptions = new HashSet<>(0);

		User tempUser;

		for (Revision r : newRevisionsAndFiles.keySet()) {
			// tempUser =dbFileService.getUserByLogin(r.getAuthor());// TODO
			tempUser = new User();

			if (tempUser != null) {
				newSubscriptions.addAll(subscribeNewFiles(newRevisionsAndFiles.get(r), tempUser));
			}

			// tempSubscriptions =
			// subscriptionManagementService.getSubscriptionsByFiles(newRevisionsAndFiles.get(r));TODO

			requiredSubscriptions.addAll(tempSubscriptions);
		}

		// subscriptionManagementService.saveSubscriptions(newSubscriptions);TODO

		// mailSendingService.send(Set <Subscription> subscriptions);TODO

	}

	private Set<Subscription> subscribeNewFiles(Map<File, TypeOfFileChanges> tempFiles, User user) {
		Set<Subscription> subscriptions = new HashSet<>(0);

		// check existing added files in revision.
		if (!tempFiles.containsValue(TypeOfFileChanges.ADDED)) {
			return subscriptions;
		}

		for (File f : tempFiles.keySet()) {
			if (TypeOfFileChanges.ADDED.equals(tempFiles.get(f))) {
				subscriptions.add(new Subscription(user, f));
			}
		}
		return subscriptions;
	}

}
