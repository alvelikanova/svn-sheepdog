package com.sheepdog.business.services.svn.impl;

import java.util.HashMap;
import java.util.Map;

/**
 * Enum of types of the change applied to the file in revision.
 * 
 * @author Ivan Arkhipov.
 * 
 */
public enum TypeOfFileChanges {
	MODIFIED('M', " was modified."), 
	ADDED('A', " was added."), 
	DELETED('D', " was deleted."), 
	REPLACED('R', " was replaced."), 
	UNKNOWN('0', "Unknown changes of file!!");

	private Character mark;
	private String description;

	private static Map<Character, TypeOfFileChanges> typesMapping;

	private TypeOfFileChanges(char mark, String description) {
		this.mark = mark;
		this.description = description;
	}


	public static void initMap() {
		typesMapping = new HashMap<>();

		for (TypeOfFileChanges t : values()) {
			typesMapping.put(t.mark, t);
		}
	}

	public static TypeOfFileChanges getType(char inType) {
		if (typesMapping == null) {
			initMap();
		}

		TypeOfFileChanges outType = typesMapping.get(inType);

		if (outType == null) {
			return TypeOfFileChanges.UNKNOWN;
		}
		return outType;
	}

	@Override
	public String toString() {
		return description;
	}

}
