/**
 * 
 */
package com.xmltojava.helper;

import java.util.HashMap;

/**
 * @author Niel
 *
 */
public class ClassStructure {

	private String[] classes;
	private HashMap<String, HashMap<String, Integer>> classWithMembers;
	/**
	 * @return the classes
	 */
	public String[] getClasses() {
		return classes;
	}
	/**
	 * @param classes the classes to set
	 */
	public void setClasses(String[] classes) {
		this.classes = classes;
	}
	/**
	 * @return the classWithMembers
	 */
	public HashMap<String, HashMap<String, Integer>> getClassWithMembers() {
		return classWithMembers;
	}
	/**
	 * @param classWithMembers the classWithMembers to set
	 */
	public void setClassWithMembers(
			HashMap<String, HashMap<String, Integer>> classWithMembers) {
		this.classWithMembers = classWithMembers;
	}
	
}
