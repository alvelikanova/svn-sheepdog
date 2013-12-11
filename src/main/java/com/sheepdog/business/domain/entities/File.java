package com.sheepdog.business.domain.entities;

public class File extends PersistentEntity<Integer> {

	private static final long serialVersionUID = -3129188013320967949L;

	private Project project;
	private Revision revision;

	private String name;
	private String qualifiedName;
	private String path;
	private String creatorName;

	// TODO For Ivan For Ivan - for which purpose?
	// private boolean isFile = Boolean.TRUE; - deprecated field.

	public File() {
	}

	public File(Project project, Revision revision, String name, String qualifiedName, String creatorName,
			boolean isFile) {
		this.project = project;
		this.revision = revision;
		this.name = name;
		this.qualifiedName = qualifiedName;
		this.creatorName = creatorName;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public Revision getRevision() {
		return revision;
	}

	public void setRevision(Revision revision) {
		this.revision = revision;
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

}
