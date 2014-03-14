package com.sheepdog.frontend.beans.pages;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.security.auth.RefreshFailedException;

import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.sheepdog.business.domain.entities.FileTreeComposite;
import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.InvalidURLException;
import com.sheepdog.business.exceptions.RepositoryAuthenticationExceptoin;
import com.sheepdog.business.services.SubscriptionManagementService;
import com.sheepdog.business.services.svn.SVNFileService;
import com.sheepdog.business.services.svn.SVNProjectFacade;
import com.sheepdog.frontend.beans.templates.FeedbackBean;

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
	private SubscriptionBean subscrBean;

	@Autowired
	private LoginManager loginManager;

	@Autowired
	private FeedbackBean feedback;

	@Value("${repository.url}")
	private String repoUrl;

	@Value("${repository.login}")
	private String repoLogin;

	@Value("${repository.password}")
	private String repoPass;

	private TreeNode root = new DefaultTreeNode("root", null);

	private TreeNode selectedNode = null;

	private Collection<TreeNode> files = new LinkedList<>();

	private boolean needToReload = true;

	public void loadData() {

		if (!needToReload) {
			return;
		}

		needToReload = false;
		User currentUser = loginManager.getCurrentUser();

		if (currentUser == null) {
			LOG.warn("Current session User is null. Failed on load files from repo.");
			return;
		}

		cleanRoot();

		FileTreeComposite rootFTC = null;

		try {
			rootFTC = fs.getAllFiles(currentUser);
		} catch (IllegalArgumentException e) {
			LOG.info("Failed to get files from repository. " + e.getMessage());
			feedback.feedback(FacesMessage.SEVERITY_WARN, "Registration problem.",
					"Check your profile or contact to your administrator.");
		} catch (RepositoryAuthenticationExceptoin e) {
			LOG.warn("Failed to get all files. Authentication is failed.");
			feedback.feedback(FacesMessage.SEVERITY_ERROR, "Authentication is failed.",
					"Check your autentication info in profile page and Verify connection.");
		} catch (IOException e) {
			LOG.warn("Failed to get all files. " + e.getMessage());
			feedback.feedback(FacesMessage.SEVERITY_ERROR, "Connetion to repository is failed.",
					"Check your profile or contact to your administrator.");
		}

		for (FileTreeComposite ftc : rootFTC.getChilds()) {

			printComposite(ftc, root);

		}

		RequestContext.getCurrentInstance().update("file_form:file_tree");
		feedback.feedback(FacesMessage.SEVERITY_INFO, "Files are loaded", "Actual state on :" + new Date());
	}

	private void printComposite(FileTreeComposite ftc, TreeNode parent) {

		if (subscrBean.isSubscribed(ftc.getFile())) {
			ftc.setSubscribed(true);
		} else {
			ftc.setSubscribed(false);
		}
		TreeNode current = new DefaultTreeNode(ftc, parent);

		files.add(current);

		if (!ftc.getChilds().isEmpty()) {
			for (FileTreeComposite ftcChild : ftc.getChilds()) {
				printComposite(ftcChild, current);
			}
		}

	}

	private void cleanRoot() {
		// FileTreeComposite ftc = null;
		//
		// for (TreeNode tn : root.getChildren()) {
		// // ftc = (FileTreeComposite) tn.getData();
		// // System.out.println(ftc.getFile().getName());
		// tn.setParent(null);
		// }
		// root.getChildren().clear();
		root = new DefaultTreeNode("root", null);

		// for (TreeNode tn : files) {
		// tn.setParent(new DefaultTreeNode("none", null));
		// tn.getChildren().clear();
		// }
		files.clear();

	}

	public void collapseAll() {

		for (TreeNode tn : files) {
			tn.setExpanded(false);
		}
	}

	public void expandeAll() {

		for (TreeNode tn : files) {
			tn.setExpanded(true);
		}
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

	public void setNeedToReload() {
		this.needToReload = true;
	}

}
