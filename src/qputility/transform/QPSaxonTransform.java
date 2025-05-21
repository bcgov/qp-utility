package qputility.transform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class QPSaxonTransform 
{
	public static String transform(URL xslURL, URL xmlURL) throws TransformerException, IOException
	{
		return transform(new StreamSource(xslURL.openStream()), xmlURL.openStream(), null, null);
	}
	
	public static String transform(URL xslURL, InputStream xmlStream) throws TransformerException, IOException
	{
		return transform(new StreamSource(xslURL.openStream()), xmlStream, null, null);
	}
	
	public static String transform(URL xslURL, InputStream xmlStream, String paramName, String paramValue) throws TransformerException, IOException
	{
		return transform(new StreamSource(xslURL.openStream()), xmlStream, paramName, paramValue);
	}
	
	public static String transform(URL xslURL, URL xmlURL, String paramName, String paramValue) throws TransformerException, IOException
	{
		return transform(new StreamSource(xslURL.openStream()), xmlURL.openStream(), paramName, paramValue);
	}
	
	public static String transform(InputStream xslStream, InputStream xmlStream) throws TransformerException, IOException
	{
		return transform(new StreamSource(xslStream), xmlStream, null, null);
	}
	
	public static String transform(InputStream xslStream, InputStream xmlStream, String paramName, String paramValue) throws TransformerException, IOException
	{
		return transform(new StreamSource(xslStream), xmlStream, paramName, paramValue);
	}
	
	public static String transform(String xslPath, InputStream xmlStream) throws TransformerException, IOException
	{	
		try
		{
			return transform(new StreamSource("file:///" + xslPath.replace("\\", "/")), xmlStream, null, null);
		}
		catch (Exception e)
		{
			return transform(new StreamSource(xslPath.replace("\\", "/")), xmlStream, null, null);
		}
	}
	
	public static String transform(String xslPath, InputStream xmlStream, String paramName, String paramValue) throws TransformerException, IOException
	{	
		try
		{
			return transform(new StreamSource("file:///" + xslPath.replace("\\", "/")), xmlStream, paramName, paramValue);
		}
		catch (Exception e)
		{
			return transform(new StreamSource(xslPath.replace("\\", "/")), xmlStream, paramName, paramValue);
		}
	}
	
	public static String transform(InputStream xslStream, String xmlPath) throws FileNotFoundException, TransformerException, IOException
	{
		try
		{
			return transform(new StreamSource(xslStream), new FileInputStream(new File("file:///" + xmlPath.replace("\\", "/"))), null, null);
		}
		catch (Exception e)
		{
			return transform(new StreamSource(xslStream), new FileInputStream(new File(xmlPath.replace("\\", "/"))), null, null);
		}
	}
	
	public static String transform(InputStream xslStream, String xmlPath, String paramName, String paramValue) throws FileNotFoundException, TransformerException, IOException
	{
		try
		{
			return transform(new StreamSource(xslStream), new FileInputStream(new File("file:///" + xmlPath.replace("\\", "/"))), paramName, paramValue);
		}
		catch (Exception e)
		{
			return transform(new StreamSource(xslStream), new FileInputStream(new File(xmlPath.replace("\\", "/"))), paramName, paramValue);
		}
	}
	
	public static String transform(String xslPath, String xmlPath) throws FileNotFoundException, TransformerException, IOException
	{
		try
		{
			try
			{
				try
				{
					return transform(new StreamSource("file:///" + xslPath.replace("\\", "/")), new FileInputStream(new File("file:///" + xmlPath.replace("\\", "/"))), null, null);
				}
				catch (Exception e)
				{
					return transform(new StreamSource("file:///" + xslPath.replace("\\", "/")), new FileInputStream(new File(xmlPath.replace("\\", "/"))), null, null);
				}
			}
			catch (Exception e)
			{
				return transform(new StreamSource(xslPath.replace("\\", "/")), new FileInputStream(new File("file:///" + xmlPath.replace("\\", "/"))), null, null);
			}
		}
		catch (Exception e)
		{
			return transform(new StreamSource(xslPath.replace("\\", "/")), new FileInputStream(new File(xmlPath.replace("\\", "/"))), null, null);
		}
		
	}
	
	public static String transform(String xslPath, String xmlPath, String paramName, String paramValue) throws FileNotFoundException, TransformerException, IOException
	{
		try
		{
			try
			{
				try
				{
					return transform(new StreamSource("file:///" + xslPath.replace("\\", "/")), new FileInputStream(new File("file:///" + xmlPath.replace("\\", "/"))), paramName, paramValue);
				}
				catch (Exception e)
				{
					return transform(new StreamSource("file:///" + xslPath.replace("\\", "/")), new FileInputStream(new File(xmlPath.replace("\\", "/"))), paramName, paramValue);
				}
			}
			catch (Exception e)
			{
				return transform(new StreamSource(xslPath.replace("\\", "/")), new FileInputStream(new File("file:///" + xmlPath.replace("\\", "/"))), paramName, paramValue);
			}
		}
		catch (Exception e)
		{
			return transform(new StreamSource(xslPath.replace("\\", "/")), new FileInputStream(new File(xmlPath.replace("\\", "/"))), paramName, paramValue);
		}	
	}
	
	public static String transform(StreamSource xslStream, InputStream xmlStream) throws TransformerException, IOException
	{
		return transform(xslStream, xmlStream, null, null);
	}
	
	public static String transform(StreamSource xslStream, InputStream xmlStream, String paramName, String paramValue) throws TransformerException, IOException
	{
		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer(xslStream);
		
		if(paramName!=null)
		{
			transformer.setParameter(paramName, paramValue);
		}		
		StringWriter sw = new StringWriter();
		StreamResult out = new StreamResult(sw);
			
		transformer.transform(new StreamSource(xmlStream), out);
		
		String result = "";
		result = sw.toString();
		sw.close();
		xmlStream.close();
		
		return result;
	}
	
	/**
	 * Transform to the file system.
	 * @param args
	 * @throws FileNotFoundException 
	 * @throws TransformerException 
	 */
	public static void transform(InputStream xslStream, String inputXmlPath, String outputXmlPath, Map<String, String> params) 
			throws FileNotFoundException, TransformerException {
		
		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
		
		TransformerFactory tFactory = null;
		Transformer transformer = null;
		Writer w = null;
		
		try
		{
			tFactory = TransformerFactory.newInstance();
			transformer = tFactory.newTransformer(new StreamSource(xslStream));
			if(params!=null)
			{
				Iterator it = params.entrySet().iterator();
				while(it.hasNext()) {
					Map.Entry<String, String> pair = (Map.Entry<String, String>)it.next();
					transformer.setParameter(pair.getKey(), pair.getValue());
				}
			}
			
			w = new OutputStreamWriter(new FileOutputStream(new File(outputXmlPath)));
			
			StreamResult out = new StreamResult(w);
			transformer.transform(new StreamSource(new File(inputXmlPath)), out);
		
		} catch (FileNotFoundException e) {
			throw e;
		} catch (TransformerException e) {
			throw e;
		} finally {
			try {
				if(w!=null) w.close();
			} catch (Exception ignore) {}
		}
		
	}
	
	public static void main(String[] args) throws FileNotFoundException, TransformerException, IOException
	{
		QPSaxonTransform.transform(args[0], args[1]);
	}
}
