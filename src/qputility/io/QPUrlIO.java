package qputility.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class QPUrlIO 
{
	public static void saveURLResourceToFile(String urlString, String destFilePath) throws Exception 
	{
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try 
		{
			URL url = new URL(urlString);
			URLConnection urlc = url.openConnection();

			bis = new BufferedInputStream(urlc.getInputStream());
			bos = new BufferedOutputStream(new FileOutputStream(destFilePath));

			int i;
			while ((i = bis.read()) != -1) 
			{
				bos.write(i);
			}
		}
		catch (Exception e) 
		{
			throw new Exception(e);
		}
		finally 
		{
			if (bis != null) 
			{
				try 
				{
					bis.close();
				}
				catch (IOException ioe) 
				{}
			}
			if (bos != null) 
			{
				try 
				{
					bos.close();
				}
				catch (IOException ioe) 
				{}
			}
		}
	}
	
	public static byte[] getURLResource(String urlString) throws Exception
	{
		InputStream is = null;
		byte[] retVal = null;
		try 
		{
			URL url = new URL(urlString);
			is = url.openStream();
			DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
			
			retVal = new byte[dis.available()];
			dis.read(retVal, 0, dis.available());
		} 
		catch (Exception e) 
		{
			throw new Exception(e);			
		}
		finally
		{
			try 
			{
				is.close();
			} 
			catch (IOException e) 
			{}
		}
		return retVal;
	}
	
	public static Document getDocumentFromURL(URL url) throws ParserConfigurationException, SAXException, IOException
	{
		Document doc = null;
					
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
				
		db = dbf.newDocumentBuilder();
		doc = db.parse(url.openStream());
		doc.getDocumentElement().normalize();
		
		return doc;
	}
	
	public static byte[] getBytesFromURL(String urlString) throws IOException
	{
		return getBytesFromURL(new URL(urlString));
	}
	
	public static byte[] getBytesFromURL(URL url) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		
		String inputLine;
		while ((inputLine = in.readLine()) != null)
		{
			baos.write(inputLine.getBytes());
		}
		in.close();
		return baos.toByteArray();
	}	
}
