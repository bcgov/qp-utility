package qputility.io;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class QPReader {

	public static String[] readFileLineByLine(String filepath) throws IOException {
		DataInputStream in = null;
		FileInputStream fstream = null;
		BufferedReader br = null;
		ArrayList<String> ar = new ArrayList<String>();
		
		try
		{
		    // Open the file that is the first 
		    // command line parameter
			fstream = new FileInputStream(filepath);
		    // Get the object of DataInputStream
		    in = new DataInputStream(fstream);
		    br = new BufferedReader(new InputStreamReader(in));
		    String strLine;
		    //Read File Line By Line
		    while ((strLine = br.readLine()) != null)   {
		      // Print the content to the ArrayList.
		      ar.add(strLine);
		    }
		} catch (Exception e)	{//Catch exception if any
		      throw new IOException(e);
		} finally {
			// Close everything down.
	    	fstream.close();
	    	in.close();
	    	br.close();
	    }
		return ar.toArray(new String[ar.size()]);
	}
	
	public static String readFileToString(String filePath) throws IOException
	{
		return readFileToString(new File(filePath));
	}
	
	public static String readFileToString(File file) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		FileInputStream fin = new FileInputStream(file);
	
		BufferedInputStream bin = new BufferedInputStream(fin);
	
		byte[] contents = new byte[1024];
		int bytesRead=0;
	
		while((bytesRead = bin.read(contents)) != -1)
		{
			sb.append(new String(contents, 0, bytesRead));
		}
		
		fin.close();
		bin.close();
		fin = null;
		bin = null;
		
		return sb.toString();
	}
}
