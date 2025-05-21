package qputility.xPath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import javax.xml.xpath.XPathFactoryConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class QPXPath 
{
	
	public static javax.xml.xpath.XPath getXpath() {
		XPathFactory xpfactory = null;
		XPath xPath = null;
		try {
			xpfactory = XPathFactory.newInstance(
						  XPathFactory.DEFAULT_OBJECT_MODEL_URI,
						  "net.sf.saxon.xpath.XPathFactoryImpl",
						  ClassLoader.getSystemClassLoader());
			xPath = xpfactory.newXPath();
		} catch (XPathFactoryConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xPath;
	}
	
	public static String xPathXMLDocument(String myXPath, File xmlDocument) throws XPathExpressionException, FileNotFoundException
	{	
		//XPathFactory factory = XPathFactory.newInstance();
		XPath xpath = getXpath();
		XPathExpression xPathExpression = xpath.compile(myXPath);
		InputSource inputSource = new InputSource(new FileInputStream(xmlDocument));
		return xPathExpression.evaluate(inputSource);
	}
	
	public static String xPathXMLDocument(String myXPath, Document doc) throws XPathExpressionException, FileNotFoundException
	{	
		//XPathFactory factory = XPathFactory.newInstance();
		XPath xPath = getXpath();
		XPathExpression xPathExpression = xPath.compile(myXPath);
//		InputSource inputSource = new InputSource(new FileInputStream(xmlDocument));
		return xPathExpression.evaluate(doc);
	}
	
	public static NodeList xPathDocument(String xPathString, Document doc) throws XPathExpressionException
	{
		NodeList nodes = null;
		
		XPath xPath = getXpath();;
		XPathExpression expr = xPath.compile(xPathString);
			
		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		nodes = (NodeList)result;
		
		return nodes;
	}
	
	public static void main(String[] args) throws Exception {
		
		System.out.println("starting");
		File file = new File("test.xml");
		String supplimentValue = QPXPath.xPathXMLDocument("/*/supplement", file);
		String prefix;
		if(supplimentValue != "")
		{
			prefix = "Supplement - ";
		}
		System.out.println("done");

	}
	
	//TODO: Check to see if using Saxon is faster
	/**
	 * Parses an XML document on the file system
	 * @param filePath	The path to the XML Document
	 * @param xpath		The xPath expression to query on the document
	 * @return			A Nodelist of the results
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	
	public static NodeList getsResults(String filePath, String xpath) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException 
	{
		InputStream is = new FileInputStream(filePath);
		return getsResults(is, xpath);
	}
	
	public static Document getDocument(String filepath) throws ParserConfigurationException, SAXException, IOException 
	{
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse(filepath);
		return doc;
	}
	
	public static Document getDocument(InputStream isXML) throws ParserConfigurationException, SAXException, IOException 
	{
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse(isXML);
		return doc;
	}
	
	
	/**
	 * Parses an XML document on an in-memory InputStream
	 * @param filePath	The path to the XML Document
	 * @param xpath		The xPath expression to query on the document
	 * @return			A Nodelist of the results
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws XPathExpressionException
	 */
	public static NodeList getsResults(InputStream isXML, String xpath) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException 
	{
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse(isXML);

		//XPathFactory factory = XPathFactory.newInstance();
		javax.xml.xpath.XPath objXPath = getXpath();
		XPathExpression expr = objXPath.compile(xpath);

		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList)result;

		return nodes;

	}
	
	public static ArrayList<String> getsStringResults(InputStream isXML, String xpath) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, Exception 
	{
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true);
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		Document doc = builder.parse(isXML);

		//XPathFactory factory = XPathFactory.newInstance();
		javax.xml.xpath.XPath objXPath = getXpath();
		XPathExpression expr = objXPath.compile(xpath);

		Object result = expr.evaluate(doc, XPathConstants.NODESET);
		NodeList nodes = (NodeList)result;
		
		return getMyNodeString(nodes);

	}
	
	public static ArrayList<String> getMyNodeString(NodeList nodes) throws Exception 
	{
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		ArrayList<String> items = new ArrayList<String>();
		StringWriter sw = new StringWriter();
		for (int i = 0; i < nodes.getLength(); i++) {
			transformer.transform(new DOMSource(nodes.item(i)), new StreamResult(sw));
			StringBuffer buffer = sw.getBuffer();
			items.add(buffer.toString());
			buffer.setLength(0);
		}
		sw.flush();
		sw.close();
		return items;
	}
}
