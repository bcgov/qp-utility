package qputility.xml;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class QPXml 
{
	public static String xmlToString(Node node) throws TransformerException 
	{
		Source source = new DOMSource(node);
		StringWriter stringWriter = new StringWriter();
		Result result = new StreamResult(stringWriter);
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.transform(source, result);
		return stringWriter.getBuffer().toString().replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>","");
	}

	public static Document xmlStringToDocument(String xmlString) throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(new InputSource(new StringReader(xmlString)));
	}

	public static void saveDocument(Document doc, String fileLocation) throws TransformerException
	{
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		Result result = new StreamResult(new File(fileLocation));
		Source source = new DOMSource(doc);
		transformer.transform(source, result);
	}

	public static String documentToString(Node node) throws TransformerException
	{
		Source source = new DOMSource(node);
		StringWriter stringWriter = new StringWriter();
		Result result = new StreamResult(stringWriter);
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.transform(source, result);
		return stringWriter.getBuffer().toString();
	}

	public static String documentToString(Document doc) throws TransformerException
	{
		return documentToString(doc.getDocumentElement());
	}

	/**
	 * This function intends to return a Document object given a
	 * Node.
	 * @param node a proper Node (probably a snippet of a larger document)
	 * @return Document
	 * @throws ParserConfigurationException
	 */
	public static Document documentTypeFromNode(Node node) throws ParserConfigurationException
	{
		Document newXmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		newXmlDocument.appendChild(newXmlDocument.importNode(node, true));
		return newXmlDocument;
	}

	/**
	 * @author chris.ditcher<br>
	 * Convenience method for generating a dummy
	 * xml for use in transformation scenarios
	 * that require one. Only has a <root/> element.
	 * @return null if there has been a parserconfigurationexception.
	 * @throws ParserConfigurationException 
	 */
	public static Document getEmptyXMLDoc() throws ParserConfigurationException
	{
		DocumentBuilder docBuilder;
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		docBuilder = docFactory.newDocumentBuilder();

		// create an element
		Document doc = docBuilder.newDocument();
		Element rootElement = doc.createElement("root");
		doc.appendChild(rootElement);

		return doc;
	}

	@SuppressWarnings("unused")
	public static void main(String[] args)
	{
		try 
		{
			Document doc = QPXml.xmlStringToDocument("<subsection secTest=\"secTest\"><num/><text>some more crap</text></subsection>");
			String test = "";
		} 
		catch (ParserConfigurationException e) 
		{
			e.printStackTrace();
		} 
		catch (SAXException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}