/**
 * 
 */
package com.xmltojava.helper;

import java.util.HashMap;
import java.util.Set;

import com.xmltojava.classgenerator.JavaClassGenerator;
import com.xmltojava.parser.GenericXMLParser;

/**
 * @author Niel
 * 
 */
public class ClassGeneratorHelper {

	/**
	 * 
	 * @param parser
	 * @return
	 */
	public static ClassStructure generateClassStructure(GenericXMLParser parser ) {
	//		HashMap<String, HashMap<String, Integer>> classWithMembers) {
		HashMap<String, HashMap<String, Integer>> classWithMembers = parser
		.getClassMembersWithVariablesName();
		String[] classNameList = parser.getClassListAsArray();
		
		ClassStructure classStructure = new ClassStructure();
		classStructure.setClassWithMembers(classWithMembers);
		classStructure.setClasses(classNameList);
		
		return classStructure;
	}

	/**
	 * 
	 * @param classStructure
	 */
	public static void generatePOJO(ClassStructure classStructure) {
		HashMap<String, HashMap<String, Integer>> classWithMembers = classStructure
				.getClassWithMembers();

		Set<String> classes = classStructure.getClassWithMembers().keySet();

		System.out.println("before class generation");
		for (int i = 0; i < classes.toArray().length; i++) {
			String className = (String) classes.toArray()[i];
			HashMap<String, Integer> classMemberMap = classWithMembers
					.get(className);
			System.out.println("\nClass Name : " + className);
			createClass(className, classMemberMap, classStructure.getClasses());
		}
	}

	/**
	 * 
	 * @param className
	 * @param memberMap
	 * @param classList
	 */
	public static void createClass(String className,
			HashMap<String, Integer> memberMap, String[] classList) {
		JavaClassGenerator.createClassWithMemberVariables(className, memberMap,
				classList);
	}
	
	
}
