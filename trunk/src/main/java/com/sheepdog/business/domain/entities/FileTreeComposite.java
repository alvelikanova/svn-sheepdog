package com.sheepdog.business.domain.entities;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * FileTreeComposite class is required for creating hierarchy of files tree.
 * 
 * @author Ivan Arkhipov.
 * 
 */
public class FileTreeComposite implements Comparable<FileTreeComposite> {

	private FileTreeComposite parent = null;

	private Collection<FileTreeComposite> childs = new TreeSet<FileTreeComposite>();

	private File file = null;

	private Map property = new HashMap<>(0);

	public FileTreeComposite() {

	}

	public FileTreeComposite(File file, Map props) {

		this.setFile(file);
		this.setProperty(props);

	}

	public FileTreeComposite addChild(File file, Map props) {
		FileTreeComposite composite = new FileTreeComposite();

		composite.setFile(file);
		composite.setProperty(props);
		composite.setParent(this);

		this.childs.add(composite);

		return this;
	}

	public FileTreeComposite addChild(FileTreeComposite composite) {
		composite.setParent(this);
		this.childs.add(composite);

		return this;
	}

	public FileTreeComposite getParent() {
		return parent;
	}

	public void setParent(FileTreeComposite parent) {
		this.parent = parent;
	}

	public Collection<FileTreeComposite> getChilds() {
		return childs;
	}

	public void setChilds(Collection<FileTreeComposite> childs) {
		this.childs = childs;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public Map getProperty() {
		return property;
	}

	public void setProperty(Map property) {
		this.property = property;
	}

	@Override
	public String toString() {
		return file.toString();
	}

	/*
	 * @throws NullPointerException if ftc, files or filename strings is null.
	 * 
	 * @throws IllegalArgumentException if this object and ftc are not in one
	 * level of FileTreeComposite hierarchy.
	 */
	@Override
	public int compareTo(FileTreeComposite ftc) throws NullPointerException, IllegalArgumentException {

		if (this == ftc) {
			return 0;
		}

		if (!ftc.getParent().equals(this.getParent())) {
			throw new IllegalArgumentException();
		}

		int compare = this.getFile().getName().compareTo(ftc.getFile().getName());

		return compare;
	}

}
