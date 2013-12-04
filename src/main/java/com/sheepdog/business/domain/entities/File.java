package com.sheepdog.business.domain.entities;

public class File {

	private String name;

	private String qualifiedName;

	private String creatorName;

	private boolean isFile;

	public File() {

	}

	public File(String name, String qualifiedName, String creatorName,
			boolean isFile) {
		super();
		this.name = name;
		this.qualifiedName = qualifiedName;
		this.creatorName = creatorName;
		this.isFile = isFile;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getQualifiedName() {
		return qualifiedName;
	}

	public void setQualifiedName(String qualifiedName) {
		this.qualifiedName = qualifiedName;
	}

	public String getCreatorName() {
		return creatorName;
	}

	public void setCreatorName(String creatorName) {
		this.creatorName = creatorName;
	}

	public boolean isFile() {
		return isFile;
	}

	public void setFile(boolean isFile) {
		this.isFile = isFile;
	}

}
