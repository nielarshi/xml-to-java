/**
 * 
 */
package com.xmltojava.parser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.xmltojava.helper.ClassGeneratorHelper;
import com.xmltojava.helper.ClassPopulatorHelper;
import com.xmltojava.helper.ClassStructure;
import com.xmltojava.model.Notes;
import com.xmltojava.utility.NameUtility;

/**
 * @author Niel
 * 
 */
public class GenericXMLParser {

	private Set<String> parsedNodeList = null;
	private Set<String> classSet = null;
	private HashMap<Integer, Node> validNodeMap = null;
	private HashMap<String, Integer> nodeOccuranceMap = null;
	private HashMap<Integer, Integer> childParentMap = null;
	private HashMap<Integer, HashMap<Integer, Integer>> classMemberMap = null;

	private String message = "";

	/**
	 * 
	 */
	public GenericXMLParser() {
		this.parsedNodeList = new HashSet<String>();
		this.classSet = new HashSet<String>();
		this.validNodeMap = new HashMap<Integer, Node>();
		this.nodeOccuranceMap = new HashMap<String, Integer>();
		this.childParentMap = new HashMap<Integer, Integer>();
		this.classMemberMap = new HashMap<Integer, HashMap<Integer, Integer>>();
	}

	/**
	 * @return the parsedNodeList
	 */
	public Set<String> getParsedNodeList() {
		return parsedNodeList;
	}

	/**
	 * @return the classSet
	 */
	public Set<String> getClassSet() {
		return classSet;
	}

	public String[] getClassListAsArray() {
		String[] classNameList = new String[this.classSet.size()];
		int i = 0;
		for (Object className : this.classSet.toArray()) {
			classNameList[i++] = NameUtility
					.getValidClassNameForTagName((String) className);
		}
		return classNameList;
	}

	/**
	 * @return the validNodeMap
	 */
	public HashMap<Integer, Node> getValidNodeMap() {
		return validNodeMap;
	}

	/**
	 * @return the nodeOccuranceMap
	 */
	public HashMap<String, Integer> getNodeOccuranceMap() {
		return nodeOccuranceMap;
	}

	/**
	 * @return the childParentMap
	 */
	public HashMap<Integer, Integer> getChildParentMap() {
		return childParentMap;
	}

	/**
	 * @return the classMemberMap
	 */
	public HashMap<Integer, HashMap<Integer, Integer>> getClassMemberMap() {
		return classMemberMap;
	}

	/**
	 * @return the class names with their member variables
	 */
	public HashMap<String, HashMap<String, Integer>> getClassMembersWithVariablesName() {
		HashMap<String, HashMap<String, Integer>> classMembersWithVariableName = new HashMap<String, HashMap<String, Integer>>();
		Set<Integer> nodeHashCodes = this.classMemberMap.keySet();
		Object[] keys = nodeHashCodes.toArray();

		for (int i = 0; i < nodeHashCodes.size(); i++) {

			Integer key = (Integer) keys[i];
			String className = NameUtility
					.getValidClassNameForTagName(this.validNodeMap.get(key)
							.getNodeName());

			message = "Class Name : " + className;
			logMessage();
			HashMap<Integer, Integer> variableMap = this.classMemberMap
					.get(key);

			Set<Integer> memberHashCodeList = variableMap.keySet();
			Object[] memberKeys = memberHashCodeList.toArray();

			HashMap<String, Integer> memberOccuranceMap = new HashMap<String, Integer>();
			for (int j = 0; j < memberKeys.length; j++) {
				Node memberNode = this.validNodeMap.get(memberKeys[j]);
				Integer memberHashCode = memberNode.hashCode();
				String memberName = NameUtility
						.getValidClassNameForTagName(this.validNodeMap.get(
								memberHashCode).getNodeName());
				message = "Member Name : " + memberName;
				logMessage();

				if (memberOccuranceMap.containsKey(memberName)) {
					int occurance = (memberOccuranceMap.get(memberName));
					memberOccuranceMap.put(memberName, (++occurance));
				} else {
					memberOccuranceMap.put(memberName, 1);
				}
			}
			classMembersWithVariableName.put(className, memberOccuranceMap);
		}

		return classMembersWithVariableName;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	public void parseXML(File xmlFile) {
		this.message = xmlFile.getName() + " File exists : " + xmlFile.exists();
		logMessage();

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder documentBuilder = null;
		Document xmlDocument = null;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			xmlDocument = documentBuilder.parse(xmlFile);

			Element rootElement = xmlDocument.getDocumentElement();
			this.message = "Root document is : " + rootElement.getNodeName();
			logMessage();

			Node parentNode = rootElement.cloneNode(true);
			putParentNodeToMaps(parentNode);
			addToClassList(parentNode);
			addChildNodeList(parentNode);
			addSubChildNodesForRootChildNodes(parentNode);
			showToBeGeneratedClassNames();

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @param parentNode
	 */
	private void putParentNodeToMaps(Node parentNode) {
		this.validNodeMap.put(parentNode.hashCode(), parentNode);
		this.childParentMap.put(parentNode.hashCode(), parentNode.hashCode());
		this.classMemberMap.put(parentNode.hashCode(), null);
	}

	/**
	 * @param parentNode
	 */
	private void addSubChildNodesForRootChildNodes(Node parentNode) {
		Set<Integer> keySet = this.validNodeMap.keySet();
		for (int i = 0; i < keySet.size(); i++) {
			Integer key = (Integer) keySet.toArray()[i];
			Node node = this.validNodeMap.get(key);
			if (node.hashCode() != parentNode.hashCode()) {
				addChildNodeList(node);
			}

		}
	}

	/**
	 * @param parentElement
	 */
	private void addChildNodeList(Node parentNode) {
		this.message = "Parent elements : " + parentNode.getNodeName()
				+ " with hash code : " + parentNode.hashCode();
		logMessage();

		NodeList nodeList = parentNode.getChildNodes();
		int j = 0;
		HashMap<Integer, Integer> memberVariableOccurance = new HashMap<Integer, Integer>();
		if (nodeList != null) {
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				if (!(node instanceof Text)) {
					j++;

					this.validNodeMap.put(node.hashCode(), node);
					this.nodeOccuranceMap.put(node.getNodeName(), 0);
					this.childParentMap.put(node.hashCode(),
							parentNode.hashCode());

					memberVariableOccurance.put(node.hashCode(), 1);
					System.err.println(node.getNodeName() + " got added to "
							+ parentNode.getNodeName());
					this.message = "Child elements " + j + " : "
							+ node.getNodeName() + " with hash code : "
							+ node.hashCode();
					logMessage();

					validateNodeWithOtherNodes(j, node);
				}
			}
			this.classMemberMap.put(parentNode.hashCode(),
					memberVariableOccurance);
		}
	}

	/**
	 * @param j
	 * @param node
	 */
	public void validateNodeWithOtherNodes(int j, Node node) {

		boolean check = checkIfPresentInClass(node);
		int elementCount = getValidChildNodeCount(node);

		performComparisonCheckOnNode(j, elementCount, node);

		if (!check) {
			if (elementCount > 0) {
				addToClassList(node);
			}
		}
	}

	/**
	 * @param j
	 * @param elementCount
	 * @param node
	 */
	private void performComparisonCheckOnNode(int j, int elementCount, Node node) {
		boolean result = compareNodeWithOtherNodes(j, node, elementCount);

		this.message = "Comparison result for " + node.getNodeName() + " is "
				+ result + "\nAll matching tag's structures are similar\n";

		if (result) {
			logMessage();
		} else {
			System.err.println(this.message);
		}
	}

	/**
	 * @param node
	 */
	public boolean checkIfPresentInClass(Node node) {
		boolean check = false;
		if (classSet.contains(node.getNodeName())) {
			check = true;
			this.message = node.getNodeName()
					+ " is already available in the class list\n";
			System.err.println(this.message);
		} else {
			check = false;
		}
		return check;
	}

	/**
	 * @param j
	 * @param node
	 * @param elementCount
	 */
	private boolean compareNodeWithOtherNodes(int j, Node node, int elementCount) {
		boolean isValidStructure = true;
		Node otherNode = null;
		Set<Integer> otherNodeKeySet = this.validNodeMap.keySet();
		int vx = 0;
		for (int x = 0; x < otherNodeKeySet.size(); x++) {
			Integer key = (Integer) otherNodeKeySet.toArray()[x];
			otherNode = this.validNodeMap.get(key);
			if (!(node instanceof Text)) {
				vx++;
				if (node.getNodeName().equals(otherNode.getNodeName())) {
					int childNodeCount = getValidChildNodeCount(otherNode);
					if (elementCount != childNodeCount) {
						this.message = otherNode.getNodeName()
								+ " at position " + vx + " do not match with "
								+ node.getNodeName() + " at position " + j
								+ "\n";
						System.err.println(this.message);
						isValidStructure = false;
					}
				}
			}
		}
		return isValidStructure;
	}

	/**
	 * @param node
	 */
	private void addToClassList(Node node) {
		this.classSet.add(node.getNodeName());
		this.message = node.getNodeName() + " added to class list\n";
		logMessage();
	}

	/**
	 * @param node
	 * @return
	 */
	private int getValidChildNodeCount(Node node) {
		NodeList nodeChildList;
		nodeChildList = node.getChildNodes();
		int elementCount = 0;
		for (int k = 0; k < nodeChildList.getLength(); k++) {
			Node childNode = nodeChildList.item(k);
			if (!(childNode instanceof Text)) {
				elementCount++;
			}
		}
		return elementCount;
	}

	/**
	 * 
	 */
	public void logMessage() {
		System.out.println(this.message);
	}

	/**
	 * 
	 */
	private void showToBeGeneratedClassNames() {
		this.message = "These classes will be generated :";
		System.out.println();
		for (Object className : this.classSet.toArray()) {
			System.out.println(className);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				GenericXMLParser parser = new GenericXMLParser();
				parser.parseXML(new File("note.xml"));
				
				ClassStructure classStructure = ClassGeneratorHelper
						.generateClassStructure(parser);
				
				ClassGeneratorHelper.generatePOJO(classStructure);
				
				Object noteObject = ClassPopulatorHelper.printClassNames(classStructure,parser);

				Notes notes = (Notes)noteObject;
				
				System.out.println(notes.getNoteList().get(1).getFrom());
//				System.out.println(note.getTo());
//				System.out.println(note.getBody());
//				System.out.println(note.getHeading());
			}
		});
		t.start();
	}
}
