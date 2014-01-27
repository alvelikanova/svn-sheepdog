package com.sheepdog.business.domain.entities;

public class File extends PersistentEntity<Integer> {

	private static final long serialVersionUID = -3129188013320967949L;

	private Project project;
	private Revision lastRevision;

	private String name;
	private String qualifiedName;
	private String path;
	private String creatorName;

	private boolean isDir = Boolean.FALSE;

	public File() {
	}

	public File(Project project, Revision lastRevision, String name, String qualifiedName, String creatorName,
			boolean isFile) {
		this.project = project;
		this.lastRevision = lastRevision;
		this.name = name;
		this.qualifiedName = qualifiedName;
		this.creatorName = creatorName;
	}

	public static File getRootDir() {
		File root = new File();
		root.setIsDir(true);
		root.setName("/");
		root.setQualifiedName("/");

		return root;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Revision getRevision() {
		return lastRevision;
	}

	public void setRevision(Revision lastRevision) {
		this.lastRevision = lastRevision;
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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isDir() {
		return isDir;
	}

	public void setIsDir(boolean isDir) {
		this.isDir = isDir;
	}

	@Override
	public String toString() {
		return name + " " + qualifiedName;
	}

}
