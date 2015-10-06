/**
 * 
 */
package com.xmltojava.classgenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.xmltojava.utility.NameUtility;

/**
 * @author Niel
 * 
 */
public class JavaClassGenerator {

	private static JCodeModel jCodeModel;

	/**
	 * 
	 * @param className
	 * @param memberMap
	 * @param classList
	 * @return
	 */
	public static boolean createClassWithMemberVariables(String className,
			HashMap<String, Integer> memberMap, String[] classList) {
		boolean isCreated = false;
		jCodeModel = new JCodeModel();
		try {
			for (String classNameInList : classList) {
				System.out.println("Class : " + classNameInList + " / "
						+ className);
			}
			for (String classNameInList : classList) {
				System.out.println("Class : " + classNameInList + " / "
						+ className);
				if (className.equals(classNameInList)) {
					System.out.println("Creating class for : " + className);
					JDefinedClass jClass = createClass(className);
					createClassMembers(jClass, memberMap, classList);
					jCodeModel.build(new File("src"));
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return isCreated;
	}

	/**
	 * 
	 * @param className
	 * @return
	 */
	private static JDefinedClass createClass(String className) {
		JDefinedClass jClass = null;
		try {
			
			String packageName = "com.xmltojava.model";
			jClass = jCodeModel._class(packageName+"."+ className);
		} catch (JClassAlreadyExistsException e) {
			e.printStackTrace();
		}
		return jClass;
	}

	/**
	 * 
	 * @param jClass
	 * @param memberMap
	 * @param classList
	 */
	private static void createClassMembers(JDefinedClass jClass,
			HashMap<String, Integer> memberMap, String[] classList) {
		Set<String> members = memberMap.keySet();

		for (int j = 0; j < members.toArray().length; j++) {
			Set<String> visited = new HashSet<String>();
			String memberName = (String) members.toArray()[j];
			System.out.println(memberName + " : " + memberMap.get(memberName));
			int k = 0;
			JType jtype = null;
			JFieldVar field = null;
			boolean isVisited = false;
			for (String className : classList) {
				k++;
				System.out.println(className + " // " + memberName);

				if (!visited.contains(memberName)) {
					System.out.println("not visited");
					if (className.equals(memberName)) {
						System.out.println("matched");
						if (memberMap.get(memberName) == 1) {
							jtype = jCodeModel.ref(memberName);
							field = jClass.field(JMod.PRIVATE, jtype,
									NameUtility.getValidMemberName(memberName));
						} else if (memberMap.get(memberName) > 1) {
							jtype = jCodeModel.ref(ArrayList.class).narrow(
									jCodeModel.ref(memberName));
							memberName = memberName + "List";
							field = jClass.field(JMod.PRIVATE, jtype,
									NameUtility.getValidMemberName(memberName));
						}
						isVisited = true;
						visited.add(memberName);
					}
				}
			}

			if (!isVisited) {
				System.out.println("dint match");
				if (memberMap.get(memberName) == 1) {
					jtype = jCodeModel.ref(String.class);
					field = jClass.field(JMod.PRIVATE, jtype,
							NameUtility.getValidMemberName(memberName));
				} else if (memberMap.get(memberName) > 1) {
					jtype = jCodeModel.ref(ArrayList.class).narrow(
							jCodeModel.ref("String"));
					memberName = memberName + "List";
					field = jClass
							.field(JMod.PRIVATE, jtype,
									NameUtility.getValidMemberName(memberName));
				}
			}
			generateGetterMethodForVariable(jClass, memberName, field, jtype);
			generateSetterMethodForVariable(jClass, memberName, field, jtype);
		}
	}

	/**
	 * 
	 * @param jClass
	 * @param variableName
	 * @param field
	 * @param jType
	 */
	private static void generateGetterMethodForVariable(JDefinedClass jClass,
			String variableName, JFieldVar field, JType jType) {
		String getterMethodName = "get" + variableName;
		JMethod getterMethod = jClass.method(JMod.PUBLIC, jType,
				getterMethodName);
		JBlock block = getterMethod.body();
		block._return(field);
	}
	
	/**
	 * 
	 * @param jClass
	 * @param variableName
	 * @param field
	 * @param jType
	 */
	private static void generateSetterMethodForVariable(JDefinedClass jClass,
			String variableName, JFieldVar field, JType jType) {
		String setterMethodName = "set" + variableName;
	     JMethod setterMethod = jClass.method(JMod.PUBLIC, Void.TYPE, setterMethodName);
	     String setterParameter = field.name();
	     setterMethod.param(jType, setterParameter);
	    setterMethod.body().assign(JExpr._this().ref(field), JExpr.ref(setterParameter));
	   
	}
}
