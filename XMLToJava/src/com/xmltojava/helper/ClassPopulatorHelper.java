/**
 * 
 */
package com.xmltojava.helper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.xmltojava.parser.GenericXMLParser;
import com.xmltojava.utility.NameUtility;

/**
 * @author Niel
 *
 */
public class ClassPopulatorHelper {

	static HashMap<Integer,Integer> nodeObjectMap = new HashMap<Integer, Integer>();
	static HashMap<Integer,Object> classMap = new HashMap<Integer, Object>();
	static HashMap<Integer,HashMap<String,Integer>> classMemberCountMap = new HashMap<Integer, HashMap<String,Integer>>();
	static HashMap<Integer, Node> validNodeMap = null;
	
	/**
	 * 
	 * @param classStructure
	 * @param parser
	 * @return
	 */
	public static Object printClassNames(ClassStructure classStructure, GenericXMLParser parser) {

		HashMap<Integer, HashMap<Integer, Integer>> classMemberMapFromParser = parser.getClassMemberMap();
		Set<Integer> hashCodeSet = classMemberMapFromParser.keySet();
		validNodeMap = parser.getValidNodeMap();
		HashMap<Integer,Integer> childParentMap = parser.getChildParentMap();
		HashMap<String, HashMap<String, Integer>> classWithMembers = classStructure
				.getClassWithMembers();
		Object newObject = null;
		Set<String> classes = classStructure.getClassWithMembers().keySet();

		System.out.println("Node names in list ");
		String[] classNameList = classStructure.getClasses();

		for(Integer validNodeHashCode : validNodeMap.keySet()) {
			Node validNode = validNodeMap.get(validNodeHashCode);
			Integer parentNodeHashCode = childParentMap.get(validNodeHashCode);
			Node parentNode = validNodeMap.get(parentNodeHashCode);

			String tagName = "";
			String parentName = "";
			String parentClassName = "";
			String classNameForTagName = "";
			tagName = validNode.getNodeName();
			parentName = parentNode.getNodeName();
			parentClassName = NameUtility.getValidClassNameForTagName(parentName);
			classNameForTagName = NameUtility.getValidClassNameForTagName(tagName);

			System.out.println(tagName+" --> "+classNameForTagName+" with hash code "+validNodeHashCode+" in "+parentName+" with hash code "+parentNodeHashCode+"\n");

			for(String className : classNameList) {

				System.out.println("Comparing with "+className);
				if(classNameForTagName.equals(className)) {
					System.out.println("Its a class. U need to create an object\n");
					if(nodeObjectMap.containsKey(validNodeHashCode)) {
						if(parentNodeHashCode.equals(validNodeHashCode)) {
							System.out.println("The root class is already present");
							newObject = classMap.get(nodeObjectMap.get(parentNodeHashCode));
						} else {
							System.out.println("The parent class "+tagName+"with hash code "+validNodeHashCode+"is already present");
						}
					} else {
						if(parentNodeHashCode.equals(validNodeHashCode)) {
							System.out.println("Root object for class "+className+" needs to be created ");
							System.out.println("Creating root class....");
							createObjectForTagClass(validNodeHashCode,
									classNameForTagName);
						} else {
							System.out.println("Object needs to be created for "+tagName+" with hash code "+validNodeHashCode+" and added to the parent class "+parentName+" with hash code "+parentNodeHashCode);
							createObjectForTagClass(validNodeHashCode,
									classNameForTagName);

						}
					}
				} 
			}

			boolean matched = false;
			for(String className : classNameList) {

				System.out.println("Comparing with "+className);
				if(classNameForTagName.equals(className)) {
					matched = true;

				}

				if(!matched) {
					System.out.println(tagName+" is here");
					System.out.println("Its not of a class type, so no user defined object will be created for this");
					System.out.println("Parent is "+parentName+" with hash code "+parentNodeHashCode);
					if(nodeObjectMap.containsKey(parentNodeHashCode)) {
						System.out.println("parent object has been already created");
					}
					else {
						System.out.println("need to create parent object");
						createObjectForTagClass(parentNodeHashCode,
								parentClassName);
						for(Integer individualHashCodeForClassMemberMap : hashCodeSet) {
							System.out.println(individualHashCodeForClassMemberMap);
							if(individualHashCodeForClassMemberMap.equals(parentNodeHashCode)) {
								System.out.println(validNodeMap.get(individualHashCodeForClassMemberMap).getNodeName()+" contains");
								HashMap<Integer,Integer> memberMap = classMemberMapFromParser.get(individualHashCodeForClassMemberMap);
								Set<Integer> memberCodeSet = memberMap.keySet();

								Integer classHashMap = nodeObjectMap.get(individualHashCodeForClassMemberMap);
								HashMap<String,Integer> memberCountMap = null;
								int i = 0;
								for(Integer memberCode : memberCodeSet) {
									System.out.println(i++);
									System.out.println(memberCode);

									memberCountMap = classMemberCountMap.get(classHashMap);
									String key = validNodeMap.get(memberCode).getNodeName();

									System.out.println("Before value ==> "+validNodeMap.get(memberCode).getNodeName()+" --> "+memberCountMap.get(key));

									System.out.println(key);
									if(memberCountMap.containsKey(key)) {
										System.out.println("matched");
										Integer value = memberCountMap.get(key);

										int value1 = value.intValue();
										Integer valueToBePut = new Integer(value1++);
										memberCountMap.put(key, value1);
										System.out.println("Updated "+memberCountMap.get(key)+" / "+memberCountMap.size()+" in "+parentName+" with hash code "+parentNodeHashCode);
									}
									else {
										System.out.println("did not matched");
										memberCountMap.put(key, 1);
										System.out.println("Added "+memberCountMap.get(key)+" / "+memberCountMap.size());
									}
									System.out.println("After value ==> "+validNodeMap.get(memberCode).getNodeName()+" --> "+memberCountMap.get(key)+" in "+parentName+" with hash code "+parentNodeHashCode);
									classMemberCountMap.put(classHashMap, memberCountMap);
									System.out.println("class member count map updated");
								}
							}


						}
					}
				}else{

					for(Integer individualHashCodeForClassMemberMap : hashCodeSet) {
						System.out.println(individualHashCodeForClassMemberMap);
						if(individualHashCodeForClassMemberMap.equals(parentNodeHashCode)) {
							System.out.println(validNodeMap.get(individualHashCodeForClassMemberMap).getNodeName()+" contains");
							HashMap<Integer,Integer> memberMap = classMemberMapFromParser.get(individualHashCodeForClassMemberMap);
							Set<Integer> memberCodeSet = memberMap.keySet();

							Integer classHashMap = nodeObjectMap.get(individualHashCodeForClassMemberMap);
							HashMap<String,Integer> memberCountMap = null;
							int i = 0;
							for(Integer memberCode : memberCodeSet) {
								System.out.println(i++);
								System.out.println(memberCode);

								memberCountMap = classMemberCountMap.get(classHashMap);
								String key = validNodeMap.get(memberCode).getNodeName();

								System.out.println("Before value ==> "+validNodeMap.get(memberCode).getNodeName()+" --> "+memberCountMap.get(key));

								System.out.println(key);
								if(memberCountMap.containsKey(key)) {
									System.out.println("matched");
									Integer value = memberCountMap.get(key);

									int value1 = value.intValue();
									Integer valueToBePut = new Integer(value1++);
									memberCountMap.put(key, value1);
									System.out.println("Updated "+memberCountMap.get(key)+" / "+memberCountMap.size()+" in "+parentName+" with hash code "+parentNodeHashCode);
								}
								else {
									System.out.println("did not matched");
									memberCountMap.put(key, 1);
									System.out.println("Added "+memberCountMap.get(key)+" / "+memberCountMap.size());
								}
								System.out.println("After value ==> "+validNodeMap.get(memberCode).getNodeName()+" --> "+memberCountMap.get(key)+" in "+parentName+" with hash code "+parentNodeHashCode);
								classMemberCountMap.put(classHashMap, memberCountMap);
								System.out.println("class member count map updated");
							}
						}


					}
				
				}
			}
		}

		showClassMemberOccuranceList(classStructure);
		return newObject;

	}

	/**
	 * @param validNodeHashCode
	 * @param classNameForTagName
	 */
	public static void createObjectForTagClass(Integer validNodeHashCode,
			String classNameForTagName) {
		Class class1 = null;

		try {
			class1 = Class.forName("com.xmltojava.model."
					+ classNameForTagName);
			System.out.println(class1.hashCode()+" // "+class1.getName()+" created");
			Constructor constructor = class1.getConstructor();
			Object newObject = constructor.newInstance();
			System.out.println("object "+newObject.hashCode());
			nodeObjectMap.put(validNodeHashCode, newObject.hashCode());
			classMap.put(newObject.hashCode(), newObject);

			HashMap<String,Integer> classMemberCount = new HashMap<String, Integer>();
			classMemberCountMap.put(newObject.hashCode(), classMemberCount);

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param classStructure
	 */
	private static void showClassMemberOccuranceList(ClassStructure classStructure) {
		Set<Integer> classHashCodeSet = classMemberCountMap.keySet();
		HashMap<String,Integer> memberOccuranceMap = null;
		Set<String> memberNameSet = null;
		for(Integer classHashCode : classHashCodeSet) {
			System.out.println("Class "+classMap.get(classHashCode).getClass().getName()+" // "+classHashCode);

			Set<Integer> nodeHashCodeSet = nodeObjectMap.keySet();
			Integer requiredNodeHashCode = null;
			for(Integer nodeHashCode : nodeHashCodeSet) {
				if(nodeObjectMap.get(nodeHashCode).equals(classHashCode)) {
					requiredNodeHashCode = nodeHashCode;
				}
			}
			Node currentNode = validNodeMap.get(requiredNodeHashCode);
			System.out.println("Node is : "+currentNode);

			memberOccuranceMap = classMemberCountMap.get(classHashCode);
			memberNameSet = memberOccuranceMap.keySet();
			ArrayList objectList = null;
			ArrayList<String> stringList = null;
			for(String memberName : memberNameSet) {
				System.out.println(memberName+" -- "+memberOccuranceMap.get(memberName));
				String validMemberName = NameUtility.getValidClassNameForTagName(memberName);
				String setMethodName = NameUtility.getSetMethodName(NameUtility.getValidClassNameForTagName(memberName));
				System.out.println("need to invoke "+setMethodName);

				NodeList childNodeList = currentNode.getChildNodes();
				int j =0;

				for(int i = 0; i<childNodeList.getLength();i++) {
					Node node = childNodeList.item(i);
					if(!(node instanceof Text)) {

						if(node.getNodeName().equals(memberName)) {
							j++;
							System.out.println("Matched with "+memberName);
							if(memberOccuranceMap.get(memberName).intValue()>1){
								String[] classNameList = classStructure.getClasses();
								String name="";
								for(String className : classNameList) {
									System.out.println("Comparing "+validMemberName+" with "+className);
									if(validMemberName.equals(className)) {
										name = validMemberName;
									}
								}
								if(!"".equals(name)) {
									System.out.println("parameter is a list of type "+name);
									if(j==(memberOccuranceMap.get(memberName).intValue()/5)) {
										objectList.add(classMap.get(nodeObjectMap.get(node.hashCode())));
										Object object = classMap.get(classHashCode);
										try {
											Method method = object.getClass().getMethod(setMethodName+"List",ArrayList.class);
											method.invoke(object, objectList);
											System.out.println("method "+method.getName()+" of "+object.getClass()+" invoked");
										} catch (SecurityException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (NoSuchMethodException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (IllegalArgumentException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (IllegalAccessException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (InvocationTargetException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
									else if(j==1){
										objectList = new ArrayList();
										objectList.add(classMap.get(nodeObjectMap.get(node.hashCode())));
									}else if(j==2){
										objectList.add(classMap.get(nodeObjectMap.get(node.hashCode())));
									}
								} else{
									System.out.println("parameter is a list of type String with value "+j+" = "+node.getTextContent());
									String value = node.getTextContent();
									if(j==memberOccuranceMap.get(memberName).intValue()) {
										stringList.add(value);
										Object object = classMap.get(classHashCode);
										try {
											Method method = object.getClass().getMethod(setMethodName+"List",ArrayList.class);
											method.invoke(object, stringList);
											System.out.println("method "+method.getName()+" of "+object.getClass()+" invoked");
										} catch (SecurityException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (NoSuchMethodException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (IllegalArgumentException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (IllegalAccessException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (InvocationTargetException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
									else if(j==1){
										stringList = new ArrayList<String>();
										stringList.add(value);
									}else{
										stringList.add(value);
									}
								}

							} else{
								String[] classNameList = classStructure.getClasses();
								String name="";

								for(String className : classNameList) {
									System.out.println("Comparing "+validMemberName+" with "+className);
									if(validMemberName.equals(className)) {
										name = validMemberName;
									}
								}
								if(!"".equals(name)) {
									System.out.println("parameter is of type "+name);
									Object childObj = classMap.get(nodeObjectMap.get(node.hashCode()));
									Object object = classMap.get(classHashCode);
									try {
										Method method = object.getClass().getMethod(setMethodName+"List",ArrayList.class);
										method.invoke(object, childObj);
										System.out.println("method "+method.getName()+" of "+object.getClass()+" invoked");
									} catch (SecurityException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (NoSuchMethodException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IllegalArgumentException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IllegalAccessException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (InvocationTargetException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else{

									System.out.println("parameter is of type String with value ----> "+node.getTextContent());
									String value = node.getTextContent();
									Object object = classMap.get(classHashCode);
									try {
										Method method = object.getClass().getMethod(setMethodName,String.class);
										method.invoke(object, value);
										System.out.println("method "+method.getName()+" of "+object.getClass()+" invoked");
									} catch (SecurityException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (NoSuchMethodException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IllegalArgumentException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IllegalAccessException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (InvocationTargetException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

								}
							}
						}
					}

				}

			}
		}
	}
}
