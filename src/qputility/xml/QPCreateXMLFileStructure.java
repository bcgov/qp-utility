package qputility.xml;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import qputility.enums.QPDisplayDebugInfo;

public class QPCreateXMLFileStructure 
{
	public static void createXMLStructure(String rootDirectory, String outputLocation) throws IOException
	{
		createXMLStructure(rootDirectory, outputLocation, QPDisplayDebugInfo.doNotDisplayDebugInfo);
	}
	
	public static void createXMLStructure(String rootDirectory, String outputLocation, QPDisplayDebugInfo displayDebugInfo) throws IOException
	{
		//FileWriter fw = new FileWriter(outputLocation);
		//BufferedWriter out = new BufferedWriter(fw);
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputLocation),"UTF-8"));
		out.write(createXML(rootDirectory, displayDebugInfo).toString());
		out.flush();
		out.close();
	}
	
	public static Document createXMLStructure(String rootDirectory) throws SAXException, IOException, ParserConfigurationException
	{		
		return createXMLStructure(rootDirectory, QPDisplayDebugInfo.doNotDisplayDebugInfo); 
	}
	
	public static Document createXMLStructure(String rootDirectory, QPDisplayDebugInfo displayDebugInfo) throws SAXException, IOException, ParserConfigurationException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		InputSource source = new InputSource(new StringReader(createXML(rootDirectory, displayDebugInfo).toString()));
		Document doc = null;
				
		doc = factory.newDocumentBuilder().parse(source);
		
		return doc; 
	}
	
	private static StringBuilder createXML(String rootDirectory, QPDisplayDebugInfo displayDebugInfo)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		//sb.append("<?xml version=\"1.0\"?>");
		sb.append("<root>");
		populateXML(sb, new File(rootDirectory), displayDebugInfo);
		sb.append("</root>");
		
		return sb;
	}
	
	private static StringBuilder populateXML(StringBuilder sb, File dir, QPDisplayDebugInfo displayDebugInfo)
	{
		File[] files = dir.listFiles();
		for(int i = 0; i < files.length; i ++)
		{
			File file = files[i];
			String fPath = file.getPath();
			if(!file.isFile())
			{
				sb.append("<directory id=\"" + fPath + "\" modified=\"" + file.lastModified() + "\">");
				if(displayDebugInfo.equals(QPDisplayDebugInfo.displayDebugInfo))
				{
					System.out.println("-Added directory: " + fPath);
				}
				sb = populateXML(sb, file, displayDebugInfo);
				sb.append("</directory>");
			}
			else
			{
				sb.append("<file id=\"" + fPath + "\" modified=\"" + file.lastModified() + "\"/>");
				if(displayDebugInfo.equals(QPDisplayDebugInfo.displayDebugInfo))
				{
					System.out.println("-Added file: " + fPath);
				}
			}
		}
		return sb;
	}
	
	public static void main(String[] args) throws Exception 
	{
		System.out.println("Starting...");
		QPCreateXMLFileStructure.createXMLStructure("live", "test.xml", QPDisplayDebugInfo.displayDebugInfo);
		System.out.println("done");
	}
}
