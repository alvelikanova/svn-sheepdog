package com.sheepdog.business.services.svn.impl;

import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNPropertyValue;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.diff.SVNDiffWindow;

import com.sheepdog.business.domain.entities.File;

public class QuickEditor implements ISVNEditor {
	private Set<File> files = new HashSet<>();
	private File tempFile;

	@Override
	public void targetRevision(long revision) throws SVNException {

	}

	@Override
	public void openRoot(long revision) throws SVNException {
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

		// NOTE : we can get all paths of directories.
	}

	@Override
	public void openDir(String path, long revision) throws SVNException {
	}

	@Override
	public void changeDirProperty(String name, SVNPropertyValue value) throws SVNException {
	}

	@Override
	public void closeDir() throws SVNException {
	}

	@Override
	public void addFile(String path, String copyFromPath, long copyFromRevision) throws SVNException {
		tempFile = new File();
		tempFile.setPath(path);

		files.add(tempFile);
	}

	@Override
	public void openFile(String path, long revision) throws SVNException {
	}

	@Override
	public void changeFileProperty(String path, String propertyName, SVNPropertyValue propertyValue)
			throws SVNException {
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

	public Set<File> getFiles() {
		return files;
	}

}
