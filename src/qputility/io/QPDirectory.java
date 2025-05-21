package qputility.io;

import java.io.File;

/**
 * @author alex.strudwick
 *
 */
public class QPDirectory
{

	/**
	 * @param path Path to ensure that it ends in a file.seperator char
	 * @return Path with a file.seperator char
	 */
	public static String validatePath(String path)
	{
		if(!path.endsWith(System.getProperty("file.separator")))
		{
			return path + System.getProperty("file.separator");
		}
		else
		{
			return path;
		}
	}
	
	/**
	 * @param directoryPath Path to ensure that it does not end in a file.seperator char
	 * @return Path without a file.seperator char
	 */
	public static String removeTrailingSlash(String directoryPath)
	{
		if(directoryPath.endsWith(System.getProperty("file.separator")))
		{
			directoryPath = directoryPath.substring(0, directoryPath.length()-1);
		}
		return directoryPath;
	}
	
	public static String getDirectoryName(File file)
	{
		return file.getName();	
	}
	
	public static String getDirectoryName(String directoryLocation)
	{
		return getDirectoryName(new File(directoryLocation));
	}
	
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub

	}

}
