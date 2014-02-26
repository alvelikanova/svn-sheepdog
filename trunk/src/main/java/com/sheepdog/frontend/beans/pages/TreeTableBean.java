package com.sheepdog.frontend.beans.pages;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

import javax.annotation.PostConstruct;
import javax.security.auth.RefreshFailedException;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.sheepdog.business.domain.entities.FileTreeComposite;
import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.InvalidURLException;
import com.sheepdog.business.exceptions.RepositoryAuthenticationExceptoin;
import com.sheepdog.business.services.SubscriptionManagementService;
import com.sheepdog.business.services.svn.SVNFileService;
import com.sheepdog.business.services.svn.SVNProjectFacade;

@Component(value = "fileTreeBean")
@Scope("session")
public class TreeTableBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7838274538453793449L;

	/**
	 * Logger object.
	 */
	public static final Logger LOG = LoggerFactory.getLogger(TreeTableBean.class);

	@Autowired
	private SVNProjectFacade projFacade;

	@Autowired
	private SVNFileService fs;
	
	@Autowired
	private SubscriptionManagementService subscrService;

	private TreeNode root = new DefaultTreeNode("root", null);;

	private TreeNode selectedNode = null;

	private Collection<TreeNode> files = new LinkedList<>();

	
	// TODO set parameter User object of authenticated user
	@PostConstruct
	public void loadData() {

		// test initialization block. TODO replace this to the singleton of main
		// state
		try {
			projFacade.createMainConnection();
		} catch (InvalidURLException e) {
			LOG.warn("Failed to create main connection. Url:" + e.getUrl() + "is invalid.");
			// TODO feedback for exceptions
		} catch (RepositoryAuthenticationExceptoin e) {
			LOG.warn("Failed to create main connection. Authentication is failed.");
		} catch (RefreshFailedException e) {
			LOG.warn("Failed to create main connection. Can't load properties.");
		} catch (IOException e) {
			LOG.warn("Failed to create main connection. " + e.getMessage());
		}

		FileTreeComposite rootFTC = null;

		// test initialization TODO replace this to the singleton of main state
		User.getUpdateUser().setProject(new Project("sheepdog", "sdasdsad"));

		try {
			rootFTC = fs.getAllFiles(User.getUpdateUser());
		} catch (IllegalArgumentException e) {
			LOG.info("Failed to get files from repository. " + e.getMessage());
			// TODO feedback for exceptions
		} catch (RepositoryAuthenticationExceptoin e) {
			LOG.warn("Failed to get all files. Authentication is failed.");
			feedback("Failed to get all files. Authentication is failed.");
		} catch (IOException e) {
			LOG.warn("Failed to get all files. " + e.getMessage());
			feedback("Failed to get all files. " + e.getMessage());
		}

		for (FileTreeComposite ftc : rootFTC.getChilds()) {

			printComposite(ftc, root);

		}
	}

	private void printComposite(FileTreeComposite ftc, TreeNode parent) {
		
//		if(subscrService.isSubscribed(ftc.getFile(), User user)){         TODO
//ftc.setSubscribed(true);}
		TreeNode current = new DefaultTreeNode(ftc, parent);

		files.add(current);

		if (!ftc.getChilds().isEmpty()) {
			for (FileTreeComposite ftcChild : ftc.getChilds()) {
				printComposite(ftcChild, current);
			}
		}

	}

	private void feedback(String string) {
		// TODO some feedback by primefaces messages or growl ??

	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public Collection<TreeNode> getFiles() {
		return files;
	}

	public void setFiles(Collection<TreeNode> files) {
		this.files = files;
	}

	public void setProjFacade(SVNProjectFacade projFacade) {
		this.projFacade = projFacade;
	}

	public void setFs(SVNFileService fs) {
		this.fs = fs;
	}

	public void setSubscrService(SubscriptionManagementService subscrService) {
		this.subscrService = subscrService;
	}

}
