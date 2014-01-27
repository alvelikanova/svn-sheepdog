package com.sheepdog.business.services.svn.impl;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.SVNPropertyValue;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.diff.SVNDiffWindow;

import com.sheepdog.business.domain.entities.File;

/**
 * QuickEditor is simple implementing of ISVNEditor, that required for
 * one-request loading information about files and directories from repository.
 * 
 * @author Ivan Arkhipov.
 * 
 */
public class QuickEditor implements ISVNEditor {
	private LinkedList<File> files = new LinkedList<>();

	private File tempFile;

	private LinkedList myDirectoriesStack = new LinkedList();

	private Map myDirProps = new HashMap();
	private Map myFileProps = new HashMap();

	@Override
	public void targetRevision(long revision) throws SVNException {

	}

	@Override
	public void openRoot(long revision) throws SVNException {
		String absoluteDirPath = "/";
		myDirectoriesStack.push(absoluteDirPath);
	}

	@Override
	public void deleteEntry(String path, long revision) throws SVNException {
	}

	@Override
	public void absentDir(String path) throws SVNException {
	}

	@Override
	public void absentFile(String path) throws SVNException {
	}

	@Override
	public void addDir(String path, String copyFromPath, long copyFromRevision) throws SVNException {
		tempFile = new File();
		tempFile.setPath(path);
		tempFile.setIsDir(true);

		files.add(tempFile);

		String absouluteDirPath = "/" + path;
		myDirectoriesStack.push(absouluteDirPath);
	}

	@Override
	public void openDir(String path, long revision) throws SVNException {
		String absoluteDirPath = "/" + path;
		myDirectoriesStack.push(absoluteDirPath);
	}

	@Override
	public void changeDirProperty(String name, SVNPropertyValue value) throws SVNException {

		String currentDirPath = (String) myDirectoriesStack.peek();

		Map props = (Map) myDirProps.get(currentDirPath);
		if (props == null) {
			props = new HashMap();
			myDirProps.put(currentDirPath, props);
		}
		props.put(name, value);
	}

	@Override
	public void closeDir() throws SVNException {
		myDirectoriesStack.pop();
	}

	@Override
	public void addFile(String path, String copyFromPath, long copyFromRevision) throws SVNException {
		tempFile = new File();
		tempFile.setPath(path);
		tempFile.setIsDir(false);

		files.add(tempFile);

	}

	@Override
	public void openFile(String path, long revision) throws SVNException {
	}

	@Override
	public void changeFileProperty(String path, String propertyName, SVNPropertyValue propertyValue)
			throws SVNException {

		String absolutePath = "/" + path;
		Map props = (Map) myFileProps.get(absolutePath);
		if (props == null) {
			props = new HashMap();
			myFileProps.put(absolutePath, props);
		}
		props.put(propertyName, propertyValue);
	}

	@Override
	public void closeFile(String path, String textChecksum) throws SVNException {
	}

	@Override
	public SVNCommitInfo closeEdit() throws SVNException {
		return null;
	}

	@Override
	public void abortEdit() throws SVNException {
	}

	@Override
	public void applyTextDelta(String path, String baseChecksum) throws SVNException {
	}

	@Override
	public void textDeltaEnd(String path) throws SVNException {
	}

	@Override
	public OutputStream textDeltaChunk(String path, SVNDiffWindow diffWindow) throws SVNException {
		return null;
	}

	public LinkedList<File> getFiles() {
		return files;
	}

	public Map getMyDirProps() {
		return myDirProps;
	}

	public Map getMyFileProps() {
		return myFileProps;
	}

}
