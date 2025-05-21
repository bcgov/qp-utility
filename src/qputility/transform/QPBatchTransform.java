package qputility.transform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.transform.TransformerException;

import qputility.io.QPDirectory;
import qputility.io.QPFileIO;
import qputility.io.QPWriter;

public class QPBatchTransform
{

	public static void batchTransform(String xmlDirectory, String outputDirectory, String xsl, String extension)
	{
		System.out.println("Starting XML batch transform:");
		System.out.println("XSL= " + xsl);
		System.out.println("SourceDir= " + xmlDirectory);
		System.out.println("OutputDir= " + outputDirectory);
		System.out.println("Result extension= " + extension);
		System.out.println("________________________________");
		
		
		File xmlDir = new File(xmlDirectory);
		File[] files = xmlDir.listFiles();
		for(int i = 0; i < files.length; i++)
		{
			File file = files[i];
			if(file.isFile())
			{
				String fileName = QPFileIO.getFileName(file);
				String fileExtension = QPFileIO.getFileExtension(file);
				if(fileExtension.toLowerCase().equals(".xml"))
				{
					System.out.println();
					System.out.println();
					System.out.println("+++++++++++");
					System.out.println("Translating: " + files[i].toString());
					String output = "null";
					try
					{
						output = QPSaxonTransform.transform(xsl, files[i].toString());
					}
					catch (Exception e)
					{
						System.out.println("Error translating...");
						e.printStackTrace();
					}
					QPWriter writer = new QPWriter();
					try
					{
						writer.writeStringToFile(output, QPDirectory.validatePath(outputDirectory) + fileName + extension, false, true);
					}
					catch (IOException e)
					{
						System.out.println("Error writing output...");
						e.printStackTrace();
					}
				}
			}
		}
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("Finished XML batch transform.");
	}
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		
		QPBatchTransform.batchTransform("content\\xml", "content", "Content.xsl", ".htm");

	}

}
