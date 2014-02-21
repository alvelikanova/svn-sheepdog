package com.sheepdog.frontend.beans.pages;

import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.security.auth.RefreshFailedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sheepdog.business.domain.entities.FileTreeComposite;
import com.sheepdog.business.domain.entities.Project;
import com.sheepdog.business.domain.entities.User;
import com.sheepdog.business.exceptions.InvalidURLException;
import com.sheepdog.business.exceptions.RepositoryAuthenticationExceptoin;
import com.sheepdog.business.services.svn.SVNFileService;
import com.sheepdog.business.services.svn.SVNProjectFacade;

@Component
@ManagedBean(name = "testBean")
@javax.faces.bean.SessionScoped
public class TestBean {
	
	@Autowired
	private SVNProjectFacade projFacade;
	
	@Autowired
	private SVNFileService fs;

	private int tabIndex = 0;

	private String output = "CLEAR";
	
	FileTreeComposite root = null;

	
	public void update(){
	
		try {
			projFacade.createMainConnection();
		} catch (InvalidURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryAuthenticationExceptoin e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RefreshFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		User.getUpdateUser().setProject(new Project("sheepdog", "sdasdsad"));
		
		try {
			root = fs.getAllFiles(User.getUpdateUser());
		} catch (IllegalArgumentException e) {
			
			e.printStackTrace();
		} catch (RepositoryAuthenticationExceptoin e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		printComposite(root);
		
		System.out.println(output);
	}
	private void printComposite(FileTreeComposite ftc){
		System.out.println("0");
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
		sb.append("\n");
		
		output +=sb.toString();
		
		if (!ftc.getChilds().isEmpty()){
			for(FileTreeComposite ftcChild : ftc.getChilds()){
				printComposite(ftcChild);
			}
		}
		
	}


	public int getTabIndex() {
		return tabIndex;
	}

	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}

	public String getOutput() {
		return output;
	}
	


	public void setOutput(String output) {
		this.output = output;
	}
	public SVNProjectFacade getProjFacade() {
		return projFacade;
	}
	public void setProjFacade(SVNProjectFacade projFacade) {
		this.projFacade = projFacade;
	}
	public SVNFileService getFs() {
		return fs;
	}
	public void setFs(SVNFileService fs) {
		this.fs = fs;
	}
	public FileTreeComposite getRoot() {
		return root;
	}
	public void setRoot(FileTreeComposite root) {
		this.root = root;
	}

}
